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

import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.system.MemoryStack.stackMallocInt;

import java.nio.IntBuffer;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.jme3.opencl.CommandQueue;
import com.jme3.opencl.Event;
import com.jme3.opencl.Kernel;
import com.jme3.opencl.Kernel.WorkSize;
import com.jme3.opencl.Program;
import com.jme3.opencl.lwjgl.LwjglContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnlKernel {

    private static final String ANLOPENCL_USE_OPENCL = "-DANLOPENCL_USE_OPENCL";

    private static final String ANLOPENCL_USE_DOUBLE = "-DANLOPENCL_USE_DOUBLE";

    public interface AnlKernelFactory {

        AnlKernel create(LwjglContext context);
    }

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

    private Kernel kernel;

    private Program lprogramKernel;

    @Inject
    public AnlKernel() {
        this.options = ANLOPENCL_USE_OPENCL;
    }

    public Event run1(CommandQueue queue, WorkSize globalWorkSize, Object... args) {
        return kernel.Run1(queue, globalWorkSize, args);
    }

    public Event run2(CommandQueue queue, WorkSize globalWorkSize, WorkSize workGroupSize, Object... args) {
        return kernel.Run2(queue, globalWorkSize, workGroupSize, args);
    }

    public void createKernel(String name) {
        kernel = lprogramKernel.createKernel(name).register();
        log.debug("Kernel created {}", kernel);
    }

    public void setUseDouble(boolean useDouble) {
        this.options = useDouble ? ANLOPENCL_USE_OPENCL + " " + ANLOPENCL_USE_DOUBLE : ANLOPENCL_USE_OPENCL;
    }

    public void compileKernel(String kernelSource) throws Exception {
        var clc = context.getContext();
        var source = new StringBuilder();
        var err = stackMallocInt(1);
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
    }

    public void buildLib() throws Exception {
        createLib();
        buildHeaders();
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
        this.lprogramLib = programLib;
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
