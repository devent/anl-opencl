/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Model
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
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Model bundles and uses the RandomCL library:
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

import javax.inject.Inject;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.map.mutable.primitive.SynchronizedIntObjectMap;

import com.anrisoftware.anlopencl.jmeapp.messages.ActorTerminatedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.CreateNamedActorMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.ShutdownMessage;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.pattern.StatusReply;
import lombok.extern.slf4j.Slf4j;

/**
 * Dispatches messages to child actors.
 *
 * @author Erwin Müller
 */
@Slf4j
public class MainActor extends MessageActor<Message> {

    public interface MainActorFactory {
        MainActor create(ActorContext<Message> context);
    }

    public static Behavior<Message> create(Injector injector) {
        return Behaviors.setup((context) -> injector.getInstance(MainActorFactory.class).create(context));
    }

    private final MutableIntObjectMap<ActorRef<Message>> actors;

    @Inject
    MainActor(@Assisted ActorContext<Message> context) {
        super(context);
        this.actors = new SynchronizedIntObjectMap<>(IntObjectMaps.mutable.empty());
    }

    public ActorRef<Message> getActor(int id) {
        return actors.get(id);
    }

    public boolean haveActor(int id) {
        return actors.containsKey(id);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()//
                .onMessage(CreateNamedActorMessage.class, this::onCreateNamedActor)//
                .onMessage(ActorTerminatedMessage.class, this::onActorTerminated)//
                .onMessage(ShutdownMessage.class, this::onShutdown)//
                .onMessage(Message.class, this::forwardMessage)//
                .build();
    }

    private Behavior<Message> onShutdown(ShutdownMessage m) {
        log.debug("onShutdown {}", m);
        forwardMessage(m);
        getContext().getSystem().terminate();
        return this;
    }

    private Behavior<Message> onCreateNamedActor(CreateNamedActorMessage m) {
        log.debug("onCreateNamedActor {}", m);
        var actor = getContext().spawnAnonymous(m.actor);
        getContext().watchWith(actor, new ActorTerminatedMessage(m.id, m.key, actor));
        actors.put(m.id, actor);
        m.replyTo.tell(StatusReply.success(actor));
        return this;
    }

    private Behavior<Message> onActorTerminated(ActorTerminatedMessage m) {
        log.debug("onActorTerminated {}", m);
        actors.remove(m.id);
        return this;
    }

    private Behavior<Message> forwardMessage(Message m) {
        for (ActorRef<Message> actor : actors) {
            actor.tell(m);
        }
        return this;
    }

}
