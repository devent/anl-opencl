/**
 * Dwarf Hustle :: Terrain Editor :: Gui :: JavaFx - Dwarf Hustle Terrain Editor gui using the JavaFx.
 * Copyright © 2021 Erwin Müller (erwin.mueller@anrisoftware.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.anrisoftware.anlopencl.jmeapp.actors;

import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.jocl.cl_context;

import com.anrisoftware.anlopencl.anlkernel.AnlKernel.AnlKernelFactory;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
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

        public final Supplier<cl_context> clcontext;

    }

    @RequiredArgsConstructor
    @ToString(callSuper = true)
    private static class SetupUiErrorMessage extends Message {

        public final Throwable cause;

    }

    public static Behavior<Message> create(Injector injector) {
        return Behaviors.withStash(100, stash -> Behaviors.setup((context) -> {
            createClContext(injector, context);
            return injector.getInstance(OpenclBuildActorFactory.class).create(context, stash).start();
        }));
    }

    private static void createClContext(Injector injector, ActorContext<Message> context) {
        var javaFxBuild = injector.getInstance(CLContextBuild.class);
        context.pipeToSelf(javaFxBuild.createClContext(context.getExecutionContext()), (result, cause) -> {
            if (cause == null) {
                return new InitialStateMessage(result);
            } else {
                return new SetupUiErrorMessage(cause);
            }
        });
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
        gmpp.get().kernel.set(anlKernelFactory.create(m.clcontext));
        return Behaviors.receive(Message.class)//
                .build();
    }
}
