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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.jocl.cl_context;
import org.jocl.cl_program;

import com.anrisoftware.easycl.corejocl.Program;
import com.anrisoftware.easycl.corejocl.ProgramFactory;
import com.google.inject.assistedinject.Assisted;

public class AnlKernel implements Runnable, AutoCloseable {

    private static final String ANLOPENCL_USE_OPENCL = "-DANLOPENCL_USE_OPENCL";

    private static final String ANLOPENCL_USE_DOUBLE = "-DANLOPENCL_USE_DOUBLE";

    public interface AnlKernelFactory {

        AnlKernel create(Supplier<cl_context> context);
    }

    private final Supplier<cl_context> context;

    @Inject
    private ProgramFactory programFactory;

    @Inject
    private Map<String, String> sources;

    @Inject
    private LibSourcesProvider libSources;

    private List<Supplier<cl_program>> headers;

    private Supplier<cl_program> lib;

    private Program program;

    private String options;

    @Inject
    public AnlKernel(@Assisted Supplier<cl_context> context) {
        this.context = context;
        this.options = ANLOPENCL_USE_OPENCL;
    }

    public void setUseDouble(boolean useDouble) {
        this.options = useDouble ? ANLOPENCL_USE_OPENCL + " " + ANLOPENCL_USE_DOUBLE : ANLOPENCL_USE_OPENCL;
    }

    public void compileKernel(String kernelSource) throws Exception {
        var source = new StringBuilder(kernelSource);
        source.append(sources.get("kiss09.cl"));
        source.append(sources.get("random.cl"));
        var p = programFactory.create(context, source.toString());
        var program = (Program) p;
        program.compileProgram(options, headers);
        this.program = program;
    }

    public void buildLib() throws Exception {
        createHeaders();
        createLib();
    }

    private void createLib() throws Exception {
        var p = programFactory.create(context, libSources.get());
        try (var program = (Program) p) {
            program.compileProgram(options);
            this.lib = program.linkLibrary();
        } catch (Exception e) {
            throw e;
        }
    }

    private void createHeaders() {
        headers = new ArrayList<>();
        putHeader("opencl_utils.h");
        putHeader("qsort.h");
        putHeader("utility.h");
        putHeader("hashing.h");
        putHeader("noise_lut.h");
        putHeader("noise_gen.h");
        putHeader("imaging.h");
        putHeader("kernel.h");
    }

    private void putHeader(String name) {
        headers.add(programFactory.create(context, sources.get(name), name));
    }

    @Override
    public void run() {

    }

    @Override
    public void close() throws Exception {
        ((AutoCloseable) lib).close();
        for (Supplier<cl_program> header : headers) {
            ((AutoCloseable) header).close();
        }
        program.close();
    }

}
