/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Model
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
 */
package com.anrisoftware.anlopencl.jmeapp.model;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Provider;

import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties.GameMainPaneProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides the {@link GameMainPaneProperties} and saves/loads the properties
 * from/to file.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class GameMainPanePropertiesProvider implements Provider<ObservableGameMainPaneProperties> {

    private final static String LAST_MAIN_PANE_PROPERTIES_FILE = GameMainPanePropertiesProvider.class.getPackageName()
            + ".last_main_pane_properties_file";

    private static final File DEFAULT_LAST_MAIN_PANE_PROPERTIES_FILE = new File(
            System.getProperty("user.home") + "/.anlopencl-last.yaml");

    private final GameMainPaneProperties p;

    private final ObservableGameMainPaneProperties op;

    @Inject
    private ObjectMapper mapper;

    public GameMainPanePropertiesProvider() {
        this.p = new GameMainPaneProperties();
        this.op = new ObservableGameMainPaneProperties(p);
    }

    @Override
    public ObservableGameMainPaneProperties get() {
        return op;
    }

    @SneakyThrows
    public void save() {
        File file = getFile();
        log.debug("Save properties to {}", file);
        mapper.writeValue(file, p);
    }

    @SneakyThrows
    public void load() {
        var file = getFile();
        if (file.exists()) {
            log.debug("Load properties from {}", file);
            var p = mapper.readValue(file, GameMainPaneProperties.class);
            op.copy(p);
        }
    }

    private File getFile() {
        var argsFile = System.getProperty(LAST_MAIN_PANE_PROPERTIES_FILE);
        if (argsFile != null) {
            return new File(argsFile);
        }
        return DEFAULT_LAST_MAIN_PANE_PROPERTIES_FILE;
    }
}
