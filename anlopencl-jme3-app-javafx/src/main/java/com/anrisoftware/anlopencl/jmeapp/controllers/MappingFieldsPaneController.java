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

import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.toGraphicFromResource;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;

import akka.actor.typed.ActorRef;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.util.StringConverter;
import lombok.Data;
import lombok.SneakyThrows;
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
    public Spinner<Float> mappingX0Field;

    @FXML
    public Label mappingX1Label;

    @FXML
    public Spinner<Float> mappingX1Field;

    @FXML
    public Label mappingY0Label;

    @FXML
    public Spinner<Float> mappingY0Field;

    @FXML
    public Label mappingY1Label;

    @FXML
    public Spinner<Float> mappingY1Field;

    @FXML
    public Label mappingZ0Label;

    @FXML
    public Spinner<Float> mappingZ0Field;

    @FXML
    public Label mappingZ1Label;

    @FXML
    public Spinner<Float> mappingZ1Field;

    @FXML
    public CheckBox threeDMappingBox;

    @FXML
    public Spinner<Integer> rowsField;

    @FXML
    public Spinner<Integer> columnsField;

    @FXML
    public ToggleButton linkXButton;

    @FXML
    public ToggleButton linkYButton;

    @FXML
    public TextField stepsXField;

    @FXML
    public TextField stepsYField;

    private LinkMappingFieldListener linkXMappingFieldListener;

    private LinkMappingFieldListener linkYMappingFieldListener;

    private DecimalFormat df;

    @SneakyThrows
    public void updateLocale(Locale locale, Images images, IconSize iconSize) {
        df = new DecimalFormat("#.#####");
        linkXButton.setGraphic(toGraphicFromResource(images.getResource("linkXButton", locale, iconSize)));
        linkXButton.setText("");
        linkYButton.setGraphic(toGraphicFromResource(images.getResource("linkYButton", locale, iconSize)));
        linkYButton.setText("");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void initializeListeners(ActorRef<Message> actor, Locale locale, ObservableGameMainPaneProperties np) {
        log.debug("initializeListeners");
        var x0ValueFactory = createFloatValueFactory(np.mapx0.get(), (Property) np.mapx0);
        mappingX0Field.setValueFactory(x0ValueFactory);
        var x1ValueFactory = createFloatValueFactory(np.mapx1.get(), (Property) np.mapx1);
        mappingX1Field.setValueFactory(x1ValueFactory);
        var y0ValueFactory = createFloatValueFactory(np.mapy0.get(), (Property) np.mapy0);
        mappingY0Field.setValueFactory(y0ValueFactory);
        var y1ValueFactory = createFloatValueFactory(np.mapy1.get(), (Property) np.mapy1);
        mappingY1Field.setValueFactory(y1ValueFactory);
        var z0ValueFactory = createFloatValueFactory(np.mapz0.get(), (Property) np.mapz0);
        mappingZ0Field.setValueFactory(z0ValueFactory);
        var z1ValueFactory = createFloatValueFactory(np.mapz1.get(), (Property) np.mapz1);
        mappingZ1Field.setValueFactory(z1ValueFactory);

        mappingZ0Field.setDisable(!np.map3d.get());
        mappingZ1Field.setDisable(!np.map3d.get());
        threeDMappingBox.setSelected(np.map3d.get());
        threeDMappingBox.selectedProperty().bindBidirectional(np.map3d);
        threeDMappingBox.selectedProperty().addListener((o, oldValue, newValue) -> {
            mappingZ0Field.setDisable(!np.map3d.get());
            mappingZ1Field.setDisable(!np.map3d.get());
        });

        var rowsValueFactory = createIntegerValueFactory(np.rows.get(), (Property) np.rows);
        rowsField.setValueFactory(rowsValueFactory);
        var columnsValueFactory = createIntegerValueFactory(np.columns.get(), (Property) np.columns);
        columnsField.setValueFactory(columnsValueFactory);

        linkXMappingFieldListener = new LinkMappingFieldListener(mappingX0Field, mappingX1Field, np);
        registerOrUnregisterLinkXMappingField(np.linkX.get());
        linkXButton.setSelected(np.linkX.get());
        linkXButton.selectedProperty().bindBidirectional(np.linkX);
        linkXButton.selectedProperty().addListener((o, oldValue, newValue) -> {
            registerOrUnregisterLinkXMappingField(newValue);
        });
        linkYMappingFieldListener = new LinkMappingFieldListener(mappingY0Field, mappingY1Field, np);
        registerOrUnregisterLinkYMappingField(np.linkY.get());
        linkYButton.setSelected(np.linkY.get());
        linkYButton.selectedProperty().bindBidirectional(np.linkY);
        linkYButton.selectedProperty().addListener((o, oldValue, newValue) -> {
            registerOrUnregisterLinkYMappingField(newValue);
        });

        linkYMappingFieldListener.setAmountToStepBy((float) np.stepsY.get());
        stepsYField.setTextFormatter(new TextFormatter<>(createNumberConverter()));
        stepsYField.getTextFormatter().valueProperty().bindBidirectional((Property) np.stepsY);
        np.stepsY.addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                linkYMappingFieldListener.setAmountToStepBy((float) newValue);
            }
        });
        linkXMappingFieldListener.setAmountToStepBy((float) np.stepsX.get());
        stepsXField.setTextFormatter(new TextFormatter<>(createNumberConverter()));
        stepsXField.getTextFormatter().valueProperty().bindBidirectional((Property) np.stepsX);
        np.stepsX.addListener((o, oldValue, newValue) -> {
            if (newValue != null) {
                linkXMappingFieldListener.setAmountToStepBy((float) newValue);
            }
        });
    }

    private StringConverter<Float> createNumberConverter() {
        return new StringConverter<>() {

            @Override
            public String toString(Float object) {
                if (object == null) {
                    return "";
                }
                return df.format(object);
            }

            @Override
            public Float fromString(String string) {
                try {
                    return df.parse(string).floatValue();
                } catch (ParseException e) {
                    return null;
                }
            }
        };
    }

    private void registerOrUnregisterLinkYMappingField(boolean register) {
        mappingY0Field.setEditable(!register);
        mappingY1Field.setEditable(!register);
        if (register) {
            linkYMappingFieldListener.register();
        } else {
            linkYMappingFieldListener.unregister();
        }
    }

    private void registerOrUnregisterLinkXMappingField(boolean register) {
        mappingX0Field.setEditable(!register);
        mappingX1Field.setEditable(!register);
        if (register) {
            linkXMappingFieldListener.register();
        } else {
            linkXMappingFieldListener.unregister();
        }
    }

    private SpinnerValueFactory<Float> createFloatValueFactory(float initialValue, Property<Float> p) {
        var f = new MappingRangeSpinnerValueFactory(-5000, 5000, initialValue, 1, df);
        f.valueProperty().bindBidirectional(p);
        return f;
    }

    private SpinnerValueFactory<Integer> createIntegerValueFactory(int initialValue, Property<Integer> p) {
        var f = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, initialValue);
        f.valueProperty().bindBidirectional(p);
        return f;
    }

    @Data
    private class LinkMappingFieldListener {

        private final Spinner<Float> mapping0Field;

        private final Spinner<Float> mapping1Field;

        private final ObservableGameMainPaneProperties np;

        public void register() {
            var mapping0 = (MappingRangeSpinnerValueFactory) mapping0Field.getValueFactory();
            var mapping1 = (MappingRangeSpinnerValueFactory) mapping1Field.getValueFactory();
            mapping0.setIncrementCallback((s) -> {
                mapping0.setLock(true);
                mapping1.setLock(true);
                mapping1.increment(s);
                mapping0.setLock(false);
                mapping1.setLock(false);
            });
            mapping1.setIncrementCallback((s) -> {
                mapping0.setLock(true);
                mapping1.setLock(true);
                mapping0.increment(s);
                mapping0.setLock(false);
                mapping1.setLock(false);
            });
            mapping0.setDecrementCallback((s) -> {
                mapping0.setLock(true);
                mapping1.setLock(true);
                mapping1.decrement(s);
                mapping0.setLock(false);
                mapping1.setLock(false);
            });
            mapping1.setDecrementCallback((s) -> {
                mapping0.setLock(true);
                mapping1.setLock(true);
                mapping0.decrement(s);
                mapping0.setLock(false);
                mapping1.setLock(false);
            });
        }

        public void setAmountToStepBy(float amount) {
            var mapping0 = (MappingRangeSpinnerValueFactory) mapping0Field.getValueFactory();
            mapping0.setAmountToStepBy(amount);
            var mapping1 = (MappingRangeSpinnerValueFactory) mapping1Field.getValueFactory();
            mapping1.setAmountToStepBy(amount);
        }

        public void unregister() {
            ((MappingRangeSpinnerValueFactory) (mapping0Field.getValueFactory())).setIncrementCallback(null);
            ((MappingRangeSpinnerValueFactory) (mapping1Field.getValueFactory())).setIncrementCallback(null);
            ((MappingRangeSpinnerValueFactory) (mapping0Field.getValueFactory())).setDecrementCallback(null);
            ((MappingRangeSpinnerValueFactory) (mapping1Field.getValueFactory())).setDecrementCallback(null);
        }

    }
}
