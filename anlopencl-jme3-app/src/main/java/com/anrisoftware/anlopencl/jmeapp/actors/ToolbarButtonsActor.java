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
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFailedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
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
        controller.buttonQuit.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get().get("QUIT_MAPPING"));
        });
        controller.buttonBuild.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get().get("BUILD_MAPPING"));
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
                .onMessage(BuildStartMessage.class, this::onBuildStart)//
                .onMessage(BuildFinishedMessage.class, this::onBuildFinished)//
                .onMessage(BuildFailedMessage.class, this::onBuildFailed)//
                .onMessage(GuiMessage.class, this::onGuiCatchall)//
                .build();
    }

    private void tellLocalizeControlsSelf(GameSettings gs) {
        context.getSelf().tell(new LocalizeControlsMessage(gs.getLocale(), gs.getIconSize(), gs.getTextPosition()));
    }

    private Behavior<Message> onBuildStart(BuildStartMessage m) {
        log.debug("onBuildStart {}", m);
        setDisableControlButtons(true);
        return Behaviors.same();
    }

    private Behavior<Message> onBuildFinished(BuildFinishedMessage m) {
        log.debug("onBuildFinished {}", m);
        setDisableControlButtons(false);
        return Behaviors.same();
    }

    private Behavior<Message> onBuildFailed(BuildFailedMessage m) {
        log.debug("onBuildFailed {}", m);
        setDisableControlButtons(false);
        return Behaviors.same();
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

    private void setDisableControlButtons(boolean disabled) {
        runFxThread(() -> {
            controller.buttonBuild.setDisable(disabled);
            controller.buttonRun.setDisable(disabled);
        });
    }

}
