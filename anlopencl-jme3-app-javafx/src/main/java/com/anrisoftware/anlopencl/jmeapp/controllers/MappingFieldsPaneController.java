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
 */
package com.anrisoftware.anlopencl.jmeapp.controllers;

import java.text.NumberFormat;
import java.util.Locale;

import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;

import akka.actor.typed.ActorRef;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code mapping-fields-pane.fxml} controller.
 *
 * @author Erwin Müller
 */
@Slf4j
public class MappingFieldsPaneController {

    @FXML
    public Label mappingX0Label;

    @FXML
    public TextField mappingX0Field;

    @FXML
    public Label mappingX1Label;

    @FXML
    public TextField mappingX1Field;

    @FXML
    public Label mappingY0Label;

    @FXML
    public TextField mappingY0Field;

    @FXML
    public Label mappingY1Label;

    @FXML
    public TextField mappingY1Field;

    @FXML
    public Label mappingZ0Label;

    @FXML
    public TextField mappingZ0Field;

    @FXML
    public Label mappingZ1Label;

    @FXML
    public TextField mappingZ1Field;

    @FXML
    public CheckBox threeDMappingBox;

    public void updateLocale(Locale locale, Images images, IconSize iconSize) {
    }

    public void initializeListeners(ActorRef<Message> actor, Locale locale, ObservableGameMainPaneProperties np) {
        log.debug("initializeListeners");
        mappingX0Field.textProperty().bindBidirectional(np.mapx0, NumberFormat.getIntegerInstance(locale));
        mappingX1Field.textProperty().bindBidirectional(np.mapx1, NumberFormat.getIntegerInstance(locale));
        mappingY0Field.textProperty().bindBidirectional(np.mapy0, NumberFormat.getIntegerInstance(locale));
        mappingY1Field.textProperty().bindBidirectional(np.mapy1, NumberFormat.getIntegerInstance(locale));
        mappingZ0Field.textProperty().bindBidirectional(np.mapz0, NumberFormat.getIntegerInstance(locale));
        mappingZ1Field.textProperty().bindBidirectional(np.mapz1, NumberFormat.getIntegerInstance(locale));
        mappingZ0Field.setDisable(true);
        mappingZ1Field.setDisable(true);
        threeDMappingBox.selectedProperty().not().addListener((observable, oldValue, newValue) -> {
            mappingZ0Field.setDisable(newValue);
            mappingZ1Field.setDisable(newValue);
        });
    }

}
