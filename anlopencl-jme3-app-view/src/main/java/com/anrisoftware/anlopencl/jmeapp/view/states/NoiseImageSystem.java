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

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.collections.impl.factory.Maps;
import org.lwjgl.system.MemoryStack;

import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.anrisoftware.anlopencl.jmeapp.view.components.KernelComponent;
import com.anrisoftware.anlopencl.jmeapp.view.states.NoiseImageQuad.NoiseImageQuadFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.jme3.opencl.CommandQueue;
import com.jme3.opencl.Kernel;
import com.jme3.scene.Node;

import lombok.extern.slf4j.Slf4j;

/**
 * Updates the noise image.
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@Slf4j
public class NoiseImageSystem extends IntervalIteratingSystem {

    private static final Family FAMILY = Family.one(ImageComponent.class).get();

    private final Map<Entity, NoiseImageQuad> noiseImageQuads;

    private final ObservableGameMainPaneProperties gmpp;

    @Inject
    private NoiseImageQuadFactory noiseImageQuadFactory;

    @Inject
    @Named("pivotNode")
    private Node pivotNode;

    private final CommandQueue queue;

    @Inject
    public NoiseImageSystem(GameMainPanePropertiesProvider onp, com.jme3.opencl.Context context) {
        super(FAMILY, 1f);
        this.gmpp = onp.get();
        this.noiseImageQuads = Maps.mutable.empty();
        this.queue = context.createQueue().register();
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
        var quad = noiseImageQuadFactory.create(c);
        noiseImageQuads.put(entity, quad);
        pivotNode.attachChild(quad.getPic());
        entity.componentRemoved.add((signal, object) -> {
            if (!KernelComponent.m.has(object)) {
                quad.setNotSetTexture(true);
            }
        });
    }

    private void removeNoiseImageQuad(Entity entity) {
        log.debug("removeNoiseImageQuad {}", entity);
        var quad = noiseImageQuads.remove(entity);
        pivotNode.detachChild(quad.getPic());
    }

    @Override
    protected void processEntity(Entity entity) {
        if (KernelComponent.m.has(entity)) {
            var kc = KernelComponent.m.get(entity);
            var imageQuad = noiseImageQuads.get(entity);
            if (!imageQuad.isTextureSet()) {
                imageQuad.setTex(kc.tex);
            }
            if (imageQuad.isTextureUploaded() && !imageQuad.isImageBoundOpenCL()) {
                imageQuad.bindTextureToImage();
            }
            if (imageQuad.isImageBoundOpenCL() && !kc.kernelRun) {
                runKernel(kc, imageQuad);
                kc.kernelRun = true;
            }
        }
    }

    private void runKernel(KernelComponent kc, NoiseImageQuad imageQuad) {
        try (var s = MemoryStack.stackPush()) {
            var work = new Kernel.WorkSize(gmpp.width.get(), gmpp.height.get());
            String name = gmpp.kernelName.get();
            try {
                log.trace("acquiring image for sharing");
                imageQuad.getTexCL().acquireImageForSharingNoEvent(queue);
                log.trace("running kernel");
                gmpp.kernel.get().run1NoEvent(name, queue, work, kc.ranges, imageQuad.getTexCL());
                log.trace("releasing image for sharing");
                imageQuad.getTexCL().releaseImageForSharingNoEvent(queue);
            } catch (Exception e) {
                log.error("Error run kernel", e);
            }
        }
    }

}
