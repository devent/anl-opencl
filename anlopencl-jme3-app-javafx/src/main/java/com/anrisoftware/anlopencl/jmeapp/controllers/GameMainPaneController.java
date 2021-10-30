/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX
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
 */
package com.anrisoftware.anlopencl.jmeapp.controllers;

import org.fxmisc.richtext.CodeArea;

import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.view.renderer.FormRenderer;

import akka.actor.typed.ActorRef;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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
    public TitledPane map2dInputsPane;

    @FXML
    public BorderPane mappingFieldsPane;

    @FXML
    public TitledPane fileInputsPane;

    public Form imageForm;

    @FXML
    public Button buttonBuild;

    @FXML
    public Button buttonQuit;

    @FXML
    public ToggleButton buttonRun;

    @FXML
    public Label statusLabel;

    @FXML
    public ProgressIndicator statusProgress;

    public Form mappingForm;

    public void initializeListeners(ActorRef<Message> actor, ObservableGameMainPaneProperties np) {
        setupKernelTextField(np);
        setupImagePropertiesFields(np);
        setupSplitMain(np);
        setupInputAccordion(np);
        statusProgress.setProgress(0);
        statusProgress.setVisible(false);
        // JavaFxUtil.runFxThread(() -> {
        // ScenicView.show(JavaFxUI.getInstance().getScene());
        // });
    }

    private void setupImagePropertiesFields(ObservableGameMainPaneProperties np) {
        this.imageForm = Form.of(Group.of(//
                Field.ofIntegerType(np.seed).label("Seed").required("Not empty"), //
                Field.ofIntegerType(np.width).label("Width").required("Not empty"), //
                Field.ofIntegerType(np.height).label("Height").required("Not empty"), //
                Field.ofDoubleType(np.z).label("Z").required("Not empty"), //
                Field.ofIntegerType(np.dim).label("Dimension").required("Not empty") //
        ));
        imageFieldsPane.setTop(new FormRenderer(imageForm));
        var map3dField = Field.ofBooleanType(np.map3d).label("Map 3D");
        var z0Field = Field.ofDoubleType(np.mapz0).label("z0").required("Not empty");
        z0Field.getRenderer().setDisable(!np.map3d.get());
        var z1Field = Field.ofDoubleType(np.mapz1).label("z1").required("Not empty");
        z1Field.getRenderer().setDisable(!np.map3d.get());
        map3dField.changedProperty().addListener((observable, oldValue, newValue) -> {
            z0Field.getRenderer().setDisable(!newValue);
            z1Field.getRenderer().setDisable(!newValue);
        });
        this.mappingForm = Form.of(Group.of(//
                Field.ofDoubleType(np.mapx0).label("x0").required("Not empty"), //
                Field.ofDoubleType(np.mapx1).label("x1").required("Not empty"), //
                Field.ofDoubleType(np.mapy0).label("y0").required("Not empty"), //
                Field.ofDoubleType(np.mapy1).label("y1").required("Not empty") //
        ), Group.of(//
                map3dField, //
                z0Field, //
                z1Field //
        ));
        mappingFieldsPane.setTop(new FormRenderer(mappingForm));
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
