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

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

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
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.jme3.opencl.lwjgl.LwjglBuffer;
import com.jme3.opencl.lwjgl.LwjglContext;
import com.jme3.texture.Image.Format;
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

    private final NoiseImageEntities noiseImageEntities;

    @Assisted
    @Inject
    private ActorContext<Message> context;

    @Assisted
    @Inject
    private StashBuffer<Message> buffer;

    @Inject
    private ActorSystemProvider actor;

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
    public ViewActor(com.jme3.opencl.Context openclContext, NoiseImageEntities noiseImageEntities) {
        this.clContext = (LwjglContext) openclContext;
        this.noiseImageEntities = noiseImageEntities;
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
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.debug("stashOtherCommand {}", m);
        buffer.stash(m);
        return Behaviors.same();
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
            noiseImageEntities.set(gmpp.get().columns.get(), gmpp.get().rows.get());
        });
        gmpp.get().columns.addListener((observable, oldValue, newValue) -> {
            app.enqueue(() -> {
                noiseImageEntities.set(newValue.intValue(), gmpp.get().rows.get());
            });
        });
        gmpp.get().rows.addListener((observable, oldValue, newValue) -> {
            app.enqueue(() -> {
                noiseImageEntities.set(gmpp.get().columns.get(), newValue.intValue());
            });
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
        var entities = noiseImageEntities.getEntities();
        int ncols = entities.size();
        int nrows = entities.get(0).size();
        float xx = (gmp.mapx1.get() - gmp.mapx0.get()) / (float) ncols;
        float yy = (gmp.mapy1.get() - gmp.mapy0.get()) / (float) nrows;
        float x0 = gmp.mapx0.get();
        float x1 = gmp.mapx1.get();
        float y0 = gmp.mapy0.get();
        float y1 = gmp.mapy1.get();
        float z0 = gmp.mapz0.get();
        float z1 = gmp.mapz1.get();
        for (var rows : entities) {
            for (var entity : rows) {
                if (KernelComponent.m.has(entity)) {
                    var kc = entity.remove(KernelComponent.class);
                    kc.tex.getImage().dispose();
                    kc.ranges.release();
                }
                var ic = ImageComponent.m.get(entity);
                try (var s = MemoryStack.stackPush()) {
                    int width = gmp.width.get();
                    int height = gmp.height.get();
                    var tex = new Texture2D(width, height, 1, Format.RGBA32F);
                    var ranges = MappingRanges.createWithBuffer(s);
                    if (gmp.map3d.get()) {
                        setMap3D(ranges, ncols, nrows, ic.column, ic.row, x0, x1, y0, y1, z0, z1, xx, yy);
                    } else {
                        setMap2D(ranges, ncols, nrows, ic.column, ic.row, x0, x1, y0, y1, xx, yy);
                    }
                    var rangesb = new LwjglBuffer(ranges.getClBuffer(s, clContext.getContext()));
                    entity.add(new KernelComponent(tex, rangesb));
                }
            }
        }
    }

    private void setMap2D(MappingRanges ranges, int cols, int rows, int c, int r, float x0, float x1, float y0,
            float y1, float xx, float yy) {
        float mx0 = x0 + (xx * c);
        float mx1 = x1 - ((cols - c - 1) * xx);
        float my0 = y0 + (yy * r);
        float my1 = y1 - ((rows - r - 1) * yy);
        ranges.setMap2D(mx0, mx1, my0, my1);
    }

    private void setMap3D(MappingRanges ranges, int cols, int rows, int c, int r, float x0, float x1, float y0,
            float y1, float z0, float z1, float xx, float yy) {
        float mx0 = x0 + (xx * c);
        float mx1 = x1 - ((cols - c - 1) * xx);
        float my0 = y0 + (yy * r);
        float my1 = y1 - ((rows - r - 1) * yy);
        ranges.setMap3D(mx0, mx1, my0, my1, z0, z1);
    }

}
