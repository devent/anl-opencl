/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App
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
 * ANL-OpenCL :: JME3 - App is a derivative work based on Josua Tippetts' C++ library:
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
package com.anrisoftware.anlopencl.jmeapp.controllers;

import java.util.ArrayList;
import java.util.Arrays;
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

@Slf4j
public class MainPanelControllerBuild {

    @RequiredArgsConstructor
    public static class MainPanelControllerResult {

        public final Region root;

        public final Object controller;

    }

    @Inject
    private Application app;

    @Inject
    private GlobalKeys globalKeys;

    public CompletableFuture<MainPanelControllerResult> setupUi(Executor executor, String mainUiResource,
            String... additionalCss) {
        return CompletableFuture.supplyAsync(() -> {
            return setupGui0(mainUiResource, additionalCss);
        }, executor);
    }

    @SneakyThrows
    private MainPanelControllerResult setupGui0(String mainUiResource, String... additionalCss) {
        log.debug("setupGui0");
        // Font.loadFont(MainPanelControllerBuild.class.getResource("/Fonts/Behrensschrift.ttf").toExternalForm(),
        // 14);
        var css = new ArrayList<String>();
        css.add(getCss());
        css.addAll(Arrays.asList(additionalCss));
        var task = app.enqueue(() -> {
            JavaFxUI.initialize(app, css.toArray(new String[0]));
            return true;
        });
        task.get();
        globalKeys.setup(JavaFxUI.getInstance());
        var loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(mainUiResource));
        return new MainPanelControllerResult(loadFxml(loader, mainUiResource), loader.getController());
    }

    @SneakyThrows
    private String getCss() {
        return IOUtils.resourceToURL("/game-theme.css").toExternalForm();
    }

    @SneakyThrows
    private Pane loadFxml(FXMLLoader loader, String mainUiResource) {
        var root = (Pane) loader.load(getClass().getResourceAsStream(mainUiResource));
        return root;
    }

}
