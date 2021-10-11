/**
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
 */
package com.anrisoftware.anlopencl.jmeapp.states;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

import java.util.concurrent.CompletionStage;

import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProvider;
import com.anrisoftware.anlopencl.jmeapp.actors.GameMainPanelActor;
import com.anrisoftware.anlopencl.jmeapp.messages.AttachGuiMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.AttachGuiMessage.AttachGuiFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.messages.ShutdownMessage;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
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
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class MainPanelsUiTest extends SimpleApplication {

    public static void main(String[] args) {
        var injector = Guice.createInjector();
        injector.getInstance(MainPanelsUiTest.class).start(injector);
    }

    Injector injector;

    Injector parent;

    ActorSystemProvider actor;

    ActorRef<Message> mainWindowActor;

    Engine engine;

    public MainPanelsUiTest() {
        super(new StatsAppState(), new DebugKeysAppState(), new ConstantVerifierState());
        setShowSettings(false);
        var s = new AppSettings(true);
        s.setResizable(true);
        s.setWidth(1024);
        s.setHeight(768);
        s.setVSync(false);
        s.setOpenCLSupport(true);
        setSettings(s);
    }

    private void start(Injector parent) {
        this.parent = parent;
        super.start();
    }

    @Override
    @SneakyThrows
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        this.engine = new Engine();
        this.injector = parent.createChildInjector(new MainPanelsUiModules(), new MainPanelsUiTestModule(this));
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
