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
package com.anrisoftware.anlopencl.jmeapp.controllers;

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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
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

    public void initializeListeners(ActorRef<Message> actor, ObservableGameMainPaneProperties np) {
        setupKernelTextField();
        setupImagePropertiesFields(np);
        setupSplitMain(np);
        setupInputAccordion(np);
    }

    private void setupImagePropertiesFields(ObservableGameMainPaneProperties np) {
        this.loginForm = Form.of(Group.of(//
                Field.ofIntegerType(np.seed).label("Seed").required("Not empty"), //
                Field.ofIntegerType(np.width).label("Width").required("Not empty"), //
                Field.ofIntegerType(np.height).label("Height").required("Not empty")));
        imageFieldsPane.setTop(new FormRenderer(loginForm));
    }

    private void setupKernelTextField() {
        var editor = new OpenCLKeywordsEditor();
        imageFieldsPane.setCenter(editor.getCodeArea());
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
