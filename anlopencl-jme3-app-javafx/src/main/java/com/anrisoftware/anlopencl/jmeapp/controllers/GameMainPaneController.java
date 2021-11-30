/*
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

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

import java.util.Locale;

import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.dlsc.formsfx.model.structure.Form;

import akka.actor.typed.ActorRef;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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
    public Pane imageFieldsPane;

    @FXML
    public ImageFieldsPaneController imageFieldsPaneController;

    @FXML
    public TitledPane map2dInputsPane;

    @FXML
    public BorderPane mappingFieldsPane;

    @FXML
    public MappingFieldsPaneController mappingFieldsPaneController;

    @FXML
    public TitledPane fileInputsPane;

    @FXML
    public Button buttonBuild;

    @FXML
    public Button buttonQuit;

    @FXML
    public ToggleButton buttonRun;

    @FXML
    public Button resetCameraButton;

    @FXML
    public Label statusLabel;

    @FXML
    public ProgressIndicator statusProgress;

    @FXML
    public TitledPane buildLogsPane;

    @FXML
    public TextArea buildLogsText;

    public Form mappingForm;

    public void updateLocale(Locale locale, Images images, IconSize iconSize) {
        resetCameraButton.setGraphic(new ImageView(toFXImage(
                images.getResource("resetCameraButton", locale, iconSize).getBufferedImage(TYPE_INT_ARGB), null)));
        resetCameraButton.setText(null);
    }

    public void initializeListeners(ActorRef<Message> actor, ObservableGameMainPaneProperties np) {
        log.debug("initializeListeners");
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
