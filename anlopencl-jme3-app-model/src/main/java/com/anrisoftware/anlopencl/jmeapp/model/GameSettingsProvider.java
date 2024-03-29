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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;

import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameSettings.GameSettings;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides the {@link GameSettings} and saves/loads the properties from/to
 * file.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class GameSettingsProvider implements Provider<ObservableGameSettings> {

    private final static String GAME_SETTINGS_FILE = GameSettingsProvider.class.getPackageName()
            + ".game_settings_file";

    private static final File DEFAULT_GAME_SETTINGS_FILE = new File(
            System.getProperty("user.home") + "/.anlopencl-settings.yaml");

    private final GameSettings p;

    private final ObservableGameSettings op;

    @Inject
    private ObjectMapper mapper;

    public GameSettingsProvider() throws IOException {
        this.p = new GameSettings();
        this.op = new ObservableGameSettings(p);
    }

    @Override
    public ObservableGameSettings get() {
        return op;
    }

    @SneakyThrows
    public void save() {
        File file = getFile();
        log.debug("Save settings to {}", file);
        mapper.writeValue(file, p);
    }

    @SneakyThrows
    public void load() {
        var file = getFile();
        if (file.exists()) {
            log.debug("Load settings from {}", file);
            var p = mapper.readValue(file, GameSettings.class);
            if (p.editorPath == null) {
                p.editorPath = lookupEditorPaths();
            }
            op.copy(p);
        }
    }

    private Path lookupEditorPaths() {
        var system = System.getProperty("os.name").toLowerCase(Locale.US);
        Path path = null;
        if (system.startsWith("linux")) {
            path = Path.of("/usr/bin/kwrite");
            if (Files.isExecutable(path)) {
                return path;
            }
            path = Path.of("/usr/bin/gedit");
            if (Files.isExecutable(path)) {
                return path;
            }
            path = Path.of("/usr/bin/gnome-text-editor");
            if (Files.isExecutable(path)) {
                return path;
            }
        } else if (system.startsWith("windows")) {
            path = Path.of("C:\\OS\\Program Files\\Notepad++\\notepad++.exe");
            if (Files.isReadable(path)) {
                return path;
            }
        }
        return path;
    }

    private File getFile() {
        var argsFile = System.getProperty(GAME_SETTINGS_FILE);
        if (argsFile != null) {
            return new File(argsFile);
        }
        return DEFAULT_GAME_SETTINGS_FILE;
    }
}
