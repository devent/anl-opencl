/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Model
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
 * ANL-OpenCL :: JME3 - App - Model is a derivative work based on Josua Tippetts' C++ library:
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
 * ANL-OpenCL :: JME3 - App - Model bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl.jmeapp.model;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.anrisoftware.anlopencl.jmeapp.messages.TextPosition;
import com.anrisoftware.resources.images.external.IconSize;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * Settings that apply to all games and can be changed in the game settings
 * menu.
 *
 * @author Erwin Müller
 */
public class ObservableGameSettings {

    /**
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @Data
    public static class GameSettings {

        public Locale locale = Locale.US;

        @JsonIgnore
        public DateTimeFormatter gameTimeFormat = DateTimeFormatter.RFC_1123_DATE_TIME;

        public float tickLength = 1 / 30f;

        public float tickLongLength = 1 / 15f;

        public boolean windowFullscreen = false;

        public int windowWidth = 1024;

        public int windowHeight = 768;

        public IconSize iconSize = IconSize.MEDIUM;

        public TextPosition textPosition = TextPosition.RIGHT;

        public double mainSplitPosition = 0.71;

        public Path tempDir = Path.of(System.getProperty("java.io.tmpdir"));

        public Path editorPath = null;
    }

    public final ObjectProperty<Locale> locale;

    public final ObjectProperty<DateTimeFormatter> gameTimeFormat;

    public final FloatProperty tickLength;

    public final FloatProperty tickLongLength;

    public final BooleanProperty windowFullscreen;

    public final IntegerProperty windowWidth;

    public final IntegerProperty windowHeight;

    public final ObjectProperty<IconSize> iconSize;

    public final ObjectProperty<TextPosition> textPosition;

    public final DoubleProperty mainSplitPosition;

    public final ObjectProperty<Path> tempDir;

    public final ObjectProperty<Path> editorPath;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public ObservableGameSettings(GameSettings p) {
        this.locale = JavaBeanObjectPropertyBuilder.create().bean(p).name("locale").build();
        this.gameTimeFormat = JavaBeanObjectPropertyBuilder.create().bean(p).name("gameTimeFormat").build();
        this.tickLength = JavaBeanFloatPropertyBuilder.create().bean(p).name("tickLength").build();
        this.tickLongLength = JavaBeanFloatPropertyBuilder.create().bean(p).name("tickLongLength").build();
        this.windowFullscreen = JavaBeanBooleanPropertyBuilder.create().bean(p).name("windowFullscreen").build();
        this.windowWidth = JavaBeanIntegerPropertyBuilder.create().bean(p).name("windowWidth").build();
        this.windowHeight = JavaBeanIntegerPropertyBuilder.create().bean(p).name("windowHeight").build();
        this.iconSize = JavaBeanObjectPropertyBuilder.create().bean(p).name("iconSize").build();
        this.textPosition = JavaBeanObjectPropertyBuilder.create().bean(p).name("textPosition").build();
        this.mainSplitPosition = JavaBeanDoublePropertyBuilder.create().bean(p).name("mainSplitPosition").build();
        this.tempDir = JavaBeanObjectPropertyBuilder.create().bean(p).name("tempDir").build();
        this.editorPath = JavaBeanObjectPropertyBuilder.create().bean(p).name("editorPath").build();
    }

    public void copy(GameSettings other) {
        locale.set(other.locale);
        gameTimeFormat.set(other.gameTimeFormat);
        tickLength.set(other.tickLength);
        tickLongLength.set(other.tickLongLength);
        windowFullscreen.set(other.windowFullscreen);
        windowWidth.set(other.windowWidth);
        windowHeight.set(other.windowHeight);
        iconSize.set(other.iconSize);
        iconSize.set(other.iconSize);
        mainSplitPosition.set(other.mainSplitPosition);
        tempDir.set(other.tempDir);
        editorPath.set(other.editorPath);
    }
}
