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

import static com.anrisoftware.anlopencl.jmeapp.actors.AdditionalCss.ADDITIONAL_CSS;
import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.anlopencl.jmeapp.controllers.PanelControllerBuild;
import com.anrisoftware.anlopencl.jmeapp.controllers.PanelControllerBuild.PanelControllerResult;
import com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import javafx.scene.layout.Region;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract actor that loads a pane from a FXML file.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class AbstractJavafxPaneActor<ControllerType> {

    /**
     * Message that is send after the pane was successfully loaded from the FXML
     * file.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @RequiredArgsConstructor
    @ToString(callSuper = true)
    protected static class JavafxPaneInitialStateMessage<ControllerType> extends Message {

        public final ActorContext<Message> context;

        public final Region root;

        public final ControllerType controller;
    }

    /**
     * Message that is send if there was some error loading the pane from the FXML
     * file.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @RequiredArgsConstructor
    @ToString(callSuper = true)
    protected static class JavafxPaneErrorSetupControllerMessage extends Message {

        public final ActorContext<Message> context;

        public final Throwable cause;
    }

    /**
     * Factory to create the {@link AbstractJavafxPaneActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface AbstractJavafxPaneActorFactory {

        @SuppressWarnings("rawtypes")
        AbstractJavafxPaneActor create(ActorContext<Message> context, StashBuffer<Message> buffer);
    }

    /**
     * Creates a new named actor that loads a FXML pane from a file.
     *
     * @param timeout
     * @param id
     * @param key
     * @param name
     * @param injector
     * @param paneFxml
     *
     * @see CreateActorMessage
     *
     * @return
     */
    public static CompletionStage<ActorRef<Message>> createWithPane(Duration timeout, int id, ServiceKey<Message> key,
            String name, Injector injector, AbstractJavafxPaneActorFactory paneActorFactory, String paneFxml) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, id, key, name,
                createWithPane(injector, paneActorFactory, key, paneFxml));
    }

    @SuppressWarnings("unchecked")
    private static <ControllerType> Behavior<Message> createWithPane(Injector injector,
            AbstractJavafxPaneActorFactory paneActorFactory, ServiceKey<Message> key, String paneFxml) {
        return Behaviors.withStash(100, stash -> Behaviors.setup((context) -> {
            context.pipeToSelf(loadPanel(injector, context, paneFxml), (value, cause) -> {
                if (cause == null) {
                    return new JavafxPaneInitialStateMessage<>(context, value.root, (ControllerType) value.controller);
                } else {
                    return new JavafxPaneErrorSetupControllerMessage(context, cause);
                }
            });
            context.getSystem().receptionist().tell(Receptionist.register(key, context.getSelf()));
            return paneActorFactory.create(context, stash).start();
        }));
    }

    private static CompletableFuture<PanelControllerResult> loadPanel(Injector injector, ActorContext<Message> context,
            String paneFxml) {
        var build = injector.getInstance(PanelControllerBuild.class);
        return build.loadFxml(context.getExecutionContext(), paneFxml, ADDITIONAL_CSS);
    }

    protected final ActorContext<Message> context;

    protected final StashBuffer<Message> buffer;

    protected ControllerType controller;

    protected Region pane;

    protected AbstractJavafxPaneActor(ActorContext<Message> context, StashBuffer<Message> buffer) {
        this.context = context;
        this.buffer = buffer;
    }

    public final Behavior<Message> start() {
        return getStartBehaviorBuilder().build();
    }

    protected BehaviorBuilder<Message> getStartBehaviorBuilder() {
        return Behaviors.receive(Message.class)//
                .onMessage(JavafxPaneInitialStateMessage.class, this::onInitialState)//
                .onMessage(JavafxPaneErrorSetupControllerMessage.class, this::onErrorState)//
                .onMessage(Message.class, this::stashOtherCommand)//
        ;
    }

    private Behavior<Message> stashOtherCommand(Object m) {
        var mm = (Message) m;
        log.debug("stashOtherCommand {}", mm);
        buffer.stash(mm);
        return Behaviors.same();
    }

    private Behavior<Message> onInitialState(JavafxPaneInitialStateMessage<ControllerType> m) {
        log.debug("onInitialState {}", m);
        return buffer.unstashAll(active(m));
    }

    private Behavior<Message> active(JavafxPaneInitialStateMessage<ControllerType> m) {
        log.debug("activate {}", m);
        this.controller = m.controller;
        this.pane = m.root;
        return doActivate(m).build();
    }

    protected BehaviorBuilder<Message> doActivate(JavafxPaneInitialStateMessage<ControllerType> m) {
        return Behaviors.receive(Message.class);
    }

    private Behavior<Message> onErrorState(Object m) {
        var mm = (JavafxPaneErrorSetupControllerMessage) m;
        log.debug("onErrorState {}", mm);
        log.error("Error setup controller", mm.cause);
        return Behaviors.stopped();
    }

}
