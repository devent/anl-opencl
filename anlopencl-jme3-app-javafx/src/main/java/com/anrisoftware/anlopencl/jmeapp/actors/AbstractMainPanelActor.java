/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX
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
 * ANL-OpenCL :: JME3 - App - JavaFX is a derivative work based on Josua Tippetts' C++ library:
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
 * ANL-OpenCL :: JME3 - App - JavaFX bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl.jmeapp.actors;

import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.runFxThread;
import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.anlopencl.jmeapp.components.PanelComponent;
import com.anrisoftware.anlopencl.jmeapp.controllers.PanelControllerBuild.PanelControllerInitializeFxBuild;
import com.anrisoftware.anlopencl.jmeapp.controllers.PanelControllerBuild.PanelControllerResult;
import com.anrisoftware.anlopencl.jmeapp.messages.AttachGuiMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.GameQuitMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MainWindowResizedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.ShutdownMessage;
import com.anrisoftware.anlopencl.jmeapp.states.MainPanelState;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jayfella.jme.jfx.JavaFxUI;
import com.jme3.app.Application;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Main panel actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public abstract class AbstractMainPanelActor {

    @RequiredArgsConstructor
    @ToString
    private static class SetupUiErrorMessage extends Message {

        public final Throwable cause;

    }

    public static Behavior<Message> create(Injector injector,
            Class<? extends AbstractMainPanelActorFactory> mainPanelActorFactoryType, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, String... additionalCss) {
        return Behaviors.withStash(100, stash -> Behaviors.setup((context) -> {
            startJavafxBuild(injector, context, mainUiResource, panelActors, additionalCss);
            return injector.getInstance(mainPanelActorFactoryType).create(context, stash).start(injector);
        }));
    }

    private static void startJavafxBuild(Injector injector, ActorContext<Message> context, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, String... additionalCss) {
        var build = injector.getInstance(PanelControllerInitializeFxBuild.class);
        context.pipeToSelf(build.loadFxml(context.getExecutionContext(), mainUiResource, additionalCss),
                (result, cause) -> {
                    if (cause == null) {
                        var actors = spawnPanelActors(injector, context, panelActors, result);
                        return new InitialStateMessage(result.controller, result.root, actors);
                    } else {
                        return new SetupUiErrorMessage(cause);
                    }
                });
    }

    private static ImmutableMap<String, ActorRef<Message>> spawnPanelActors(Injector injector,
            ActorContext<Message> context, Map<String, PanelActorCreator> panelActors, PanelControllerResult result) {
        MutableMap<String, ActorRef<Message>> actors = Maps.mutable.empty();
        panelActors.forEach((name, a) -> {
            var actor = context.spawn(a.create(injector), name);
            actors.put(name, actor);
        });
        return actors.toImmutable();
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout, int id,
            ServiceKey<Message> key, String name,
            Class<? extends AbstractMainPanelActorFactory> mainPanelActorFactoryType, String mainUiResource,
            Map<String, PanelActorCreator> panelActors, String... additionalCss) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, id, key, name,
                create(injector, mainPanelActorFactoryType, mainUiResource, panelActors, additionalCss));
    }

    @Inject
    @Assisted
    protected ActorContext<Message> context;

    @Inject
    @Assisted
    private StashBuffer<Message> buffer;

    @Inject
    protected Application app;

    @Inject
    private MainPanelState mainPanelState;

    @Inject
    protected Engine engine;

    protected InitialStateMessage initial;

    protected Injector injector;

    public Behavior<Message> start(Injector injector) {
        this.injector = injector;
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(SetupUiErrorMessage.class, this::onSetupUiError)//
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    /**
     * Unstash all messages in the buffer.
     */
    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        this.initial = m;
        initial.actors.forEachValue((a) -> a.tell(m));
        return buffer.unstashAll(Behaviors.receive(Message.class)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(AttachGuiMessage.class, this::onAttachGui)//
                .build());
    }

    /**
     * Throws database error.
     */
    @SneakyThrows
    private Behavior<Message> onSetupUiError(SetupUiErrorMessage m) {
        throw m.cause;
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.debug("stashOtherCommand: {}", m);
        buffer.stash(m);
        return Behaviors.same();
    }

    private Behavior<Message> onShutdown(ShutdownMessage m) {
        log.debug("onShutdown {}", m);
        Platform.exit();
        return Behaviors.stopped();
    }

    private Behavior<Message> onAttachGui(AttachGuiMessage m) {
        log.debug("onAttachGui {}", m);
        runFxThread(() -> {
            setupUi();
            initial.actors.forEachValue((a) -> a.tell(m));
        });
        app.enqueue(() -> {
            app.getStateManager().attach(mainPanelState);
            var entity = engine.createEntity();
            entity.add(new PanelComponent(context.getSelf()));
            engine.addEntity(entity);
        });
        m.replyTo.tell(new AttachGuiFinishedMessage());
        return getBehaviorAfterAttachGui().build();
    }

    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        return Behaviors.receive(Message.class)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(GameQuitMessage.class, this::onGameQuit)//
                .onMessage(MainWindowResizedMessage.class, this::onMainWindowResized)//
        ;
    }

    private void setupUi() {
        var pane = initial.root;
        pane.setPrefSize(app.getCamera().getWidth(), app.getCamera().getHeight());
        if (JavaFxUI.getInstance() != null) {
            JavaFxUI.getInstance().attachChild(pane);
        }
    }

    private Behavior<Message> onGameQuit(GameQuitMessage m) {
        log.debug("onGameQuit {}", m);
        app.enqueue(() -> app.stop());
        return Behaviors.same();
    }

    private Behavior<Message> onMainWindowResized(MainWindowResizedMessage m) {
        log.debug("onMainWindowResized {}", m);
        runFxThread(() -> {
            initial.root.setPrefSize(m.width, m.height);
        });
        return Behaviors.same();
    }

}
