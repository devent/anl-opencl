/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX
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
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX bundles and uses the RandomCL library:
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

import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.runFxThread;
import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;
import static com.anrisoftware.anlopencl.jmeapp.states.DefaultKeyMappings.ABOUT_DIALOG_MAPPING;
import static com.anrisoftware.anlopencl.jmeapp.states.DefaultKeyMappings.BUILD_MAPPING;
import static com.anrisoftware.anlopencl.jmeapp.states.DefaultKeyMappings.QUIT_MAPPING;
import static com.anrisoftware.anlopencl.jmeapp.states.DefaultKeyMappings.RESET_CAMERA_MAPPING;
import static com.anrisoftware.anlopencl.jmeapp.states.DefaultKeyMappings.SETTINGS_MAPPING;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import com.anrisoftware.anlopencl.jmeapp.controllers.GameMainPaneController;
import com.anrisoftware.anlopencl.jmeapp.controllers.GlobalKeys;
import com.anrisoftware.anlopencl.jmeapp.messages.AboutDialogMessage.AboutDialogOpenTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.AboutDialogMessage.AboutDialogCloseTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFailedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.LocalizeControlsMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsDialogMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsDialogMessage.SettingsDialogOpenTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameSettingsProvider;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameSettings;
import com.anrisoftware.anlopencl.jmeapp.states.KeyMapping;
import com.anrisoftware.resources.images.external.Images;
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

    private final GameSettingsProvider gsp;

    @Inject
    @Named("AppIcons")
    private Images appIcons;

    @Inject
    private GlobalKeys globalKeys;

    @Inject
    @Named("keyMappings")
    private Map<String, KeyMapping> keyMappings;

    private GameMainPaneController controller;

    @Inject
    ToolbarButtonsActor(@Assisted ActorContext<Message> context, GameSettingsProvider gsp) {
        this.context = context;
        this.gsp = gsp;
        var gs = gsp.get();
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
        tellLocalizeControlsSelf(gsp.get());
        controller.buttonQuit.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(QUIT_MAPPING.name()));
        });
        controller.buttonBuild.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(BUILD_MAPPING.name()));
        });
        controller.resetCameraButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(RESET_CAMERA_MAPPING.name()));
        });
        controller.settingsButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(SETTINGS_MAPPING.name()));
        });
        controller.aboutButton.setOnAction((e) -> {
            globalKeys.runAction(keyMappings.get(ABOUT_DIALOG_MAPPING.name()));
        });
        /*
         * controller.commandsButtons.selectedToggleProperty().addListener((o, ov, nv)
         * -> { System.out.printf("%s-%s-%s%n", o, ov, nv); // TODO if (ov != null &&
         * !ov.isSelected()) {
         *
         * } if (nv != null && nv.isSelected()) {
         * globalKeys.runAction(keyMappings.get().get("BUILDINGS_MAPPING")); } });
         */
        return getDefaultBehavior();
    }

    private Behavior<Message> getDefaultBehavior() {
        return Behaviors.receive(Message.class)//
                .onMessage(LocalizeControlsMessage.class, this::onLocalizeControls)//
                .onMessage(BuildClickedMessage.class, this::onBuildClicked)//
                .onMessage(BuildFinishedMessage.class, this::onBuildFinished)//
                .onMessage(BuildFailedMessage.class, this::onBuildFailed)//
                .onMessage(SettingsDialogOpenTriggeredMessage.class, this::onSettingsClicked)//
                .onMessage(AboutDialogOpenTriggeredMessage.class, this::onAboutClicked)//
                .build();
    }

    private void tellLocalizeControlsSelf(ObservableGameSettings gs) {
        context.getSelf().tell(new LocalizeControlsMessage(gs));
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

    private Behavior<Message> onSettingsClicked(SettingsDialogOpenTriggeredMessage m) {
        log.debug("onSettingsClicked {}", m);
        setDisableControlButtons(true);
        return Behaviors.receive(Message.class)//
                .onMessage(SettingsDialogMessage.class, this::onSettingsDialogClosed)//
                .build();
    }

    private Behavior<Message> onSettingsDialogClosed(SettingsDialogMessage m) {
        log.debug("onSettingsDialogClosed {}", m);
        setDisableControlButtons(false);
        return getDefaultBehavior();
    }

    private Behavior<Message> onAboutClicked(AboutDialogOpenTriggeredMessage m) {
        log.debug("onAboutClicked {}", m);
        setDisableControlButtons(true);
        return Behaviors.receive(Message.class)//
                .onMessage(AboutDialogCloseTriggeredMessage.class, this::onAboutDialogClosed)//
                .build();
    }

    private Behavior<Message> onAboutDialogClosed(AboutDialogCloseTriggeredMessage m) {
        log.debug("onAboutDialogClosed {}", m);
        setDisableControlButtons(false);
        return getDefaultBehavior();
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
                toFXImage(appIcons.getResource(name, m.locale, m.iconSize).getBufferedImage(TYPE_INT_ARGB), null));
    }

    private ImageView loadControlIcon(LocalizeControlsMessage m, String name) {
        return new ImageView(toFXImage(
                appIcons.getResource(name, m.locale, m.iconSize.getTwoSmaller()).getBufferedImage(TYPE_INT_ARGB),
                null));
    }

    private void setDisableControlButtons(boolean disabled) {
        runFxThread(() -> {
            controller.buttonBuild.setDisable(disabled);
            controller.buttonRun.setDisable(disabled);
        });
    }

}
