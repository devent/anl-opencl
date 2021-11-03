/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Model
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
 * ANL-OpenCL :: JME3 - App - Model is a derivative work based on Josua Tippetts' C++ library:
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
package com.anrisoftware.anlopencl.jmeapp.actors;

import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jme.opencl.AnlKernel.AnlKernelFactory;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFailedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.opencl.lwjgl.LwjglContext;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class OpenclBuildActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            OpenclBuildActor.class.getSimpleName());

    public static final String NAME = OpenclBuildActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    public interface OpenclBuildActorFactory {

        OpenclBuildActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {

        public final com.jme3.opencl.Context context;

    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupErrorMessage extends Message {

        public final Throwable cause;

    }

    public static Behavior<Message> create(Injector injector) {
        return Behaviors.withStash(100, stash -> Behaviors.setup((context) -> {
            createClContext(injector, context);
            return injector.getInstance(OpenclBuildActorFactory.class).create(context, stash).start();
        }));
    }

    private static void createClContext(Injector injector, ActorContext<Message> context) {
        context.pipeToSelf(createClContext0(injector), (result, cause) -> {
            if (cause == null) {
                return new InitialStateMessage(result);
            } else {
                return new SetupErrorMessage(cause);
            }
        });
    }

    private static CompletionStage<com.jme3.opencl.Context> createClContext0(Injector injector) {
        return CompletableFuture.completedStage(injector.getInstance(com.jme3.opencl.Context.class));
    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
    }

    @Inject
    @Assisted
    protected ActorContext<Message> context;

    @Inject
    @Assisted
    private StashBuffer<Message> buffer;

    @Inject
    private GameMainPanePropertiesProvider gmpp;

    @Inject
    private AnlKernelFactory anlKernelFactory;

    public Behavior<Message> start() {
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.debug("stashOtherCommand: {}", m);
        buffer.stash(m);
        return Behaviors.same();
    }

    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        gmpp.get().kernel.set(anlKernelFactory.create((LwjglContext) m.context));
        return Behaviors.receive(Message.class)//
                .onMessage(BuildStartMessage.class, this::onBuildStart)//
                .build();
    }

    private Behavior<Message> onBuildStart(BuildStartMessage m) {
        log.debug("onBuildStart: {}", m);
        try {
            var kernel = gmpp.get().kernel.get();
            if (!kernel.isBuildLibFinish()) {
                kernel.buildLib();
            }
            if (!kernel.isCompileFinish()) {
                kernel.compileKernel(gmpp.get().kernelCode.get());
            }
            String name = gmpp.get().kernelName.get();
            if (!kernel.isCreatedKernel(name)) {
                kernel.createKernel(name);
            }
            m.ref.tell(new BuildFinishedMessage());
        } catch (Exception e) {
            m.ref.tell(new BuildFailedMessage(e));
            log.error("Error build kernel library", e);
        }
        return Behaviors.same();
    }

}
