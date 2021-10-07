/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - OpenCL
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
 * ANL-OpenCL :: JME3 - OpenCL is a derivative work based on Josua Tippetts' C++ library:
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
package com.anrisoftware.anlopencl.jme.opencl;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Provides the appended ANL-OpenCL sources.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class LibSourcesProvider implements Provider<String> {

    private final String sources;

    @Inject
    public LibSourcesProvider(SourceResourcesProvider sources) {
        this.sources = appendSources(sources.get());
    }

    private String appendSources(Map<String, String> sources) {
        var s = new StringBuilder();
        s.append(sources.get("opencl_utils.h"));
        s.append(sources.get("opencl_utils.c"));
        s.append(sources.get("qsort.h"));
        s.append(sources.get("qsort.c"));
        s.append(sources.get("utility.h"));
        s.append(sources.get("utility.c"));
        s.append(sources.get("hashing.h"));
        s.append(sources.get("hashing.c"));
        s.append(sources.get("noise_lut.h"));
        s.append(sources.get("noise_lut.c"));
        s.append(sources.get("noise_gen.h"));
        s.append(sources.get("noise_gen.c"));
        s.append(sources.get("imaging.h"));
        s.append(sources.get("imaging.c"));
        s.append(sources.get("kernel.h"));
        s.append(sources.get("kernel.c"));
        return s.toString();
    }

    @Override
    public String get() {
        return sources;
    }

}
