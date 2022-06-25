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
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import com.anrisoftware.anlopencl.jmeapp.controllers.AboutDialogController;
import com.anrisoftware.anlopencl.jmeapp.messages.AboutDialogMessage.AboutDialogCloseTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.AboutDialogMessage.AboutDialogOpenMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.LocalizeControlsMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.GameSettingsProvider;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameSettings;
import com.anrisoftware.anlopencl.jmeapp.states.KeyMapping;
import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.texts.external.Texts;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.jayfella.jme.jfx.JavaFxUI;
import com.jme3.renderer.Camera;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.StashBuffer;
import akka.actor.typed.receptionist.ServiceKey;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

/**
 * About dialog actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public class AboutDialogActor extends AbstractJavafxPaneActor<AboutDialogController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            AboutDialogActor.class.getSimpleName());

    public static final String NAME = AboutDialogActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     * Creates {@link AboutDialogActor}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface AboutDialogActorFactory extends AbstractJavafxPaneActorFactory {

    }

    /**
     * Asynchronously loads the FXML file of the dialog and creates the dialog
     * actor.
     */
    public static CompletionStage<ActorRef<Message>> create(Duration timeout, Injector injector) {
        return createWithPane(timeout, ID, KEY, NAME, injector, injector.getInstance(AboutDialogActorFactory.class),
                "/about-dialog-pane.fxml");
    }

    private final GameSettingsProvider gsp;

    @Inject
    @Named("AppImages")
    private Images appImages;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    private GameMainPanePropertiesProvider onp;

    @Inject
    @Named("keyMappings")
    private Map<String, KeyMapping> keyMappings;

    @Inject
    @Named("AppTexts")
    private Texts appTexts;

    @Inject
    private Camera camera;

    @Inject
    AboutDialogActor(@Assisted ActorContext<Message> context, @Assisted StashBuffer<Message> buffer,
            GameSettingsProvider gsp) {
        super(context, buffer);
        this.gsp = gsp;
        var gs = gsp.get();
        gs.locale.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
        gs.iconSize.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
        gs.textPosition.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
    }

    @Override
    protected BehaviorBuilder<Message> getStartBehaviorBuilder() {
        return super.getStartBehaviorBuilder()//
                .onMessage(LocalizeControlsMessage.class, this::onLocalizeControls)//
        ;
    }

    @Override
    protected BehaviorBuilder<Message> doActivate(JavafxPaneInitialStateMessage<AboutDialogController> m) {
        log.debug("doActivate {}", m);
        runFxThread(() -> {
            controller.updateLocale(gsp.get(), appImages, appTexts);
            controller.initializeListeners(actor.get(), onp.get());
            pane.setPrefSize(camera.getWidth() - (double) 100, camera.getHeight() - (double) 100);
        });
        tellLocalizeControlsSelf(gsp.get());
        return super.doActivate(m)//
                .onMessage(LocalizeControlsMessage.class, this::onLocalizeControls)//
                .onMessage(AboutDialogOpenMessage.class, this::onAboutDialogOpenMessage)//
                .onMessage(AboutDialogCloseTriggeredMessage.class, this::onAboutDialogCloseTriggered)//
        ;
    }

    private Behavior<Message> onAboutDialogOpenMessage(AboutDialogOpenMessage m) {
        log.debug("onAboutDialogOpenMessage");
        runFxThread(() -> {
            JavaFxUI.getInstance().showDialog(pane);
        });
        return Behaviors.same();
    }

    private Behavior<Message> onAboutDialogCloseTriggered(AboutDialogCloseTriggeredMessage m) {
        log.debug("onAboutDialogCloseTriggered");
        runFxThread(() -> {
            JavaFxUI.getInstance().removeDialog();
        });
        return Behaviors.same();
    }

    private void tellLocalizeControlsSelf(ObservableGameSettings gs) {
        log.debug("tellLocalizeControlsSelf");
        context.getSelf().tell(new LocalizeControlsMessage(gs));
    }

    private Behavior<Message> onLocalizeControls(LocalizeControlsMessage m) {
        log.debug("onLocalizeControls {}", m);
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
                toFXImage(appImages.getResource(name, m.locale, m.iconSize).getBufferedImage(TYPE_INT_ARGB), null));
    }

    private ImageView loadControlIcon(LocalizeControlsMessage m, String name) {
        return new ImageView(toFXImage(
                appImages.getResource(name, m.locale, m.iconSize.getTwoSmaller()).getBufferedImage(TYPE_INT_ARGB),
                null));
    }

}
