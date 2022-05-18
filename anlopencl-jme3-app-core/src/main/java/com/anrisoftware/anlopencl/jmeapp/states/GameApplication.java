/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Core
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
 * ANL-OpenCL :: JME3 - App - Core is a derivative work based on Josua Tippetts' C++ library:
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
 * ANL-OpenCL :: JME3 - App - Core bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl.jmeapp.states;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProvider;
import com.anrisoftware.anlopencl.jmeapp.actors.GameMainPanelActor;
import com.anrisoftware.anlopencl.jmeapp.messages.AttachGuiMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.ShutdownMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.view.actors.ViewActor;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.system.AppSettings;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AskPattern;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Game application.
 *
 * @author Erwin Müller {@literal <erwin@mullerlpublic.de}
 */
@Slf4j
public class GameApplication extends SimpleApplication {

    public static void main(String[] args) {
        var injector = Guice.createInjector();
        injector.getInstance(GameApplication.class).start(injector);
    }

    Injector injector;

    Injector parent;

    ActorSystemProvider actor;

    ActorRef<Message> mainWindowActor;

    Engine engine;

    public GameApplication() throws IOException {
        super(new StatsAppState(), new DebugKeysAppState(), new ConstantVerifierState());
        setShowSettings(false);
        var s = new AppSettings(true);
        loadAppIcon(s);
        s.setResizable(true);
        s.setWidth(1024);
        s.setHeight(768);
        s.setVSync(false);
        s.setOpenCLSupport(true);
        setSettings(s);
    }

    private void loadAppIcon(AppSettings s) throws IOException {
        s.setIcons(new BufferedImage[] { ImageIO.read(getClass().getResource("/app/logo.png")) });
        s.setTitle(IOUtils.toString(getClass().getResource("/app/title.txt"), UTF_8));
    }

    private void start(Injector parent) {
        this.parent = parent;
        super.start();
    }

    @Override
    @SneakyThrows
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        // viewPort.setBackgroundColor(ColorRGBA.DarkGray.clone());
        this.engine = new Engine();
        this.injector = parent.createChildInjector(new GameApplicationModule(this));
        this.actor = injector.getInstance(ActorSystemProvider.class);
        var gmpp = injector.getInstance(GameMainPanePropertiesProvider.class);
        gmpp.load();
        GameMainPanelActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
            mainWindowActor = ret;
            CompletionStage<AttachGuiFinishedMessage> result = AskPattern.ask(mainWindowActor,
                    replyTo -> new AttachGuiMessage(replyTo), ofMinutes(1), actor.getActorSystem().scheduler());
            result.whenComplete((ret1, ex1) -> {
                inputManager.deleteMapping(INPUT_MAPPING_EXIT);
            });
        });
        ViewActor.create(injector, ofSeconds(1)).whenComplete((ret, ex) -> {
            if (ex != null) {
                log.error("ViewActor error", ex);
            }
        });
    }

    @Override
    public void stop() {
        var gmpp = injector.getInstance(GameMainPanePropertiesProvider.class);
        gmpp.save();
        actor.get().tell(new ShutdownMessage());
        super.stop();
    }

    @Override
    public void simpleUpdate(float tpf) {
        engine.update(tpf);
    }
}
