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

import org.fxmisc.richtext.CodeArea;
import org.scenicview.ScenicView;

import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.jayfella.jme.jfx.JavaFxUI;

import akka.actor.typed.ActorRef;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code game-main-pane.fxml} controller.
 *
 * @author Erwin Müller
 */
@Slf4j
public class GameMainPaneController {

    @FXML
    public BorderPane rootPane;

    @FXML
    public SplitPane splitMain;

    @FXML
    public Accordion inputAccordion;

    @FXML
    public TitledPane imageInputsPane;

    @FXML
    public BorderPane imageFieldsPane;

    @FXML
    public TitledPane fileInputsPane;

    private Form loginForm;

    @FXML
    public Button buttonBuild;

    @FXML
    public Button buttonQuit;

    @FXML
    public ToggleButton buttonRun;

    public void initializeListeners(ActorRef<Message> actor, ObservableGameMainPaneProperties np) {
        setupKernelTextField(np);
        setupImagePropertiesFields(np);
        setupSplitMain(np);
        setupInputAccordion(np);
        JavaFxUtil.runFxThread(() -> {
            ScenicView.show(JavaFxUI.getInstance().getScene());
        });
    }

    private void setupImagePropertiesFields(ObservableGameMainPaneProperties np) {
        this.loginForm = Form.of(Group.of(//
                Field.ofIntegerType(np.seed).label("Seed").required("Not empty"), //
                Field.ofIntegerType(np.width).label("Width").required("Not empty"), //
                Field.ofIntegerType(np.height).label("Height").required("Not empty")));
        imageFieldsPane.setTop(new FormRenderer(loginForm));
    }

    private void setupKernelTextField(ObservableGameMainPaneProperties np) {
        var editor = new OpenCLKeywordsEditor();
        CodeArea area = editor.getCodeArea();
        area.replaceText(0, area.getLength(), np.kernelCode.get());
        imageFieldsPane.setCenter(area);
        area.textProperty().addListener((obs, oldText, newText) -> {
            np.kernelCode.set(newText);
        });
    }

    private void setupSplitMain(ObservableGameMainPaneProperties np) {
        splitMain.setDividerPosition(0, np.splitMainPosition.get());
        splitMain.getDividers().get(0).positionProperty().bindBidirectional(np.splitMainPosition);
    }

    private void setupInputAccordion(ObservableGameMainPaneProperties np) {
        updateLastExpandedPane(np.lastExpandedPane.get());
        inputAccordion.expandedPaneProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                np.lastExpandedPane.set(nv.getId());
            }
        });
        np.lastExpandedPane.addListener((o, ov, nv) -> {
            updateLastExpandedPane(nv);
        });
    }

    private void updateLastExpandedPane(String id) {
        var list = inputAccordion.getPanes().filtered((n) -> n.getId().equals(id));
        if (list.size() > 0) {
            var pane = list.get(0);
            inputAccordion.setExpandedPane(pane);
        } else {
            log.error("No panels in noiseAccordion matching {}", id);
        }
    }

}
