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
