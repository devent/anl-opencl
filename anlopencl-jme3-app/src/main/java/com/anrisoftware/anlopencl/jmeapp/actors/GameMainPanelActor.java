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

import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.runFxThread;
import static java.time.Duration.ofSeconds;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.anlopencl.jmeapp.controllers.GameMainPaneController;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.google.inject.Injector;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.extern.slf4j.Slf4j;

/**
 * Noise main panel actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public class GameMainPanelActor extends AbstractMainPanelActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            GameMainPanelActor.class.getSimpleName());

    public static final String NAME = GameMainPanelActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    private static final Map<String, PanelActorCreator> panelActors = Maps.mutable.empty();

    static {
        panelActors.put(ToolbarButtonsActor.NAME, ToolbarButtonsActor::create);
    }

    public interface GameMainPanelActorFactory extends AbstractMainPanelActorFactory {

    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractMainPanelActor.create(injector, timeout, ID, KEY, NAME, GameMainPanelActorFactory.class,
                "/game-main-pane.fxml", panelActors, "/game-theme.css", "/forms-inputs.css", "/opencl-keywords.css");
    }

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameMainPanePropertiesProvider onp;

    @Inject
    private Injector injector;

    private ActorRef<Message> openclBuildActor;

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        runFxThread(() -> {
            var controller = (GameMainPaneController) initial.controller;
            controller.initializeListeners(actor.get(), onp.get());
        });
        Duration timeout = Duration.ofSeconds(3);
        var stage = OpenclBuildActor.create(injector, timeout);
        stage.whenComplete((response, throwable) -> {
            if (throwable == null) {
                openclBuildActor = response;
            }
        });
        return super.getBehaviorAfterAttachGui()//
                .onMessage(BuildClickedMessage.class, this::onBuildClicked)//
        ;
    }

    private Behavior<Message> onBuildClicked(BuildClickedMessage m) {
        log.debug("onBuildClicked {}", m);
        Duration timeout = ofSeconds(3);
        context.ask(BuildStartMessage.BuildResponseMessage.class, openclBuildActor, timeout,
                (ActorRef<BuildStartMessage.BuildResponseMessage> ref) -> new BuildStartMessage(ref),
                (response, throwable) -> {
                    if (throwable != null) {
                        return new BuildStartMessage.BuildFinishedMessage(response);
                    } else {
                        return new BuildStartMessage.BuildFailedMessage(throwable);
                    }
                });
        return Behaviors.same();
    }

}
