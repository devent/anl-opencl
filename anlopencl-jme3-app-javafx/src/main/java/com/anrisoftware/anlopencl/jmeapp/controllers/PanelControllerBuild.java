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
package com.anrisoftware.anlopencl.jmeapp.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.jayfella.jme.jfx.JavaFxUI;
import com.jme3.app.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Loads the FXML and creates the panel controller.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class PanelControllerBuild {

    /**
     * Initializes the JavaFx UI and loads the FXML and creates the panel
     * controller.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public static class PanelControllerInitializeFxBuild extends PanelControllerBuild {

        @Inject
        PanelControllerInitializeFxBuild(Application app, GlobalKeys globalKeys) {
            super(app, globalKeys);
        }

        @Override
        @SneakyThrows
        protected void initializeFx(List<String> css) {
            var task = app.enqueue(() -> {
                JavaFxUI.initialize(app, css.toArray(new String[0]));
                return true;
            });
            task.get();
            globalKeys.setup(JavaFxUI.getInstance(), app.getInputManager());
        }
    }

    /**
     * Contains the loaded panel and controller.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @RequiredArgsConstructor
    public static class PanelControllerResult {

        public final Region root;

        public final Object controller;

    }

    protected final Application app;

    protected final GlobalKeys globalKeys;

    @Inject
    public PanelControllerBuild(Application app, GlobalKeys globalKeys) {
        this.app = app;
        this.globalKeys = globalKeys;
    }

    public CompletableFuture<PanelControllerResult> loadFxml(Executor executor, String fxmlfile,
            String... additionalCss) {
        return CompletableFuture.supplyAsync(() -> {
            return loadFxml0(fxmlfile, additionalCss);
        }, executor);
    }

    @SneakyThrows
    private PanelControllerResult loadFxml0(String fxmlfile, String... additionalCss) {
        log.debug("setupGui0");
        loadFone();
        var css = new ArrayList<String>();
        css.add(getCss());
        css.addAll(Arrays.asList(additionalCss));
        initializeFx(css);
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlfile));
        return new PanelControllerResult(loadFxml(loader, fxmlfile), loader.getController());
    }

    private void loadFone() {
        // Font.loadFont(MainPanelControllerBuild.class.getResource("/Fonts/Behrensschrift.ttf").toExternalForm(),
        // 14);
    }

    protected void initializeFx(List<String> css) {
        // call JavaFxUI.initialize if needed
    }

    @SneakyThrows
    private String getCss() {
        return IOUtils.resourceToURL("/game-theme.css").toExternalForm();
    }

    @SneakyThrows
    private Pane loadFxml(FXMLLoader loader, String res) {
        return JavaFxUtil.runFxAndWait(10, SECONDS, () -> {
            log.debug("Load FXML file {}", res);
            return (Pane) loader.load(getClass().getResourceAsStream(res));
        });
    }

}
