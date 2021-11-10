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

import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.lwjgl.system.MemoryStack;

import com.jme3.opencl.Program;
import com.jme3.opencl.lwjgl.LwjglContext;
import com.jme3.opencl.lwjgl.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Builds the {@link Program}s of the ANL-OpenCL library.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class HeaderProgramsBuilder {

    private final List<Program> headers;

    private final List<String> headerNames;

    private final Map<String, String> sources;

    @Inject
    public HeaderProgramsBuilder(SourceResourcesProvider sources) {
        this.headers = new ArrayList<>();
        this.headerNames = new ArrayList<>();
        this.sources = sources.get();
    }

    public List<Program> getHeaders() {
        return headers;
    }

    public List<String> getHeaderNames() {
        return headerNames;
    }

    public void createPrograms(LwjglContext context) {
        try (var stack = stackPush()) {
            createProgram(stack, context, "opencl_utils.h");
            createProgram(stack, context, "qsort.h");
            createProgram(stack, context, "utility.h");
            createProgram(stack, context, "hashing.h");
            createProgram(stack, context, "noise_lut.h");
            createProgram(stack, context, "noise_gen.h");
            createProgram(stack, context, "imaging.h");
            createProgram(stack, context, "kernel.h");
        }
    }

    private void createProgram(MemoryStack stack, LwjglContext context, String name) {
        var source = sources.get(name);
        var err = stack.mallocInt(1);
        var clprogram = clCreateProgramWithSource(context.getContext(), source, err);
        var ret = err.get(0);
        Utils.checkError(ret, "clCreateProgramWithSource");
        var program = new LwjglProgramEx(clprogram, context).register();
        log.debug("Program created '{}': {}", name, clprogram);
        headers.add(program);
        headerNames.add(name);
    }
}
