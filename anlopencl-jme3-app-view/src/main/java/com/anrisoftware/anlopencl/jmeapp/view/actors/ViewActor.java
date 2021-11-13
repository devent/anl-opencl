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
import static com.jme3.texture.Image.Format.RGBA8;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.impl.factory.Maps;
import org.lwjgl.system.MemoryStack;

import com.anrisoftware.anlopencl.jme.opencl.MappingRanges;
import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProvider;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.ResetCameraMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.anrisoftware.anlopencl.jmeapp.view.components.KernelComponent;
import com.anrisoftware.anlopencl.jmeapp.view.messages.AttachViewAppStateDoneMessage;
import com.anrisoftware.anlopencl.jmeapp.view.states.CameraPanningAppState;
import com.anrisoftware.anlopencl.jmeapp.view.states.ViewAppState;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jme3.app.Application;
import com.jme3.opencl.lwjgl.LwjglBuffer;
import com.jme3.opencl.lwjgl.LwjglContext;
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

    private final ObservableGameMainPaneProperties gmpp;

    private final LwjglContext clContext;

    private final Map<String, Entity> noiseImageEntities;

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

    @Inject
    private CameraPanningAppState cameraPanningAppState;

    @Inject
    public ViewActor(GameMainPanePropertiesProvider gpp, com.jme3.opencl.Context openclContext) {
        this.gmpp = gpp.get();
        this.clContext = (LwjglContext) openclContext;
        this.noiseImageEntities = Maps.mutable.empty();
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
            var entity = engine.createEntity().add(new ImageComponent(10, 10));
            noiseImageEntities.put(gmpp.kernelName.get(), entity);
            engine.addEntity(entity);
        });
        return buffer.unstashAll(Behaviors.receive(Message.class)//
                .onMessage(ResetCameraMessage.class, this::onResetCamera)//
                .onMessage(BuildFinishedMessage.class, this::onBuildFinished)//
                .build());
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
        var entity = noiseImageEntities.get(gmpp.kernelName.get());
        if (KernelComponent.m.has(entity)) {
            var kc = entity.remove(KernelComponent.class);
            kc.tex.getImage().dispose();
            kc.ranges.release();
        }
        try (var s = MemoryStack.stackPush()) {
            int width = gmpp.width.get();
            int height = gmpp.height.get();
            var tex = new Texture2D(width, height, 1, RGBA8);
            var ranges = MappingRanges.createWithBuffer(s);
            if (gmpp.map3d.get()) {
                setMap3D(ranges);
            } else {
                setMap2D(ranges);
            }
            var rangesb = new LwjglBuffer(ranges.getClBuffer(s, clContext.getContext()));
            entity.add(new KernelComponent(tex, rangesb));
        }
    }

    private void setMap2D(MappingRanges ranges) {
        ranges.setMap2D(gmpp.mapx0.get(), gmpp.mapx1.get(), gmpp.mapy0.get(), gmpp.mapy1.get());
    }

    private void setMap3D(MappingRanges ranges) {
        ranges.setMap3D(gmpp.mapx0.get(), gmpp.mapx1.get(), gmpp.mapy0.get(), gmpp.mapy1.get(), gmpp.mapz0.get(),
                gmpp.mapz1.get());
    }

}
