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

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import com.anrisoftware.anlopencl.jmeapp.view.actors.NoiseImageEntities;
import com.anrisoftware.anlopencl.jmeapp.view.states.CoordAxisDebugShape;
import com.anrisoftware.anlopencl.jmeapp.view.states.NoiseImageSystem;
import com.badlogic.ashley.core.Engine;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.style.BaseStyles;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Tests the addition and removing of image quads.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class QuadsTest extends SimpleApplication {

    @SneakyThrows
    public static void main(String[] args) {
        var injector = Guice.createInjector();
        injector.getInstance(QuadsTest.class).start(injector);
    }

    private final Engine engine;

    private CoordAxisDebugShape coordAxisDebugShape;

    private TextField columnsField;

    private TextField rowsField;

    private NoiseImageEntities noiseImageEntities;

    private NoiseImageSystem noiseImageSystem;

    private final NumberFormat numberFormat;

    private Label infoLabel;

    private Injector injector;

    public QuadsTest() {
        super(new DebugKeysAppState(), new ConstantVerifierState());
        this.engine = new Engine();
        this.numberFormat = NumberFormat.getInstance(Locale.US);
    }

    private void start(Injector parent) throws IOException {
        this.injector = parent.createChildInjector(new GameApplicationModule(this, engine));
        // stateManager.attach(new FlyCamAppState());
        setupSettings();
        start();
    }

    private void setupSettings() {
        setShowSettings(false);
        var s = new AppSettings(true);
        s.setResizable(true);
        s.setWidth(1024);
        s.setHeight(768);
        s.setVSync(false);
        s.setOpenCLSupport(true);
        setSettings(s);
    }

    @Override
    public void simpleInitApp() {
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        coordAxisDebugShape = new CoordAxisDebugShape(assetManager);
        rootNode.attachChild(coordAxisDebugShape.getNode());
        noiseImageSystem = injector.getInstance(NoiseImageSystem.class);
        noiseImageEntities = injector.getInstance(NoiseImageEntities.class);
        engine.addSystem(noiseImageSystem);
        setupCamera();
        setupGui();
    }

    private void setupGui() {
        var window = new Container();
        window.setPreferredSize(new Vector3f(300, 100, 0));
        guiNode.attachChild(window);
        window.setLocalTranslation(10, cam.getHeight() - 10, 0);
        window.addChild(new Label("Columns:"));
        this.columnsField = new TextField("1");
        columnsField.setTextHAlignment(HAlignment.Right);
        window.addChild(columnsField);
        window.addChild(new Label("Rows:"));
        this.rowsField = new TextField("1");
        rowsField.setTextHAlignment(HAlignment.Right);
        window.addChild(rowsField);
        var applyButton = window.addChild(new Button("Apply"));
        applyButton.addClickCommands(source -> {
            updateColsRows();
        });
        infoLabel = new Label("");
        window.addChild(infoLabel);
    }

    @SneakyThrows
    private void updateColsRows() {
        var cols = numberFormat.parse(columnsField.getText()).intValue();
        var rows = numberFormat.parse(rowsField.getText()).intValue();
        if (cols > 0 && rows > 0) {
            log.debug("Apply columns {} rows {}", cols, rows);
            infoLabel.setText("");
            noiseImageEntities.set(cols, rows);
        } else {
            infoLabel.setText("Columns and rows must be greater 0");
        }
    }

    private void setupCamera() {
        cam.setLocation(new Vector3f(1.7853183f, 1.1580523f, 10.528595f));
        cam.setRotation(new Quaternion(-3.711398E-4f, 0.99803764f, 0.062332783f, 0.005950089f));
    }

    @Override
    public void simpleUpdate(float tpf) {
    }
}
