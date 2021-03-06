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

/**
 * Abstract message to create a new actor.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public abstract class CreateActorMessage extends Message {

    @ToString.Include
    public final Behavior<Message> actor;

    public final ActorRef<StatusReply<ActorRef<Message>>> replyTo;

    /**
     * Message to create a new anonymous actor.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @ToString(callSuper = true)
    public static class CreateAnonActorMessage extends CreateActorMessage {

        public CreateAnonActorMessage(Behavior<Message> actor, ActorRef<StatusReply<ActorRef<Message>>> replyTo) {
            super(actor, replyTo);
        }

    }

    /**
     * Message to create a new named actor.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
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

    /**
     * Ask to create a new anonymous actor within the timeout duration.
     *
     * @param system
     * @param timeout
     * @param actor
     * @return
     */
    public static CompletionStage<ActorRef<Message>> createAnonActor(ActorSystem<Message> system, Duration timeout,
            Behavior<Message> actor) {
        return AskPattern.<Message, ActorRef<Message>>askWithStatus(system,
                replyTo -> new CreateAnonActorMessage(actor, replyTo), timeout, system.scheduler());
    }

    /**
     * Ask to create a new named actor within the timeout duration. The named actor
     * is registered in the main actor.
     *
     * @param system
     * @param timeout
     * @param actor
     * @return
     */
    public static CompletionStage<ActorRef<Message>> createNamedActor(ActorSystem<Message> system, Duration timeout,
            int id, ServiceKey<Message> key, String name, Behavior<Message> actor) {
        return AskPattern.<Message, ActorRef<Message>>askWithStatus(system,
                replyTo -> new CreateNamedActorMessage(id, key, name, actor, replyTo), timeout, system.scheduler());
    }

}
