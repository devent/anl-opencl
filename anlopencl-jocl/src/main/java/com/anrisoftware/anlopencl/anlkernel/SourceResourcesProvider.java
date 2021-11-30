/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JOCL
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
 * ANL-OpenCL :: JOCL is a derivative work based on Josua Tippetts' C++ library:
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
 * ANL-OpenCL :: JOCL bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl.anlkernel;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import lombok.SneakyThrows;

/**
 * Loads a source from the classpath.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class SourceResourcesProvider implements Provider<Map<String, String>> {

    /**
     * Loads the specified source from the classpath.
     *
     * @param name the {@link String} name of the source, for example
     *             <code>hashing.h</code>
     * @return the {@link String} content of the resource.
     * @throws IOException if there was an error reading the resource.
     */
    public static String loadResource(String name) throws IOException {
        var resname = String.format("/%s", name);
        var r = AnlKernel.class.getResource(resname);
        notNull(r, "Resource '%s' is null", name);
        return IOUtils.toString(r, UTF_8);
    }

    private final Map<String, String> sources;

    public SourceResourcesProvider() {
        this.sources = Collections.unmodifiableMap(loadResources());
    }

    @SneakyThrows
    private Map<String, String> loadResources() {
        var map = new HashMap<String, String>();
        putResource(map, "opencl_utils.h");
        putResource(map, "opencl_utils.c");
        putResource(map, "qsort.h");
        putResource(map, "qsort.c");
        putResource(map, "utility.h");
        putResource(map, "utility.c");
        putResource(map, "hashing.h");
        putResource(map, "hashing.c");
        putResource(map, "noise_lut.h");
        putResource(map, "noise_lut.c");
        putResource(map, "noise_gen.h");
        putResource(map, "noise_gen.c");
        putResource(map, "imaging.h");
        putResource(map, "imaging.c");
        putResource(map, "kernel.h");
        putResource(map, "kernel.c");
        putResource(map, "extern/RandomCL/generators/kiss09.cl");
        putResource(map, "random.cl");
        return map;
    }

    private void putResource(Map<String, String> map, String name) throws IOException {
        map.put(FilenameUtils.getName(name), loadResource(name));
    }

    @Override
    public Map<String, String> get() {
        return sources;
    }

}
