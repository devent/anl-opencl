/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - OpenCL
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
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - OpenCL bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl.jme.opencl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.system.MemoryStack.stackMallocInt;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.collections.impl.factory.Maps;
import org.lwjgl.system.MemoryStack;

import com.google.inject.assistedinject.Assisted;
import com.jme3.opencl.CommandQueue;
import com.jme3.opencl.Event;
import com.jme3.opencl.Kernel;
import com.jme3.opencl.Kernel.WorkSize;
import com.jme3.opencl.Program;
import com.jme3.opencl.lwjgl.LwjglContext;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnlKernel {

    private static final String ANLOPENCL_USE_OPENCL = "-DANLOPENCL_USE_OPENCL";

    private static final String ANLOPENCL_USE_DOUBLE = "-DANLOPENCL_USE_DOUBLE";

    private static String insert_localMapRange;

    static {
        try {
            insert_localMapRange = IOUtils.toString(AnlKernel.class.getResource("insert_local_map_range.txt"), UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface AnlKernelFactory {

        AnlKernel create(LwjglContext context);
    }

    private final Map<String, Kernel> kernels;

    @Inject
    @Assisted
    private LwjglContext context;

    @Inject
    private LibSourcesProvider libSources;

    @Inject
    private KernelExtraSourcesProvider kernelExtraSources;

    @Inject
    private HeaderProgramsBuilder headersBuilder;

    private String options;

    private Program lprogramLib;

    private Program lprogramKernel;

    private boolean buildLibFinish;

    private boolean compileFinish;

    @Inject
    public AnlKernel() {
        this.kernels = Maps.mutable.empty();
        this.options = ANLOPENCL_USE_OPENCL;
        this.buildLibFinish = false;
        this.compileFinish = false;
    }

    public Event run1(String name, CommandQueue queue, WorkSize globalWorkSize, Object... args) {
        return kernels.get(name).Run1(queue, globalWorkSize, args);
    }

    public void run1NoEvent(String name, CommandQueue queue, WorkSize globalWorkSize, Object... args) {
        kernels.get(name).Run1NoEvent(queue, globalWorkSize, args);
    }

    public Event run2(String name, CommandQueue queue, WorkSize globalWorkSize, WorkSize workGroupSize,
            Object... args) {
        return kernels.get(name).Run2(queue, globalWorkSize, workGroupSize, args);
    }

    public void run2NoEvent(String name, CommandQueue queue, WorkSize globalWorkSize, WorkSize workGroupSize,
            Object... args) {
        kernels.get(name).Run2NoEvent(queue, globalWorkSize, workGroupSize, args);
    }

    public void createKernel(String name) {
        var kernel = lprogramKernel.createKernel(name);
        kernels.put(name, kernel);
        log.debug("Kernel created {}", kernel);
    }

    public boolean isKernelCreated(String name) {
        return kernels.containsKey(name);
    }

    public void releaseKernel(String name) {
        var kernel = kernels.remove(name);
        if (kernel != null) {
            log.debug("Release kernel {}", name);
            kernel.release();
        }
    }

    public void setUseDouble(boolean useDouble) {
        this.options = useDouble ? ANLOPENCL_USE_OPENCL + " " + ANLOPENCL_USE_DOUBLE : ANLOPENCL_USE_OPENCL;
    }

    public void compileProgram(String kernelSource, Map<String, Object> variables) throws Exception {
        var binding = new Binding(variables);
        var shell = new GroovyShell(binding);
        var processedSource = shell.evaluate(insert_localMapRange).toString();
        variables.put("insert_localMapRange", processedSource);
        var b = new StringBuilder();
        b.append("\"\"\"");
        b.append(kernelSource);
        b.append("\"\"\"");
        compileProgram(shell.evaluate(b.toString()).toString());
    }

    public void compileProgram(String kernelSource) throws Exception {
        try (var s = MemoryStack.stackPush()) {
            var clc = context.getContext();
            var source = new StringBuilder();
            var err = s.mallocInt(1);
            source.append(kernelExtraSources.get());
            source.append(kernelSource);
            var clprogramKernel = clCreateProgramWithSource(clc, source.toString(), err);
            log.debug("Kernel program created: {}", clprogramKernel);
            checkCLError(err.get(0));
            var programKernel = new LwjglProgramEx(clprogramKernel, context);
            programKernel.compile(options, headersBuilder.getHeaders(), headersBuilder.getHeaderNames());
            log.debug("Kernel program compiled: {}", programKernel);
            this.lprogramKernel = LwjglProgramEx.link(context, "", new Program[] { lprogramLib, programKernel });
            log.debug("Kernel program linked: {}", lprogramKernel);
            this.compileFinish = true;
        }
    }

    public void releaseProgram() {
        if (compileFinish) {
            log.debug("Release kernel program");
            lprogramKernel.release();
            this.compileFinish = false;
        }
    }

    public boolean isProgramCompiled() {
        return compileFinish;
    }

    public void buildLib() throws Exception {
        createLib();
        buildHeaders();
        this.buildLibFinish = true;
    }

    public boolean isBuildLibFinish() {
        return buildLibFinish;
    }

    private void buildHeaders() {
        headersBuilder.createPrograms(context);
    }

    private void createLib() {
        var clc = context.getContext();
        var err = stackMallocInt(1);
        var clprogramLib = clCreateProgramWithSource(clc, libSources.get(), err);
        log.debug("Program created: {}", clprogramLib);
        checkCLError(err.get(0));
        var programLib = new LwjglProgramEx(clprogramLib, context);
        programLib.compile(options);
        this.lprogramLib = programLib.register();
    }

    static void checkCLError(IntBuffer errcode) {
        checkCLError(errcode.get(errcode.position()));
    }

    static void checkCLError(int errcode) {
        if (errcode != CL_SUCCESS) {
            throw new RuntimeException(String.format("OpenCL error [%d]", errcode));
        }
    }

}
