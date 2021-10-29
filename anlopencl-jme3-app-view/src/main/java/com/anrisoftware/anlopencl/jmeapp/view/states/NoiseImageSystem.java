/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Core
 * ****************************************************************************
 *
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Core is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 */
package com.anrisoftware.anlopencl.jmeapp.view.states;

import static com.jme3.texture.Image.Format.RGBA8;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_WRITE;
import static org.lwjgl.opencl.CL10.clCreateBuffer;

import java.util.Map;

import javax.inject.Inject;

import org.eclipse.collections.impl.factory.Maps;
import org.lwjgl.system.MemoryStack;

import com.anrisoftware.anlopencl.jme.opencl.LwjglUtils;
import com.anrisoftware.anlopencl.jme.opencl.MappingRanges;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.anrisoftware.anlopencl.jmeapp.view.states.NoiseImageQuad.NoiseImageQuadFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.jme3.opencl.CommandQueue;
import com.jme3.opencl.Context;
import com.jme3.opencl.Image;
import com.jme3.opencl.Kernel;
import com.jme3.opencl.MemoryAccess;
import com.jme3.opencl.lwjgl.LwjglBuffer;
import com.jme3.opencl.lwjgl.LwjglContext;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;

import lombok.extern.slf4j.Slf4j;

/**
 * Updates the noise image.
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@Slf4j
public class NoiseImageSystem extends IntervalSystem {

    private static final Family FAMILY = Family.one(ImageComponent.class).get();

    private final Map<Entity, NoiseImageQuad> noiseImageQuads;

    private final ObservableGameMainPaneProperties gmpp;

    @Inject
    private NoiseImageQuadFactory noiseImageQuadFactory;

    @Inject
    private Node pivotNode;

    private final Context context;

    private Texture2D tex;

    private LwjglBuffer coord;

    private Image texCL;

    private final CommandQueue queue;

    private boolean imageBoundOpenCL;

    private final long clcontext;

    @Inject
    public NoiseImageSystem(GameMainPanePropertiesProvider onp, com.jme3.opencl.Context context) {
        super(0.33f);
        this.gmpp = onp.get();
        this.noiseImageQuads = Maps.mutable.empty();
        this.context = context;
        this.clcontext = ((LwjglContext) context).getContext();
        this.imageBoundOpenCL = false;
        this.queue = context.createQueue().register();
    }

    public void createTexture() {
        log.debug("createTexture");
        int width = gmpp.width.get();
        int height = gmpp.height.get();
        int dim = gmpp.dim.get();
        tex = new Texture2D(width, height, 1, RGBA8);
        try (var s = MemoryStack.stackPush()) {
            var err = s.mallocInt(1);
            long size = width * height * dim;
            coord = new LwjglBuffer(clCreateBuffer(clcontext, CL_MEM_READ_WRITE, size, err));
            LwjglUtils.checkCLError(err);
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(FAMILY, new EntityListener() {

            @Override
            public void entityRemoved(Entity entity) {
                removeNoiseImageQuad(entity);
            }

            @Override
            public void entityAdded(Entity entity) {
                createNoiseImageQuad(entity);
            }

        });
    }

    private void createNoiseImageQuad(Entity entity) {
        log.debug("createNoiseImageQuad {}", entity);
        var c = ImageComponent.m.get(entity);
        var quad = noiseImageQuadFactory.create(c, tex);
        noiseImageQuads.put(entity, quad);
        pivotNode.attachChild(quad.getPic());
    }

    private void removeNoiseImageQuad(Entity entity) {
        log.debug("removeNoiseImageQuad {}", entity);
        var quad = noiseImageQuads.remove(entity);
        pivotNode.attachChild(quad.getPic());
    }

    @Override
    protected void updateInterval() {
        if (!imageBoundOpenCL) {
            bindTextureToImage();
        }
        if (imageBoundOpenCL) {
            updateOpenCL();
        }
    }

    /**
     * Bind the texture to OpenCL after the texture was uploaded to OpenGL.
     */
    public void bindTextureToImage() {
        if (texCL != null) {
            texCL.release();
        }
        if (tex.getImage().getId() == -1) {
            return;
        }
        texCL = context.bindImage(tex, MemoryAccess.WRITE_ONLY).register();
        imageBoundOpenCL = true;
    }

    /**
     * Runs the kernel.
     */
    public void updateOpenCL() {
        texCL.acquireImageForSharingNoEvent(queue);
        runKernel();
        texCL.releaseImageForSharingNoEvent(queue);
    }

    private void runKernel() {
        try (var s = MemoryStack.stackPush()) {
            var ranges = MappingRanges.createWithBuffer(s);
            if (gmpp.map3d.get()) {
                ranges.setMap3D((float) gmpp.mapx0.get(), (float) gmpp.mapx1.get(), (float) gmpp.mapy0.get(),
                        (float) gmpp.mapy1.get(), (float) gmpp.mapz0.get(), (float) gmpp.mapz1.get());
            } else {
                ranges.setMap2D((float) gmpp.mapx0.get(), (float) gmpp.mapx1.get(), (float) gmpp.mapy0.get(),
                        (float) gmpp.mapy1.get());
            }
            var work = new Kernel.WorkSize(gmpp.width.get(), gmpp.height.get());
            float z = (float) gmpp.z.get();
            gmpp.kernel.get().run1NoEvent(queue, work, ranges.getClBuffer(s, clcontext), z, coord, texCL);
        }
    }
}
