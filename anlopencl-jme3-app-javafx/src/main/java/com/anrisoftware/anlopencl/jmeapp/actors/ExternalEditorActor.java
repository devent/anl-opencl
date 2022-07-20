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
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.anrisoftware.anlopencl.jmeapp.actors.ExternalEditorBuild.ExternalEditor;
import com.anrisoftware.anlopencl.jmeapp.actors.FileWatcher.FileEvent;
import com.anrisoftware.anlopencl.jmeapp.actors.FileWatcher.FileListener;
import com.anrisoftware.anlopencl.jmeapp.actors.FileWatcher.FileWatcherFactory;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.OpenExternalEditorMessage.ErrorReadKernelCodeMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.OpenExternalEditorMessage.ExternalEditorClosedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.OpenExternalEditorMessage.ExternalEditorOpenErrorMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.ShutdownMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

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
 * Opens the external editor and watches for changes in the temporary file.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class ExternalEditorActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ExternalEditorActor.class.getSimpleName());

    public static final String NAME = ExternalEditorActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Factory to create the {@link ExternalEditorActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ExternalEditorActorFactory {

        ExternalEditorActor create(ActorContext<Message> context, StashBuffer<Message> stash);
    }

    /**
     * Message send after the external editor was opened.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class InitialStateMessage extends Message {

        public final ExternalEditor externalEditor;

    }

    public static Behavior<Message> create(Injector injector) {
        return Behaviors.withStash(100, stash -> Behaviors.setup((context) -> {
            openExternalEditor(injector, context);
            return injector.getInstance(ExternalEditorActorFactory.class).create(context, stash).start(injector);
        }));
    }

    private static void openExternalEditor(Injector injector, ActorContext<Message> context) {
        context.pipeToSelf(openExternalEditor0(injector, context), (result, cause) -> {
            if (cause == null) {
                return new InitialStateMessage(result);
            } else {
                return new ExternalEditorOpenErrorMessage(cause);
            }
        });
    }

    private static CompletionStage<ExternalEditor> openExternalEditor0(Injector injector,
            ActorContext<Message> context) {
        return injector.getInstance(ExternalEditorBuild.class).startEditor(context.getExecutionContext(), (file) -> {
            context.getSelf().tell(new ExternalEditorClosedMessage(file));
        });
    }

    public static CompletionStage<ActorRef<Message>> create(Duration timeout, Injector injector) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
    }

    @Inject
    @Assisted
    private ActorContext<Message> context;

    @Inject
    @Assisted
    private StashBuffer<Message> buffer;

    @Inject
    private FileWatcherFactory fileWatcherFactory;

    @Inject
    private GameMainPanePropertiesProvider gmppp;

    @Inject
    private ActorSystemProvider actor;

    private InitialStateMessage initial;

    public Behavior<Message> start(Injector injector) {
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(ExternalEditorOpenErrorMessage.class, this::onExternalEditorOpenError)//
                .onMessage(Message.class, this::stashOtherCommand)//
                .build();
    }

    /**
     * Unstash all messages in the buffer.
     */
    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        this.initial = m;
        watchForChanges(context.getExecutionContext(), initial.externalEditor.tempFile);
        return buffer.unstashAll(Behaviors.receive(Message.class)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(ExternalEditorClosedMessage.class, this::onExternalEditorClosed)//
                .onMessage(ErrorReadKernelCodeMessage.class, this::onErrorReadKernelCode)//
                .build());
    }

    private Behavior<Message> stashOtherCommand(Message m) {
        log.debug("stashOtherCommand: {}", m);
        buffer.stash(m);
        return Behaviors.same();
    }

    /**
     * Stops the external editor.
     */
    private Behavior<Message> onShutdown(ShutdownMessage m) {
        log.debug("onShutdown {}", m);
        initial.externalEditor.process.cancel(true);
        initial.externalEditor.build.shutdown();
        return Behaviors.stopped();
    }

    /**
     * There was an error opening the external editor. Notify parent actor and stop
     * this actor.
     */
    private Behavior<Message> onExternalEditorOpenError(ExternalEditorOpenErrorMessage m) {
        log.debug("onExternalEditorOpenError {}", m);
        actor.get().tell(m);
        return Behaviors.stopped();
    }

    /**
     * The external editor was closed. Notify parent actor and stop this actor.
     */
    private Behavior<Message> onExternalEditorClosed(ExternalEditorClosedMessage m) {
        log.debug("onExternalEditorClosed: {}", m);
        actor.get().tell(m);
        return Behaviors.stopped();
    }

    private Behavior<Message> onErrorReadKernelCode(ErrorReadKernelCodeMessage m) {
        log.debug("onErrorReadKernelCode: {}", m);
        return Behaviors.same();
    }

    private void watchForChanges(Executor executor, Path temp) {
        fileWatcherFactory.create(temp).addListener(new FileListener() {
            @Override
            public void onModified(FileEvent event) {
                if (event.getPath().equals(temp)) {
                    readFile(temp);
                }
            }
        }).watch();
    }

    private void readFile(Path temp) {
        try {
            var code = FileUtils.readFileToString(temp.toFile(), UTF_8);
            runFxThread(() -> {
                gmppp.get().kernelCode.set(code);
                gmppp.get().codeLastChange.set(System.currentTimeMillis());
            });
        } catch (IOException e) {
            log.error("Error read kernel code file", e);
            actor.get().tell(new ErrorReadKernelCodeMessage(e));
        }
    }

}
