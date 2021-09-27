/**
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
 */
package com.anrisoftware.anlopencl.jme.opencl;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import com.jme3.asset.AssetManager;
import com.jme3.opencl.Program;
import com.jme3.system.JmeContext;

public class AnlKernel implements Runnable {

    private static final String ANLOPENCL_USE_OPENCL = "-DANLOPENCL_USE_OPENCL";

    private static final String ANLOPENCL_USE_DOUBLE = "-DANLOPENCL_USE_DOUBLE";

    public interface AnlKernelFactory {

        AnlKernel create(JmeContext context);
    }

    @Inject
    private JmeContext context;

    @Inject
    private AssetManager assetManager;

    @Inject
    private Map<String, String> sources;

    @Inject
    private LibSourcesProvider libSources;

    private String options;

    @Inject
    public AnlKernel() {
        this.options = ANLOPENCL_USE_OPENCL;
    }

    public void setUseDouble(boolean useDouble) {
        this.options = useDouble ? ANLOPENCL_USE_OPENCL + " " + ANLOPENCL_USE_DOUBLE : ANLOPENCL_USE_OPENCL;
    }

    public void compileKernel(String kernelSource) throws Exception {
        var c = context.getOpenCLContext();
        var source = new StringBuilder(kernelSource);
        source.append(sources.get("kiss09.cl"));
        source.append(sources.get("random.cl"));
        var program = c.createProgramFromSourceCode(source.toString());
        program.build();
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
        var c = context.getOpenCLContext();
        var include = new StringBuilder();
        include.append(String.format("#define %s", "ANLOPENCL_USE_OPENCL"));
        var p = c.createProgramFromSourceFilesWithInclude(assetManager, include, res);
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

}
