/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX
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
 */
package com.anrisoftware.anlopencl.jmeapp.actors;

import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.runFxThread;
import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import com.anrisoftware.anlopencl.jmeapp.controllers.GameMainPaneController;
import com.anrisoftware.anlopencl.jmeapp.controllers.GlobalKeys;
import com.anrisoftware.anlopencl.jmeapp.controllers.MappingFieldsPaneController;
import com.anrisoftware.anlopencl.jmeapp.messages.AttachGuiMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFailedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.GuiMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.LocalizeControlsMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.GameSettings;
import com.anrisoftware.anlopencl.jmeapp.states.KeyMapping;
import com.anrisoftware.resources.images.external.IconSize;
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
 * Mapping fields panel actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public class MappingFieldsPaneActor {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            MappingFieldsPaneActor.class.getSimpleName());

    public static final String NAME = MappingFieldsPaneActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface MappingFieldsPaneActorFactory {

        MappingFieldsPaneActor create(ActorContext<Message> context);
    }

    public static Behavior<Message> create(Injector injector) {
        return Behaviors.setup((context) -> {
            context.getSystem().receptionist().tell(Receptionist.register(KEY, context.getSelf()));
            return injector.getInstance(MappingFieldsPaneActorFactory.class).create(context).start();
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
    private GameMainPanePropertiesProvider onp;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GlobalKeys globalKeys;

    @Inject
    @Named("keyMappings")
    private Map<String, KeyMapping> keyMappings;

    private MappingFieldsPaneController controller;

    @Inject
    MappingFieldsPaneActor(@Assisted ActorContext<Message> context, GameSettings gs, ImagesFactory images) {
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
        var mainController = (GameMainPaneController) m.controller;
        this.controller = mainController.mappingFieldsPaneController;
        runFxThread(() -> {
            controller.updateLocale(Locale.US, images, IconSize.SMALL);
            controller.initializeListeners(actor.get(), Locale.US, onp.get());
        });
        tellLocalizeControlsSelf(gs);
        return Behaviors.receive(Message.class)//
                .onMessage(LocalizeControlsMessage.class, this::onLocalizeControls)//
                .onMessage(AttachGuiMessage.class, this::onAttachGui)//
                .onMessage(BuildClickedMessage.class, this::onBuildClicked)//
                .onMessage(BuildFinishedMessage.class, this::onBuildFinished)//
                .onMessage(BuildFailedMessage.class, this::onBuildFailed)//
                .onMessage(GuiMessage.class, this::onGuiCatchall)//
                .build();
    }

    private void tellLocalizeControlsSelf(GameSettings gs) {
        context.getSelf().tell(new LocalizeControlsMessage(gs.getLocale(), gs.getIconSize(), gs.getTextPosition()));
    }

    private Behavior<Message> onAttachGui(AttachGuiMessage m) {
        log.debug("onAttachGui {}", m);
        return Behaviors.same();
    }

    private Behavior<Message> onBuildClicked(BuildClickedMessage m) {
        log.debug("onBuildClicked {}", m);
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
            controller.mappingX0Field.setDisable(disabled);
            controller.mappingX1Field.setDisable(disabled);
            controller.mappingY0Field.setDisable(disabled);
            controller.mappingY1Field.setDisable(disabled);
            controller.mappingZ0Field.setDisable(disabled);
            controller.mappingZ1Field.setDisable(disabled);
            controller.threeDMappingBox.setDisable(disabled);
        });
    }

}
