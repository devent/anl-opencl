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

import static com.anrisoftware.anlopencl.jmeapp.actors.AdditionalCss.ADDITIONAL_CSS;
import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.runFxThread;
import static java.time.Duration.ofSeconds;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.anlopencl.jmeapp.controllers.GameMainPaneController;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFailedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsDialogMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsDialogOpenMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.images.external.ImagesFactory;
import com.google.inject.Injector;
import com.jme3.opencl.KernelCompilationException;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
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
        panelActors.put(StatusBarActor.NAME, StatusBarActor::create);
        panelActors.put(ImageFieldsPaneActor.NAME, ImageFieldsPaneActor::create);
        panelActors.put(MappingFieldsPaneActor.NAME, MappingFieldsPaneActor::create);
    }

    public interface GameMainPanelActorFactory extends AbstractMainPanelActorFactory {

    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractMainPanelActor.create(injector, timeout, ID, KEY, NAME, GameMainPanelActorFactory.class,
                "/game-main-pane.fxml", panelActors, ADDITIONAL_CSS);
    }

    private final Images images;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameMainPanePropertiesProvider onp;

    @Inject
    private Injector injector;

    private ActorRef<Message> openclBuildActor;

    @Inject
    public GameMainPanelActor(ImagesFactory imagesFactory) {
        this.images = imagesFactory.create(GameMainPanelActor.class.getSimpleName());
    }

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        runFxThread(() -> {
            var controller = (GameMainPaneController) initial.controller;
            controller.updateLocale(Locale.US, images, IconSize.SMALL);
            controller.initializeListeners(actor.get(), onp.get());
        });
        Duration timeout = Duration.ofSeconds(3);
        var stage = OpenclBuildActor.create(injector, timeout);
        stage.whenComplete((response, throwable) -> {
            if (throwable == null) {
                openclBuildActor = response;
                if (isNotBlank(onp.get().kernelCode.get())) {
                    actor.get().tell(new BuildClickedMessage());
                }
            }
        });
        return getDefaultBehavior();
    }

    private Behavior<Message> onBuildClicked(BuildClickedMessage m) {
        log.debug("onBuildClicked {}", m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        Duration timeout = ofSeconds(10);
        context.ask(BuildStartMessage.BuildResponseMessage.class, openclBuildActor, timeout,
                (ActorRef<BuildStartMessage.BuildResponseMessage> ref) -> new BuildStartMessage(ref),
                (response, throwable) -> {
                    if (throwable != null) {
                        log.debug("Returning BuildFailedMessage");
                        return new BuildStartMessage.BuildFailedMessage(throwable);
                    } else if (response instanceof BuildStartMessage.BuildFailedMessage) {
                        log.debug("Returning BuildFailedMessage");
                        return response;
                    } else {
                        log.debug("Returning BuildFinishedMessage");
                        return response;
                    }
                });
        return super.getBehaviorAfterAttachGui()//
                .onMessage(BuildStartMessage.BuildFinishedMessage.class, this::onBuildFinished)//
                .onMessage(BuildStartMessage.BuildFailedMessage.class, this::onBuildFailed)//
                .build();
    }

    private Behavior<Message> onBuildFinished(BuildFinishedMessage m) {
        log.debug("onBuildFinished {}", m);
        actor.get().tell(m);
        forwardMessage(m);
        runFxThread(() -> {
            var controller = (GameMainPaneController) initial.controller;
            onp.get().kernelLog.set(null);
            controller.buildLogsText.setText(null);
        });
        return getDefaultBehavior().build();
    }

    private Behavior<Message> onBuildFailed(BuildFailedMessage m) {
        log.debug("onBuildFailed {}", m);
        actor.get().tell(m);
        forwardMessage(m);
        runFxThread(() -> {
            var controller = (GameMainPaneController) initial.controller;
            if (m.cause instanceof KernelCompilationException) {
                var ex = (KernelCompilationException) m.cause;
                onp.get().kernelLog.set(ex.getLog());
                controller.buildLogsText.setText(ex.getLog());
            }
        });
        return getDefaultBehavior().build();
    }

    /**
     * Processing {@link SettingsClickedMessage}.
     * <p>
     * This message is send after if user wants to open the settings dialog by
     * either clicking on the settings button or using a shortcut key binding.
     * <p>
     * Returns a behavior that reacts to the following messages:
     * <ul>
     * <li>{@link #getBehaviorAfterAttachGui()}
     * <li>{@link SettingsDialogMessage}
     * </ul>
     */
    private Behavior<Message> onSettingsClicked(SettingsClickedMessage m) {
        log.debug("onSettingsClicked {}", m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        if (!actor.getMainActor().haveActor(SettingsDialogActor.ID)) {
            SettingsDialogActor.create(ofSeconds(1), injector).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Error create settings dialog actor", ex);
                } else {
                    res.tell(new SettingsDialogOpenMessage());
                }
            });
        } else {
            actor.getMainActor().getActor(SettingsDialogActor.ID).tell(new SettingsDialogOpenMessage());
        }
        return super.getBehaviorAfterAttachGui()//
                .onMessage(SettingsDialogMessage.class, this::onSettingsDialogClosed)//
                .build();
    }

    private Behavior<Message> onSettingsDialogClosed(SettingsDialogMessage m) {
        log.debug("onSettingsDialogClosed {}", m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        return getDefaultBehavior().build();
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(BuildClickedMessage.class, this::onBuildClicked)//
                .onMessage(SettingsClickedMessage.class, this::onSettingsClicked)//
        ;
    }

    private void forwardMessage(Message m) {
        initial.actors.forEach((a) -> {
            a.tell(m);
        });
    }

}
