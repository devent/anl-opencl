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
import java.text.ParseException;
import java.util.Locale;

import org.fxmisc.richtext.CodeArea;

import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.sun.javafx.collections.ImmutableObservableList;

import akka.actor.typed.ActorRef;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code game-main-pane.fxml} controller.
 *
 * @author Erwin Müller
 */
@Slf4j
public class ImageFieldsPaneController {

    @FXML
    public Label nameLabel;

    @FXML
    public TextField nameField;

    @FXML
    public Label seedLabel;

    @FXML
    public TextField seedField;

    @FXML
    public Label widthLabel;

    @FXML
    public Spinner<Integer> widthField;

    @FXML
    public Label heightLabel;

    @FXML
    public Spinner<Integer> heightField;

    @FXML
    public Label zLabel;

    @FXML
    public TextField zField;

    @FXML
    public Label dimensionLabel;

    @FXML
    public Spinner<Integer> dimensionField;

    @FXML
    public AnchorPane kernelCodePane;

    public void updateLocale(Locale locale, Images images, IconSize iconSize) {
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void initializeListeners(ActorRef<Message> actor, Locale locale, ObservableGameMainPaneProperties np) {
        log.debug("initializeListeners");
        nameField.textProperty().bindBidirectional(np.kernelName);
        seedField.textProperty().bindBidirectional(np.seed, NumberFormat.getIntegerInstance(locale));
        var twoExponentsList = new ImmutableObservableList<>(2, 4, 8, 16, 32, 64, 128, 512, 1024, 2048, 4096);
        var widthValueFactory = createNumbersValueFactory(locale, twoExponentsList);
        widthValueFactory.valueProperty().bindBidirectional((Property) np.width);
        widthField.setValueFactory(widthValueFactory);
        var heightValueFactory = createNumbersValueFactory(locale, twoExponentsList);
        heightValueFactory.valueProperty().bindBidirectional((Property) np.height);
        heightField.setValueFactory(heightValueFactory);
        zField.textProperty().bindBidirectional(np.z, NumberFormat.getNumberInstance(locale));
        var dimList = new ImmutableObservableList<>(2, 4, 8);
        var dimensionValueFactory = createNumbersValueFactory(locale, dimList);
        dimensionValueFactory.valueProperty().bindBidirectional((Property) np.dim);
        dimensionField.setValueFactory(dimensionValueFactory);
        setupKernelCode(np);
    }

    private ListSpinnerValueFactory<Integer> createNumbersValueFactory(Locale locale,
            ImmutableObservableList<Integer> twoExponentsList) {
        var widthValueFactory = new ListSpinnerValueFactory<>(twoExponentsList);
        widthValueFactory.setWrapAround(true);
        var numberFormat = NumberFormat.getIntegerInstance(locale);
        widthValueFactory.setConverter(new StringConverter<Integer>() {

            @Override
            public String toString(Integer object) {
                return numberFormat.format(object);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return numberFormat.parse(string).intValue();
                } catch (ParseException e) {
                    return null;
                }
            }
        });
        return widthValueFactory;
    }

    private void setupKernelCode(ObservableGameMainPaneProperties np) {
        var editor = new OpenCLKeywordsEditor();
        CodeArea area = editor.getCodeArea();
        area.replaceText(0, area.getLength(), np.kernelCode.get());
        area.textProperty().addListener((obs, oldText, newText) -> {
            np.kernelCode.set(newText);
            np.codeLastChange.set(System.currentTimeMillis());
        });
        kernelCodePane.getChildren().add(area);
        AnchorPane.setBottomAnchor(area, 0.0);
        AnchorPane.setLeftAnchor(area, 0.0);
        AnchorPane.setRightAnchor(area, 0.0);
        AnchorPane.setTopAnchor(area, 0.0);
    }

}
