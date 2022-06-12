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
package com.anrisoftware.anlopencl.jmeapp.view.actors;

import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;
import static com.jme3.texture.Image.Format.RGBA8;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.impl.factory.Maps;
import org.lwjgl.system.MemoryStack;

import com.anrisoftware.anlopencl.jme.opencl.MappingRanges;
import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProvider;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.ResetCameraMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.anrisoftware.anlopencl.jmeapp.view.components.KernelComponent;
import com.anrisoftware.anlopencl.jmeapp.view.messages.AttachViewAppStateDoneMessage;
import com.anrisoftware.anlopencl.jmeapp.view.states.CameraPanningAppState;
import com.anrisoftware.anlopencl.jmeapp.view.states.ViewAppState;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.jme3.opencl.lwjgl.LwjglBuffer;
import com.jme3.opencl.lwjgl.LwjglContext;
import com.jme3.texture.Texture2D;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.extern.slf4j.Slf4j;

/**
 * Attaches the {@link ViewAppState} to the application.
 *
 * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
 */
@Slf4j
public class ViewActor {

    /**
     * Factory to create the {@link ViewActor}.
     *
     * @author Erwin Müller
     */
    public interface ViewActorFactory {

        ViewActor create(StashBuffer<Message> stash, ActorContext<Message> context);
    }

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class, ViewActor.class.getSimpleName());

    public static final String NAME = ViewActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Creates the behavior of the {@link ViewActor}.
     *
     * @param injector the {@link Injector}.
     * @return the {@link Behavior} of the {@link ViewActor}.
     */
    public static Behavior<Message> create(Injector injector) {
        return Behaviors.withStash(100, stash -> Behaviors.setup((context) -> {
            return injector.getInstance(ViewActorFactory.class).create(stash, context).start();
        }));
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, ViewActor.create(injector));
    }

    private final LwjglContext clContext;

    private final Map<String, Entity> noiseImageEntities;

    @Assisted
    @Inject
    private ActorContext<Message> context;

    @Assisted
    @Inject
    private StashBuffer<Message> buffer;

    @Inject
    private GameMainPanePropertiesProvider gmpp;

    @Inject
    private Application app;

    @Inject
    private ViewAppState viewAppState;

    @Inject
    private Engine engine;

    @Inject
    private CameraPanningAppState cameraPanningAppState;

    @Inject
    public ViewActor(com.jme3.opencl.Context openclContext) {
        this.clContext = (LwjglContext) openclContext;
        this.noiseImageEntities = Maps.mutable.empty();
    }

    /**
     * Attaches the {@link ViewAppState}. Returns a new behavior that responds to:
     * <ul>
     * <li>{@link AttachViewAppStateDoneMessage}
     * </ul>
     */
    public Behavior<Message> start() {
        app.enqueue(() -> {
            viewAppState.setActor(context.getSelf());
            if (!app.getStateManager().hasState(viewAppState.getId())) {
                app.getStateManager().attach(viewAppState);
            }
            if (!app.getStateManager().hasState(cameraPanningAppState.getId())) {
                app.getStateManager().attach(cameraPanningAppState);
            }
        });
        return Behaviors.receive(Message.class)//
                .onMessage(AttachViewAppStateDoneMessage.class, this::onAttachViewAppStateDone)//
                .onMessage(Message.class, (m) -> {
                    log.debug("stashOtherCommand: {}", m);
                    buffer.stash(m);
                    return Behaviors.same();
                })//
                .build();
    }

    /**
     * Unstash all messages in the buffer. Returns a new behavior that responds to:
     * <ul>
     * <li>{@link ResetCameraMessage}
     * <li>{@link BuildFinishedMessage}
     * </ul>
     */
    private Behavior<Message> onAttachViewAppStateDone(AttachViewAppStateDoneMessage m) {
        log.debug("onAttachViewAppStateDone {}", m);
        app.enqueue(() -> {
            setupCamera();
            var entity = engine.createEntity().add(new ImageComponent(10, 10));
            noiseImageEntities.put(gmpp.get().kernelName.get(), entity);
            engine.addEntity(entity);
        });
        return buffer.unstashAll(Behaviors.receive(Message.class)//
                .onMessage(ResetCameraMessage.class, this::onResetCamera)//
                .onMessage(BuildFinishedMessage.class, this::onBuildFinished)//
                .build());
    }

    private void setupCamera() {
        app.getCamera().setLocation(gmpp.get().getCameraPos());
        app.getCamera().setRotation(gmpp.get().getCameraRot());
    }

    private Behavior<Message> onResetCamera(ResetCameraMessage m) {
        log.debug("onResetCamera {}", m);
        app.enqueue(() -> {
            cameraPanningAppState.resetCamera();
        });
        return Behaviors.same();
    }

    private Behavior<Message> onBuildFinished(BuildFinishedMessage m) {
        log.debug("onBuildFinished {}", m);
        app.enqueue(() -> {
            updateTexture();
        });
        return Behaviors.same();
    }

    private void updateTexture() {
        log.debug("updateTexture");
        var gmp = gmpp.get();
        var entity = noiseImageEntities.get(gmp.kernelName.get());
        if (KernelComponent.m.has(entity)) {
            var kc = entity.remove(KernelComponent.class);
            kc.tex.getImage().dispose();
            kc.ranges.release();
        }
        try (var s = MemoryStack.stackPush()) {
            int width = gmp.width.get();
            int height = gmp.height.get();
            var tex = new Texture2D(width, height, 1, RGBA8);
            var ranges = MappingRanges.createWithBuffer(s);
            if (gmp.map3d.get()) {
                setMap3D(ranges);
            } else {
                setMap2D(ranges);
            }
            var rangesb = new LwjglBuffer(ranges.getClBuffer(s, clContext.getContext()));
            entity.add(new KernelComponent(tex, rangesb));
        }
    }

    private void setMap2D(MappingRanges ranges) {
        var gmp = gmpp.get();
        ranges.setMap2D(gmp.mapx0.get(), gmp.mapx1.get(), gmp.mapy0.get(), gmp.mapy1.get());
    }

    private void setMap3D(MappingRanges ranges) {
        var gmp = gmpp.get();
        ranges.setMap3D(gmp.mapx0.get(), gmp.mapx1.get(), gmp.mapy0.get(), gmp.mapy1.get(), gmp.mapz0.get(),
                gmp.mapz1.get());
    }

}
