/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View
 * ****************************************************************************
 *
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
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
 * ANL-OpenCL :: JME3 - App - View is a derivative work based on Josua Tippetts' C++ library:
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
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View bundles and uses the RandomCL library:
 * https://github.com/bstatcomp/RandomCL
 * ****************************************************************************
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.anrisoftware.anlopencl.jmeapp.view.states;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.collections.impl.factory.Maps;
import org.lwjgl.system.MemoryStack;

import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProvider;
import com.anrisoftware.anlopencl.jmeapp.messages.KernelStartedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.KernelStartedMessage.KernelFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.anrisoftware.anlopencl.jmeapp.view.components.ImageQuadComponent;
import com.anrisoftware.anlopencl.jmeapp.view.components.KernelTexComponent;
import com.anrisoftware.anlopencl.jmeapp.view.states.NoiseImageQuad.NoiseImageQuadFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.opencl.CommandQueue;
import com.jme3.opencl.Kernel;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.util.TempVars;

import lombok.extern.slf4j.Slf4j;

/**
 * Updates the noise image.
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
@Slf4j
public class NoiseImageSystem extends IntervalIteratingSystem {

    private static final Family FAMILY = Family.one(ImageQuadComponent.class).get();

    private final Map<Entity, NoiseImageQuad> noiseImageQuads;

    private final ObservableGameMainPaneProperties gmpp;

    @Inject
    private NoiseImageQuadFactory noiseImageQuadFactory;

    @Inject
    @Named("pivotNode")
    private Node pivotNode;

    @Inject
    private ActorSystemProvider actor;

    private final Node imagesNode;

    private final CommandQueue queue;

    private final Vector3f imageNodeBound;

    @Inject
    public NoiseImageSystem(GameMainPanePropertiesProvider onp, com.jme3.opencl.Context context) {
        super(FAMILY, 1f);
        this.gmpp = onp.get();
        this.noiseImageQuads = Maps.mutable.empty();
        this.queue = context.createQueue().register();
        this.imagesNode = new Node("imagesNode");
        this.imageNodeBound = new Vector3f();
        imagesNode.getWorldBound().getCenter(imageNodeBound);
    }

    /**
     * Returns the top right and bottom left of the noise quad in screen
     * coordinates.
     */
    public void getScreenCoordinatesNoiseImage(Camera camera, Vector3f topRight, Vector3f bottomLeft) {
        var temp = TempVars.get();
        try {
            var bb = (BoundingBox) imagesNode.getWorldBound();
            var btr = bb.getMax(temp.vect1);
            var bbl = bb.getMin(temp.vect2);
            camera.getScreenCoordinates(btr, topRight);
            camera.getScreenCoordinates(bbl, bottomLeft);
        } finally {
            temp.release();
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        pivotNode.attachChild(imagesNode);
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
        var c = ImageQuadComponent.m.get(entity);
        var quad = noiseImageQuadFactory.create(c);
        noiseImageQuads.put(entity, quad);
        imagesNode.setLocalTranslation(0, 0, 0);
        imagesNode.attachChild(quad.getPic());
        imagesNode.getWorldBound().getCenter(imageNodeBound);
        imagesNode.setLocalTranslation(-imageNodeBound.x, -imageNodeBound.y, -imageNodeBound.z);
        entity.componentRemoved.add((signal, object) -> {
            if (!KernelTexComponent.m.has(object)) {
                quad.setNotSetTexture(true);
            }
        });
    }

    private void removeNoiseImageQuad(Entity entity) {
        log.debug("removeNoiseImageQuad {}", entity);
        var quad = noiseImageQuads.remove(entity);
        imagesNode.setLocalTranslation(0, 0, 0);
        imagesNode.detachChild(quad.getPic());
        var bound = imagesNode.getWorldBound().getCenter();
        imagesNode.setLocalTranslation(-bound.x, -bound.y, -bound.z);
    }

    @Override
    protected void processEntity(Entity entity) {
        if (KernelTexComponent.m.has(entity)) {
            var ic = ImageQuadComponent.m.get(entity);
            var kc = KernelTexComponent.m.get(entity);
            var imageQuad = noiseImageQuads.get(entity);
            if (!imageQuad.isTextureSet()) {
                imageQuad.setTex(kc.tex);
            }
            if (imageQuad.isTextureUploaded() && !imageQuad.isImageBoundOpenCL()) {
                imageQuad.bindTextureToImage();
            }
            if (imageQuad.isImageBoundOpenCL() && !kc.kernelRun) {
                runKernel(ic, kc, imageQuad);
                kc.kernelRun = true;
            }
        }
    }

    private void runKernel(ImageQuadComponent ic, KernelTexComponent kc, NoiseImageQuad quad) {
        actor.get().tell(new KernelStartedMessage(ic.column, ic.row));
        try (var s = MemoryStack.stackPush()) {
            var work = new Kernel.WorkSize(gmpp.width.get(), gmpp.height.get());
            var name = gmpp.kernelName.get();
            var seed = gmpp.seed.get();
            try {
                log.trace("acquiring image for sharing");
                quad.getTexCL().acquireImageForSharingNoEvent(queue);
                log.trace("running kernel");
                gmpp.kernel.get().run1NoEvent(name, queue, work, kc.ranges, quad.getTexCL(), seed);
                log.trace("releasing image for sharing");
                quad.getTexCL().releaseImageForSharingNoEvent(queue);
                actor.get().tell(new KernelFinishedMessage(ic.column, ic.row));
            } catch (Exception e) {
                log.error("Error run kernel", e);
            }
        }
    }

}
