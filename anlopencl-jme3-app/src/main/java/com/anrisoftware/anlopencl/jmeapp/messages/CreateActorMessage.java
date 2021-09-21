/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App
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
 * ANL-OpenCL :: JME3 - App is a derivative work based on Josua Tippetts' C++ library:
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
package com.anrisoftware.anlopencl.jmeapp.messages;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.receptionist.ServiceKey;
import akka.pattern.StatusReply;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public abstract class CreateActorMessage extends Message {

    @ToString.Include
    public final Behavior<Message> actor;

    public final ActorRef<StatusReply<ActorRef<Message>>> replyTo;

    @ToString(callSuper = true)
    public static class CreateAnonActorMessage extends CreateActorMessage {

        public CreateAnonActorMessage(Behavior<Message> actor, ActorRef<StatusReply<ActorRef<Message>>> replyTo) {
            super(actor, replyTo);
        }

    }

    @ToString(onlyExplicitlyIncluded = true, callSuper = true)
    public static class CreateNamedActorMessage extends CreateActorMessage {

        public final int id;

        public final ServiceKey<Message> key;

        @ToString.Include
        public final String name;

        public CreateNamedActorMessage(int id, ServiceKey<Message> key, String name, Behavior<Message> actor,
                ActorRef<StatusReply<ActorRef<Message>>> replyTo) {
            super(actor, replyTo);
            this.id = id;
            this.key = key;
            this.name = name;
        }

    }

    @ToString(onlyExplicitlyIncluded = true, callSuper = true)
    public static class CreateGameActorMessage extends CreateNamedActorMessage {

        public CreateGameActorMessage(int id, ServiceKey<Message> key, String name, Behavior<Message> actor,
                ActorRef<StatusReply<ActorRef<Message>>> replyTo) {
            super(id, key, name, actor, replyTo);
        }

    }

    public static CompletionStage<ActorRef<Message>> createAnonActor(ActorSystem<Message> system, Duration timeout,
            Behavior<Message> actor) {
        return AskPattern.<Message, ActorRef<Message>>askWithStatus(system,
                replyTo -> new CreateAnonActorMessage(actor, replyTo), timeout, system.scheduler());
    }

    public static CompletionStage<ActorRef<Message>> createNamedActor(ActorSystem<Message> system, Duration timeout,
            int id, ServiceKey<Message> key, String name, Behavior<Message> actor) {
        return AskPattern.<Message, ActorRef<Message>>askWithStatus(system,
                replyTo -> new CreateNamedActorMessage(id, key, name, actor, replyTo), timeout, system.scheduler());
    }

    public static CompletionStage<ActorRef<Message>> createGameActor(ActorSystem<Message> system, Duration timeout,
            int id, ServiceKey<Message> key, String name, Behavior<Message> actor) {
        return AskPattern.<Message, ActorRef<Message>>askWithStatus(system,
                replyTo -> new CreateGameActorMessage(id, key, name, actor, replyTo), timeout, system.scheduler());
    }

}
