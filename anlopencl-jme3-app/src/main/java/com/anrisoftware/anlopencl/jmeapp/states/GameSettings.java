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
package com.anrisoftware.anlopencl.jmeapp.states;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.anrisoftware.resources.images.external.IconSize;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Settings that apply to all games and can be changed in the game settings
 * menu.
 *
 * @author Erwin Müller
 */
public class GameSettings {

    /**
     * The locale of the game.
     */
    public final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(Locale.US);

    public void setLocale(Locale locale) {
        this.locale.setValue(locale);
    }

    public Locale getLocale() {
        return locale.get();
    }

    /**
     * The format how the game time is displayed.
     */
    public final DateTimeFormatter gameTimeFormat = DateTimeFormatter.RFC_1123_DATE_TIME;

    public DateTimeFormatter getGameTimeFormat() {
        return gameTimeFormat;
    }

    /**
     * The length of the game tick.
     */
    public final FloatProperty tickLength = new SimpleFloatProperty(1 / 30f);

    public void setTickLength(float tickLength) {
        this.tickLength.setValue(tickLength);
    }

    public float getTickLength() {
        return tickLength.get();
    }

    /**
     * The length of the game tick long.
     */
    public final FloatProperty tickLongLength = new SimpleFloatProperty(1 / 15f);

    public void setTickLongLength(float tickLongLength) {
        this.tickLongLength.setValue(tickLongLength);
    }

    public float getTickLongLength() {
        return tickLongLength.get();
    }

    public boolean windowFullscreen;

    public int windowWidth;

    public int windowHeight;

    public final ObjectProperty<IconSize> iconSize = new SimpleObjectProperty<>(IconSize.LARGE);

    public void setIconSize(IconSize iconSize) {
        this.iconSize.set(iconSize);
    }

    public IconSize getIconSize() {
        return iconSize.get();
    }

    public final ObjectProperty<TextPosition> textPosition = new SimpleObjectProperty<>(TextPosition.NONE);

    public void setTextPosition(TextPosition textPosition) {
        this.textPosition.set(textPosition);
    }

    public TextPosition getTextPosition() {
        return textPosition.get();
    }

    public final DoubleProperty mainSplitPosition = new SimpleDoubleProperty(0.71);

    public void setMainSplitPosition(double pos) {
        this.mainSplitPosition.set(pos);
    }

    public double getMainSplitPosition() {
        return mainSplitPosition.get();
    }

}
