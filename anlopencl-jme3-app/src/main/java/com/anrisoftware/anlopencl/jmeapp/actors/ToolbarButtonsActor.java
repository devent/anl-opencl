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
import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.controllers.GameMainPaneController;
import com.anrisoftware.anlopencl.jmeapp.controllers.GlobalKeys;
import com.anrisoftware.anlopencl.jmeapp.messages.GuiMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.LocalizeControlsMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.states.GameSettings;
import com.anrisoftware.anlopencl.jmeapp.states.KeyMappingsProvider;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.images.external.ImagesFactory;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

/**
 * Commands panel actor. The panel contains the buttons to interact with the
 * area.
 *
 * @author Erwin Müller
 */
@Slf4j
public class ToolbarButtonsActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            ToolbarButtonsActor.class.getSimpleName());

    public static final String NAME = ToolbarButtonsActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    public interface ToolbarButtonsActorFactory {

        ToolbarButtonsActor create(ActorContext<Message> context);
    }

    public static Behavior<Message> create(Injector injector) {
        return Behaviors.setup((context) -> {
            context.getSystem().receptionist().tell(Receptionist.register(KEY, context.getSelf()));
            return injector.getInstance(ToolbarButtonsActorFactory.class).create(context).start();
        });
    }

    public static CompletionStage<ActorRef<Message>> create(Duration timeout, Injector injector) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
    }

    private final ActorContext<Message> context;

    private final GameSettings gs;

    private final Images images;

    @Inject
    private Injector injector;

    @Inject
    private GlobalKeys globalKeys;

    @Inject
    private KeyMappingsProvider keyMappings;

    private GameMainPaneController controller;

    @Inject
    ToolbarButtonsActor(@Assisted ActorContext<Message> context, GameSettings gs, ImagesFactory images) {
        this.context = context;
        this.images = images.create("ButtonsIcons");
        this.gs = gs;
        gs.locale.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
        gs.iconSize.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
        gs.textPosition.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
    }

    public Behavior<Message> start() {
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(LocalizeControlsMessage.class, this::onLocalizeControls)//
                .build();
    }

    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        this.controller = (GameMainPaneController) m.controller;
        tellLocalizeControlsSelf(gs);
        controller.buttonBuild.onActionProperty().addListener((o, ov, nv) -> {
            System.out.printf("%s-%s-%s%n", o, ov, nv); // TODO
            globalKeys.runAction(keyMappings.get().get("BUILDINGS_MAPPING"));
        });
        /*
         * controller.commandsButtons.selectedToggleProperty().addListener((o, ov, nv)
         * -> { System.out.printf("%s-%s-%s%n", o, ov, nv); // TODO if (ov != null &&
         * !ov.isSelected()) {
         *
         * } if (nv != null && nv.isSelected()) {
         * globalKeys.runAction(keyMappings.get().get("BUILDINGS_MAPPING")); } });
         */
        return Behaviors.receive(Message.class)//
                .onMessage(LocalizeControlsMessage.class, this::onLocalizeControls)//
                .onMessage(GuiMessage.class, this::onGuiCatchall)//
                .build();
    }

    private void tellLocalizeControlsSelf(GameSettings gs) {
        context.getSelf().tell(new LocalizeControlsMessage(gs.getLocale(), gs.getIconSize(), gs.getTextPosition()));
    }

    private Behavior<Message> onGuiCatchall(GuiMessage m) {
        log.debug("onGuiCatchall {}", m);
        // buildings.tell(m);
        return Behaviors.same();
    }

    private Behavior<Message> onLocalizeControls(LocalizeControlsMessage m) {
        log.debug("onLocalizeControls");
        runFxThread(() -> {
            setupIcons(m);
        });
        return Behaviors.same();
    }

    private void setupIcons(LocalizeControlsMessage m) {
        var contentDisplay = ContentDisplay.LEFT;
        switch (m.textPosition) {
        case NONE:
            contentDisplay = ContentDisplay.GRAPHIC_ONLY;
            break;
        case BOTTOM:
            contentDisplay = ContentDisplay.TOP;
            break;
        case LEFT:
            contentDisplay = ContentDisplay.RIGHT;
            break;
        case RIGHT:
            contentDisplay = ContentDisplay.LEFT;
            break;
        case TOP:
            contentDisplay = ContentDisplay.BOTTOM;
            break;
        }
    }

    private ImageView loadCommandIcon(LocalizeControlsMessage m, String name) {
        return new ImageView(
                toFXImage(images.getResource(name, m.locale, m.iconSize).getBufferedImage(TYPE_INT_ARGB), null));
    }

    private ImageView loadControlIcon(LocalizeControlsMessage m, String name) {
        return new ImageView(toFXImage(
                images.getResource(name, m.locale, m.iconSize.getTwoSmaller()).getBufferedImage(TYPE_INT_ARGB), null));
    }

}
