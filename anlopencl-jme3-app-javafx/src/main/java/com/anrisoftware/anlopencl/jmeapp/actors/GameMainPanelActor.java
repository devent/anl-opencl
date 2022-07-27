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
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.collections.impl.factory.Maps;

import com.anrisoftware.anlopencl.jmeapp.controllers.GameMainPaneController;
import com.anrisoftware.anlopencl.jmeapp.messages.AboutDialogMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.AboutDialogMessage.AboutDialogOpenMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.AboutDialogMessage.AboutDialogOpenTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFailedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.KernelStartedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.KernelStartedMessage.KernelFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.OpenExternalEditorMessage.ExternalEditorClosedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.OpenExternalEditorMessage.ExternalEditorOpenErrorMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.OpenExternalEditorMessage.OpenExternalEditorTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.RunTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsDialogMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsDialogMessage.SettingsDialogOpenMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsDialogMessage.SettingsDialogOpenTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.google.inject.Injector;
import com.jme3.app.Application;
import com.jme3.app.LostFocusBehavior;
import com.jme3.opencl.KernelCompilationException;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.receptionist.ServiceKey;
import akka.actor.typed.scaladsl.Behaviors;
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

    private final AtomicInteger kernelStartedCounter;

    @Inject
    @Named("AppIcons")
    private Images appIcons;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameMainPanePropertiesProvider onp;

    @Inject
    private Application app;

    private ActorRef<Message> openclBuildActor;

    @Inject
    public GameMainPanelActor() {
        this.kernelStartedCounter = new AtomicInteger(0);
    }

    @Override
    protected BehaviorBuilder<Message> getBehaviorAfterAttachGui() {
        runFxThread(() -> {
            var controller = (GameMainPaneController) initial.controller;
            controller.updateLocale(Locale.US, appIcons, IconSize.SMALL);
            controller.initializeListeners(actor.get(), onp.get());
        });
        Duration timeout = Duration.ofSeconds(3);
        var stage = OpenclBuildActor.create(injector, timeout);
        stage.whenComplete((response, throwable) -> {
            if (throwable == null) {
                openclBuildActor = response;
                if (isNotBlank(onp.get().kernelCode.get())) {
                    actor.get().tell(new BuildTriggeredMessage());
                }
            }
        });
        return getDefaultBehavior()//
        ;
    }

    private Behavior<Message> onBuildTriggered(BuildTriggeredMessage m) {
        log.debug("onBuildTriggered {}", m);
        kernelStartedCounter.set(onp.get().columns.get() * onp.get().rows.get());
        setStartProgress(true);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        Duration timeout = ofSeconds(30);
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
                .onMessage(KernelStartedMessage.class, this::onKernelStarted)//
                .onMessage(KernelFinishedMessage.class, this::onKernelFinished)//
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
            setStartProgress(false);
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
     * Processing {@link SettingsDialogOpenTriggeredMessage}.
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
    private Behavior<Message> onSettingsDialogOpenTriggered(SettingsDialogOpenTriggeredMessage m) {
        log.debug("onSettingsDialogOpenTriggered {}", m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        return MainActor.sendMessageMayCreate(injector, SettingsDialogActor.ID, new SettingsDialogOpenMessage(),
                SettingsDialogActor::create, super.getBehaviorAfterAttachGui()//
                        .onMessage(SettingsDialogMessage.class, this::onSettingsDialogClosed)//
        );
    }

    private Behavior<Message> onSettingsDialogClosed(SettingsDialogMessage m) {
        log.debug("onSettingsDialogClosed {}", m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        return getDefaultBehavior().build();
    }

    /**
     * Processing {@link AboutDialogOpenTriggeredMessage}.
     * <p>
     * This message is send after if user wants to open the about dialog by either
     * clicking on the About button or using a shortcut key binding.
     * <p>
     * Returns a behavior that reacts to the following messages:
     * <ul>
     * <li>{@link #getBehaviorAfterAttachGui()}
     * <li>{@link AboutDialogMessage}
     * </ul>
     */
    private Behavior<Message> onAboutDialogOpenTriggered(AboutDialogOpenTriggeredMessage m) {
        log.debug("onAboutDialogOpenTriggered {}", m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        return MainActor.sendMessageMayCreate(injector, AboutDialogActor.ID, new AboutDialogOpenMessage(),
                AboutDialogActor::create, super.getBehaviorAfterAttachGui()//
                        .onMessage(AboutDialogMessage.class, this::onAboutDialog)//
        );
    }

    private Behavior<Message> onAboutDialog(AboutDialogMessage m) {
        log.debug("onAboutDialog {}", m);
        initial.actors.get(ToolbarButtonsActor.NAME).tell(m);
        return getDefaultBehavior().build();
    }

    private BehaviorBuilder<Message> getDefaultBehavior() {
        return super.getBehaviorAfterAttachGui()//
                .onMessage(BuildTriggeredMessage.class, this::onBuildTriggered)//
                .onMessage(RunTriggeredMessage.class, this::onRunTriggered)//
                .onMessage(SettingsDialogOpenTriggeredMessage.class, this::onSettingsDialogOpenTriggered)//
                .onMessage(AboutDialogOpenTriggeredMessage.class, this::onAboutDialogOpenTriggered)//
                .onMessage(KernelStartedMessage.class, this::onKernelStarted)//
                .onMessage(KernelFinishedMessage.class, this::onKernelFinished)//
                .onMessage(OpenExternalEditorTriggeredMessage.class, this::onOpenExternalEditorTriggered)//
        ;
    }

    private Behavior<Message> onRunTriggered(RunTriggeredMessage m) {
        log.debug("onRunTriggered {}", m);
        onp.get().kernelRun.set(!onp.get().kernelRun.get());
        return Behaviors.same();
    }

    private Behavior<Message> onKernelStarted(KernelStartedMessage m) {
        log.debug("onKernelStarted {}", m);
        return Behaviors.same();
    }

    private Behavior<Message> onKernelFinished(KernelFinishedMessage m) {
        log.debug("onKernelFinished {}", m);
        if (kernelStartedCounter.decrementAndGet() == 0) {
            setStartProgress(false);
        }
        return Behaviors.same();
    }

    private Behavior<Message> onOpenExternalEditorTriggered(OpenExternalEditorTriggeredMessage m) {
        log.debug("onOpenExternalEditorTriggered {}", m);
        app.setLostFocusBehavior(LostFocusBehavior.Disabled);
        initial.actors.get(ImageFieldsPaneActor.NAME).tell(m);
        return MainActor.sendMessageMayCreate(injector, ExternalEditorActor.ID, m, ExternalEditorActor::create,
                getDefaultBehavior()//
                        .onMessage(ExternalEditorClosedMessage.class, this::onExternalEditorClosed)//
                        .onMessage(ExternalEditorOpenErrorMessage.class, this::onExternalEditorOpenError)//
        );
    }

    /**
     * The external editor was closed. Notify parent actor and stop this actor.
     */
    private Behavior<Message> onExternalEditorClosed(ExternalEditorClosedMessage m) {
        log.debug("onExternalEditorClosed: {}", m);
        app.setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        initial.actors.get(ImageFieldsPaneActor.NAME).tell(m);
        return getDefaultBehavior().build();
    }

    private Behavior<Message> onExternalEditorOpenError(ExternalEditorOpenErrorMessage m) {
        log.debug("onExternalEditorOpenError {}", m);
        app.setLostFocusBehavior(LostFocusBehavior.PauseOnLostFocus);
        initial.actors.get(StatusBarActor.NAME).tell(m);
        return getDefaultBehavior().build();
    }

    private void forwardMessage(Message m) {
        initial.actors.forEach((a) -> {
            a.tell(m);
        });
    }

    private void setStartProgress(boolean start) {
        runFxThread(() -> {
            var controller = (GameMainPaneController) initial.controller;
            controller.statusProgress.setVisible(start);
            controller.statusProgress.setProgress(start ? -1 : 0);
        });
    }

}
