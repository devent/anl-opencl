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
package com.anrisoftware.anlopencl.jmeapp.states;

import static com.jme3.input.KeyInput.KEY_Q;
import static javafx.scene.input.KeyCode.Q;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Provider;

import com.anrisoftware.anlopencl.jmeapp.messages.BuildClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.GameQuitMessage;
import com.jme3.input.controls.KeyTrigger;

import javafx.scene.input.KeyCodeCombination;

public class KeyMappingsProvider implements Provider<Map<String, KeyMapping>> {

    private final Map<String, KeyMapping> map;

    public final Map<String, KeyMapping> commandsButtons;

    public KeyMappingsProvider() {
        var map = new HashMap<String, KeyMapping>();
        var commandsButtons = new HashMap<String, KeyMapping>();
        // general
        map.put("QUIT_MAPPING", new KeyMapping("QUIT_MAPPING", Optional.of(new KeyCodeCombination(Q, CONTROL_DOWN)),
                Optional.of(new KeyTrigger(KEY_Q)), new GameQuitMessage()));
        map.put("BUILD_MAPPING",
                new KeyMapping("BUILD_MAPPING", Optional.empty(), Optional.empty(), new BuildClickedMessage()));
        // done
        map.putAll(commandsButtons);
        this.commandsButtons = Collections.unmodifiableMap(commandsButtons);
        this.map = Collections.unmodifiableMap(map);
    }

    @Override
    public Map<String, KeyMapping> get() {
        return map;
    }

}
