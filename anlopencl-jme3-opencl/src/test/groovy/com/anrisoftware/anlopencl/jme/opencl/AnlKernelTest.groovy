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
package com.anrisoftware.anlopencl.jme.opencl

import static com.anrisoftware.anlopencl.jme.opencl.LwjglUtils.*
import static org.lwjgl.opencl.CL10.*
import static org.lwjgl.system.MemoryStack.stackMallocFloat
import static org.lwjgl.system.MemoryStack.stackMallocInt

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lwjgl.system.MemoryUtil

import com.anrisoftware.anlopencl.jme.opencl.AnlKernel.AnlKernelFactory
import com.google.inject.Guice
import com.google.inject.Injector
import com.jme3.opencl.Kernel.WorkSize
import com.jme3.opencl.lwjgl.LwjglBuffer
import com.jme3.opencl.lwjgl.LwjglCommandQueue
import com.jme3.opencl.lwjgl.LwjglContext
import com.jme3.opencl.lwjgl.LwjglDevice
import com.jme3.opencl.lwjgl.LwjglPlatform

/**
 * @see AnlKernel
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class AnlKernelTest {

    long clplatform

    long cldevice

    long clcontext

    long clqueue

    LwjglDevice device

    LwjglContext context

    LwjglCommandQueue queue

    @Test
    void "build compile kernel"() {
        def anlKernel = injector.getInstance(AnlKernelFactory).create(context)
        anlKernel.buildLib()
        anlKernel.compileKernel("""
#include <opencl_utils.h>
#include <noise_gen.h>
#include <kernel.h>

kernel void test_no_args(
) {
    int id0 = get_global_id(0);
    printf("[test_no_args] id0=%d\\n", id0);
}

kernel void value_noise2D_noInterp(
global float2 *input,
global float *output
) {
    int id0 = get_global_id(0);
    printf("[value_noise2D_noInterp] id0=%d %f/%f\\n", id0, input[id0].x, input[id0].y);
    output[id0] = value_noise2D(input[id0], 200, noInterp);
    printf("[value_noise2D_noInterp] %f \\n", output[id0]);
}
""")
        anlKernel.createKernel("test_no_args")
        def workSize = new WorkSize(1)
        def event = anlKernel.run1(queue, workSize)
        event.waitForFinished()

        anlKernel.createKernel("value_noise2D_noInterp")
        def err = stackMallocInt(1)
        long size = 1 * 4
        def input = stackMallocFloat(2)
        input.put(0)
        input.put(0)
        input.flip()
        def inputb = new LwjglBuffer(clCreateBuffer(clcontext, CL_MEM_WRITE_ONLY | CL_MEM_USE_HOST_PTR, input, err))
        checkCLError(err.get(0))
        def outputb = new LwjglBuffer(clCreateBuffer(clcontext, CL_MEM_WRITE_ONLY, size, err))
        checkCLError(err.get(0))
        workSize = new WorkSize(1)
        event = anlKernel.run1(queue, workSize, inputb, outputb)
        event.waitForFinished()
        def out = stackMallocFloat(1)
        outputb.read(queue, MemoryUtil.memByteBuffer(out), size)
        println out.get(0)
    }

    static Injector injector

    @BeforeAll
    static void setup() {
        injector = Guice.createInjector(new AnlkernelModule())
    }

    @BeforeEach
    void setupContext() {
        ToStringBuilder.defaultStyle = ToStringStyle.SHORT_PREFIX_STYLE
        clplatform = createPlatform()
        cldevice = createDevice(clplatform)
        clcontext = createContext(cldevice)
        clqueue = createQueue(clcontext, cldevice)
        device = new LwjglDevice(cldevice, new LwjglPlatform(clplatform))
        context = new LwjglContext(clcontext, [device])
        queue = new LwjglCommandQueue(clqueue, device)
    }
}
