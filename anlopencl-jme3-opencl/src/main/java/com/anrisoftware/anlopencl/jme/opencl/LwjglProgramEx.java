/*
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

import static org.apache.commons.lang3.Validate.isTrue;
import static org.lwjgl.PointerBuffer.create;
import static org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_LOG;
import static org.lwjgl.opencl.CL10.CL_PROGRAM_DEVICES;
import static org.lwjgl.opencl.CL10.clGetProgramBuildInfo;
import static org.lwjgl.opencl.CL10.clGetProgramInfo;
import static org.lwjgl.system.MemoryStack.stackASCII;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackMallocLong;
import static org.lwjgl.system.MemoryStack.stackMallocPointer;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memByteBuffer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL12;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.jme3.opencl.Device;
import com.jme3.opencl.KernelCompilationException;
import com.jme3.opencl.Program;
import com.jme3.opencl.lwjgl.LwjglContext;
import com.jme3.opencl.lwjgl.LwjglDevice;
import com.jme3.opencl.lwjgl.LwjglProgram;
import com.jme3.opencl.lwjgl.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Extend {@link LwjglProgram} with compile and link.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class LwjglProgramEx extends LwjglProgram {

    private final LwjglContext context;

    public LwjglProgramEx(long program, LwjglContext context) {
        super(program, context);
        this.context = context;
    }

    public void compile(Device... devices) throws KernelCompilationException {
        compile(null, (Program[]) null, null, devices);
    }

    public void compile(String options, Device... devices) throws KernelCompilationException {
        compile(options, (Program[]) null, null, devices);
    }

    public void compile(String options, List<Program> headers, List<String> headerNames, Device... devices)
            throws KernelCompilationException {
        compile(options, headers != null ? headers.toArray(new Program[0]) : null,
                headerNames != null ? headerNames.toArray(new String[0]) : null, devices);
    }

    public void compile(String options, Program[] headers, String[] headerNames, Device... devices)
            throws KernelCompilationException {
        PointerBuffer deviceList = null;
        if (devices != null && devices.length > 0) {
            deviceList = createBuffer((d) -> ((LwjglDevice) d).getDevice(), devices);
        }
        PointerBuffer headersList = null;
        if (headers != null && headers.length > 0) {
            headersList = createBuffer((d) -> ((LwjglProgram) d).getProgram(), headers);
        }
        PointerBuffer headerNamesList = null;
        if (headerNames != null && headerNames.length > 0) {
            headerNamesList = createBuffer(headerNames);
        }
        int ret = CL12.clCompileProgram(getProgram(), deviceList, options, headersList, headerNamesList, null, 0);
        if (ret != CL10.CL_SUCCESS) {
            log.warn("Unable to compile program {}", this);
            if (ret == CL10.CL_BUILD_PROGRAM_FAILURE) {
                var device = getDevice();
                var buildLog = getBuildLog(device);
                log.error("Unable to compile program {}: {}", this, buildLog);
                throw new KernelCompilationException("Failed to build program", ret, buildLog);
            } else {
                Utils.checkError(ret, "clBuildProgram");
            }
        } else {
            log.info("Program compiled {}", this);
        }
    }

    public static Program link(LwjglContext context, String options, Program program, Device... devices)
            throws KernelCompilationException {
        return link(context, options, new Program[] { program }, devices);
    }

    public static Program link(LwjglContext context, String options, Program[] programs, Device... devices)
            throws KernelCompilationException {
        PointerBuffer deviceList = null;
        if (devices != null && devices.length > 0) {
            deviceList = createBuffer((d) -> ((LwjglDevice) d).getDevice(), devices);
        }
        isTrue(programs.length > 0, "No programs given.");
        PointerBuffer programList = null;
        programList = createBuffer((p) -> ((LwjglProgram) p).getProgram(), programs);
        var err = stackMallocInt(1);
        long lprogram = CL12.clLinkProgram(context.getContext(), deviceList, options, programList, null, 0, err);
        if (err.get(0) != CL10.CL_SUCCESS) {
            log.warn("Unable to link program {}", programs[0]);
            if (err.get(0) == CL12.CL_LINK_PROGRAM_FAILURE) {
                throw new KernelCompilationException("Failed to link program", err.get(0), "");
            } else {
                Utils.checkError(err.get(0), "clLinkProgram");
            }
        } else {
            log.info("Program linked {}", programs[0]);
        }
        return new LwjglProgramEx(lprogram, context);
    }

    private static <T> PointerBuffer createBuffer(Function<T, Long> pointer, T[] list) {
        var p = stackMallocPointer(list.length);
        for (T d : list) {
            p.put(pointer.apply(d));
        }
        p.flip();
        return p;
    }

    private static PointerBuffer createBuffer(String[] list) {
        var p = stackMallocPointer(list.length);
        for (String s : list) {
            var buffer = stackASCII(s, true);
            p.put(memAddress(buffer));
        }
        p.flip();
        return p;
    }

    private long getDevice() {
        var size = MemoryStack.stackMallocInt(1);
        int ret = clGetProgramInfo(getProgram(), CL_PROGRAM_DEVICES, (ByteBuffer) null, create(memAddress(size), 1));
        Utils.checkError(ret, "clGetProgramInfo");
        int count = size.get(0);
        var buffer = memByteBuffer(stackMallocLong(count));
        ret = clGetProgramInfo(getProgram(), CL_PROGRAM_DEVICES, buffer, null);
        Utils.checkError(ret, "clGetProgramInfo");
        return buffer.getLong(0);
    }

    private String getBuildLog(long device) {
        Utils.pointerBuffers[0].rewind();
        int ret = clGetProgramBuildInfo(getProgram(), device, CL_PROGRAM_BUILD_LOG, (ByteBuffer) null,
                Utils.pointerBuffers[0]);
        Utils.checkError(ret, "clGetProgramBuildInfo");
        int count = (int) Utils.pointerBuffers[0].get(0);
        final ByteBuffer buffer = BufferUtils.createByteBuffer(count);
        ret = clGetProgramBuildInfo(getProgram(), device, CL_PROGRAM_BUILD_LOG, buffer, null);
        Utils.checkError(ret, "clGetProgramBuildInfo");
        return MemoryUtil.memASCII(buffer);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("context", context).append("program", getProgram()).toString();
    }
}
