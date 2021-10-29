/**
 * Dwarf Hustle :: Terrain :: JMonkeyEngine - Dwarf Hustle Terrain using the JMonkeyEngine engine.
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
package com.anrisoftware.anlopencl.jmeapp.view.actors;

import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProvider;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.anrisoftware.anlopencl.jmeapp.view.messages.AttachViewAppStateDoneMessage;
import com.anrisoftware.anlopencl.jmeapp.view.states.ViewAppState;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;

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

    @Assisted
    @Inject
    private ActorContext<Message> context;

    @Assisted
    @Inject
    private StashBuffer<Message> buffer;

    @Inject
    private Application app;

    @Inject
    private ViewAppState viewAppState;

    @Inject
    private Engine engine;

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
     * <li>
     * </ul>
     */
    private Behavior<Message> onAttachViewAppStateDone(AttachViewAppStateDoneMessage m) {
        log.debug("onAttachViewAppStateDone");
        app.enqueue(() -> {
            var entity = engine.createEntity().add(new ImageComponent(1024, 1024));
            engine.addEntity(entity);
        });
        return buffer.unstashAll(Behaviors.receive(Message.class)//
                .build());
    }

}
