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
package com.anrisoftware.anlopencl.jmeapp.states;

import static com.jme3.input.KeyInput.KEY_F10;
import static com.jme3.input.KeyInput.KEY_Q;
import static javafx.scene.input.KeyCode.F10;
import static javafx.scene.input.KeyCode.Q;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import com.anrisoftware.anlopencl.jmeapp.messages.BuildClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.GameQuitMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.ResetCameraMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsClickedMessage;
import com.jme3.input.controls.KeyTrigger;

import javafx.scene.input.KeyCodeCombination;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class KeyMappingsProvider implements Provider<Map<String, KeyMapping>> {

    private final Map<String, KeyMapping> map;

    public final Map<String, KeyMapping> commandsButtons;

    public KeyMappingsProvider() {
        var map = new HashMap<String, KeyMapping>();
        var commandsButtons = new HashMap<String, KeyMapping>();
        // general
        map.put("QUIT_MAPPING", KeyMapping.create("QUIT_MAPPING", new KeyCodeCombination(Q, CONTROL_DOWN),
                new KeyTrigger(KEY_Q), new GameQuitMessage()));
        map.put("BUILD_MAPPING", KeyMapping.create("BUILD_MAPPING", new BuildClickedMessage()));
        map.put("SETTINGS_MAPPING", KeyMapping.create("SETTINGS_MAPPING", new SettingsClickedMessage()));
        // camera
        map.put("RESET_CAMERA_MAPPING", KeyMapping.create("RESET_CAMERA_MAPPING", new KeyCodeCombination(F10),
                new KeyTrigger(KEY_F10), new ResetCameraMessage()));
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
