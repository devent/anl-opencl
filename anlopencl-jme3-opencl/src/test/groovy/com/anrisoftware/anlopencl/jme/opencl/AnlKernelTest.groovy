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
import static org.lwjgl.system.MemoryStack.*

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CLImageFormat
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

import com.anrisoftware.anlopencl.jme.opencl.AnlKernel.AnlKernelFactory
import com.google.inject.Guice
import com.google.inject.Injector
import com.jme3.opencl.MappingAccess
import com.jme3.opencl.Kernel.WorkSize
import com.jme3.opencl.lwjgl.LwjglBuffer
import com.jme3.opencl.lwjgl.LwjglCommandQueue
import com.jme3.opencl.lwjgl.LwjglContext
import com.jme3.opencl.lwjgl.LwjglDevice
import com.jme3.opencl.lwjgl.LwjglImage
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

    @Test
    void "map2D kernel"() {
        MemoryStack.stackPush().withCloseable { s ->
            def anlKernel = injector.getInstance(AnlKernelFactory).create(context)
            anlKernel.buildLib()
            anlKernel.compileKernel("""
#include <opencl_utils.h>
#include <noise_gen.h>
#include <imaging.h>
#include <kernel.h>

const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST;
const int dim = sizeof(vector3) / sizeof(float);

kernel void map2d_image(
global struct SMappingRanges *ranges,
float z,
global float *coord,
write_only image2d_t output,
global unsigned char *doutput
) {
    int g0 = get_global_id(0);
    int g1 = get_global_id(1);
    int l0 = get_local_id(0);
    int l1 = get_local_id(1);
    int w = get_global_size(0);
    int h = get_global_size(1);
    if (l0 == 0) {
        map2D(coord, calc_seamless_none, *ranges, w, h, z);
    }
    int i = (g0 * w + g1) * dim;
    //printf("[map2d_image] %d (%d,%d) g=(%d,%d) l=(%d,%d) coord=%f,%f,%f\\n", i, w, h, g0, g1, l0, l1, coord[i], coord[i+1], coord[i+2]);
    float a = 0.5;
    float r = value_noise3D(coord[i], 200, noInterp);
    float g = value_noise3D(coord[i], 200, noInterp);
    float b = value_noise3D(coord[i], 200, noInterp);
    write_imagef(output, (int2)(g0, g1), (float4)(r, g, b, a));
    doutput[(g1 * 4 + g0) * 4 + 0] = r * 256;
    doutput[(g1 * 4 + g0) * 4 + 1] = g * 256;
    doutput[(g1 * 4 + g0) * 4 + 2] = b * 256;
    doutput[(g1 * 4 + g0) * 4 + 3] = a * 256;
    printf("[map2d_image] %d (%d,%d) g=(%d,%d) l=(%d,%d) coord=%f,%f,%f argb=%f,%f,%f,%f\\n", i, w, h, g0, g1, l0, l1, coord[i], coord[i+1], coord[i+2],a,r,g,b);
}
""")
            anlKernel.createKernel("map2d_image")
            def err = s.mallocInt(1)
            int width = 4
            int height = 4
            float z = 99
            def ranges = MappingRanges.createWithBuffer(s)
            def rangesb = new LwjglBuffer(ranges.getClBuffer(s, clcontext))
            int size = width * height
            def format = CLImageFormat.malloc(s)
            format.image_channel_order(CL_RGBA)
            format.image_channel_data_type(CL_UNORM_INT8)
            def image = BufferUtils.createShortBuffer(size * channel_size)
            def output = new LwjglImage(clCreateImage2D(clcontext, CL_MEM_WRITE_ONLY | CL_MEM_USE_HOST_PTR, format, width, height, 0, image, err));
            checkCLError(err.get(0))
            def coordb = new LwjglBuffer(clCreateBuffer(clcontext, CL_MEM_READ_WRITE, size * vector3_size, err))
            checkCLError(err.get(0))
            def doutput = BufferUtils.createShortBuffer(size * channel_size)
            def doutputb = new LwjglBuffer(clCreateBuffer(clcontext, CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR, doutput, err))
            checkCLError(err.get(0))
            def work = new WorkSize(width, height)
            def event = anlKernel.run1(queue, work, rangesb, z, coordb, output, doutputb)
            event.waitForFinished()

            def doutputbb = doutputb.map(queue, doutput.limit(), 0, MappingAccess.MAP_READ_ONLY)
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int a = (x + y * height) * 4
                    int r = (x + y * height) * 4 + 1
                    int g = (x + y * height) * 4 + 2
                    int b = (x + y * height) * 4 + 3
                    println "($a,$r,$g,$b ${doutputbb.get(a)&0xFF}/${doutputbb.get(r)&0xFF}/${doutputbb.get(g)&0xFF}/${doutputbb.get(b)&0xFF})"
                }
            }
            doutputb.unmap(queue, doutputbb)

            def out = BufferUtils.createByteBuffer(size * channel_size)
            BufferUtils.zeroBuffer(out)
            output.readImage(queue, out, [0, 0, 0] as long[], [width, height, 1] as long[], 0, 0)
            println output.getWidth()
            println output.getHeight()
            println output.rowPitch
            println output.slicePitch
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    int a = (x + y * height) * 4
                    int r = (x + y * height) * 4 + 1
                    int g = (x + y * height) * 4 + 2
                    int b = (x + y * height) * 4 + 3
                    println "($a,$r,$g,$b ${out.get(a)&0xFF}/${out.get(r)&0xFF}/${out.get(g)&0xFF}/${out.get(b)&0xFF})"
                }
            }
        }
    }

    static Injector injector

    static int vector3_size = 4

    static int channel_size = 4

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
