/**
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
import static java.time.Duration.ofSeconds;

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
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.images.external.ImagesFactory;
import com.google.inject.Injector;

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
    }

    public interface GameMainPanelActorFactory extends AbstractMainPanelActorFactory {

    }

    public static CompletionStage<ActorRef<Message>> create(Injector injector, Duration timeout) {
        return AbstractMainPanelActor.create(injector, timeout, ID, KEY, NAME, GameMainPanelActorFactory.class,
                "/game-main-pane.fxml", panelActors, "/modena_dark.css", "/forms-inputs.css",
                "/opencl-keywords-dark-wombat.css");
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
        images = imagesFactory.create(GameMainPanelActor.class.getSimpleName());
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
                    if (throwable == null) {
                        log.debug("Returning BuildFinishedMessage");
                        return new BuildStartMessage.BuildFinishedMessage();
                    } else {
                        log.debug("Returning BuildFailedMessage");
                        return new BuildStartMessage.BuildFailedMessage(throwable);
                    }
                });
        return super.getBehaviorAfterAttachGui()//
                .onMessage(BuildStartMessage.BuildFinishedMessage.class, this::onBuildFinished)//
                .onMessage(BuildStartMessage.BuildFailedMessage.class, this::onBuildFailed)//
                .build();
    }

    private Behavior<Message> onBuildFinished(BuildFinishedMessage m) {
        log.debug("onBuildFinished {}", m);
        initial.actors.get(StatusBarActor.NAME).tell(m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        return getDefaultBehavior().build();
    }

    private Behavior<Message> onBuildFailed(BuildFailedMessage m) {
        log.debug("onBuildFailed {}", m);
        initial.actors.get(StatusBarActor.NAME).tell(m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        return getDefaultBehavior().build();
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(BuildClickedMessage.class, this::onBuildClicked)//
        ;
    }

}
