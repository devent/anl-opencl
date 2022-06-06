/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Core
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

import javax.inject.Named;

import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProvider;
import com.anrisoftware.anlopencl.jmeapp.actors.MainActorsModule;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.GameSettings;
import com.anrisoftware.anlopencl.jmeapp.messages.ShutdownMessage;
import com.anrisoftware.resources.binary.internal.binaries.BinariesResourcesModule;
import com.anrisoftware.resources.binary.internal.maps.BinariesDefaultMapsModule;
import com.anrisoftware.resources.images.internal.images.ImagesResourcesModule;
import com.anrisoftware.resources.images.internal.mapcached.ResourcesImagesCachedMapModule;
import com.anrisoftware.resources.images.internal.scaling.ResourcesSmoothScalingModule;
import com.anrisoftware.resources.texts.internal.maps.TextsDefaultMapsModule;
import com.anrisoftware.resources.texts.internal.texts.TextsResourcesCharsetModule;
import com.anrisoftware.resources.texts.internal.texts.TextsResourcesModule;
import com.badlogic.ashley.core.Engine;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import akka.actor.typed.ActorRef;
import lombok.extern.slf4j.Slf4j;

/**
 * Guice injected {@link SimpleApplication}.
 *
 * @author Erwin Müller {@literal <erwin@mullerlpublic.de>}
 */
@Slf4j
abstract class AbstractGameApplication extends SimpleApplication {

    /**
     * Stars the application with Guice injected.
     *
     * @param args the arguments.
     * @param type the {@link AbstractGameApplication} class.
     */
    public static void startApp(String[] args, Class<? extends AbstractGameApplication> type) {
        try {
            var globalInjector = Guice.createInjector();
            var app = globalInjector.getInstance(type);
            app.start(globalInjector);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    public final Engine entityEngine;

    public final GameSettings gs;

    public Injector globalInjector;

    public Injector injector;

    public Boolean appInitialized;

    public Node pivot;

    public ActorSystemProvider actor;

    protected AbstractGameApplication(GameSettings gs) {
        super();
        this.appInitialized = false;
        this.entityEngine = new Engine();
        this.gs = gs;
        setSettings(createAppSettings());
    }

    private AppSettings createAppSettings() {
        var s = new AppSettings(true);
        // s.setFullscreen(gs.windowFullscreen);
        // s.setWidth(gs.windowWidth);
        // s.setHeight(gs.windowHeight);
        return s;
    }

    public void start(Injector globalInjector) {
        this.globalInjector = globalInjector;
        this.injector = createInjector();
        start();
    }

    @Override
    public void simpleInitApp() {
        log.debug("simpleInitApp");
        try {
            flyCam.setEnabled(false);
            setDisplayStatView(true);
            this.pivot = new Node("pivot");
            getRootNode().attachChild(pivot);
            initApp();
            this.actor = injector.getInstance(ActorSystemProvider.class);
            this.appInitialized = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            stop();
            throw new RuntimeException(e);
        }
    }

    public void initApp() throws Exception {
    }

    @Override
    public void stop() {
        log.debug("stop");
        try {
            actor.get().tell(new ShutdownMessage());
            stopApp();
            super.stop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    public void stopApp() throws Exception {
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    protected Injector createInjector() {
        return globalInjector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new ImagesResourcesModule());
                install(new ResourcesImagesCachedMapModule());
                install(new ResourcesSmoothScalingModule());
                install(new BinariesResourcesModule());
                install(new BinariesDefaultMapsModule());
                install(new TextsResourcesModule());
                install(new TextsDefaultMapsModule());
                install(new TextsResourcesCharsetModule());
                install(new MainActorsModule());
            }

            @Provides
            Application getJmeApplication() {
                return AbstractGameApplication.this;
            }

            @Provides
            @Named("pivotNode")
            Node getPivotNode() {
                return AbstractGameApplication.this.pivot;
            }

            @Provides
            Camera getCamera() {
                return AbstractGameApplication.this.cam;
            }

            @Provides
            AssetManager getAssetManager() {
                return AbstractGameApplication.this.assetManager;
            }

            @Provides
            InputManager getInputManager() {
                return AbstractGameApplication.this.inputManager;
            }

            @Provides
            Engine getEntityEngine() {
                return AbstractGameApplication.this.entityEngine;
            }

            @Provides
            ViewPort getViewPort() {
                return AbstractGameApplication.this.viewPort;
            }

            @Provides
            ActorRef<Message> getActor() {
                return AbstractGameApplication.this.actor.get();
            }

            @Provides
            GameSettings getGameSettings() {
                return gs;
            }
        });
    }
}
