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
        anlKernel.compileProgram("""
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
            anlKernel.compileProgram("""
#include <opencl_utils.h>
#include <noise_gen.h>
#include <imaging.h>
#include <kernel.h>

#define a2vector3(a, i) ((vector3)(a[i], a[i+1], a[i+2]))

const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST;

kernel void map2d_image(
global struct SMappingRanges *ranges,
const float z,
const int dim,
global float *coord,
write_only image2d_t output,
global unsigned char *doutput
) {
    const int g0 = get_global_id(0);
    const int g1 = get_global_id(1);
    const int l0 = get_local_id(0);
    const int l1 = get_local_id(1);
    const int w = get_global_size(0);
    const int h = get_global_size(1);
    if (l0 == 0) {
        map2D(coord, calc_seamless_none, *ranges, w, h, z);
    }
    const int i = (g0 * w + g1) * dim;
    //printf("[map2d_image] %d (%d,%d) g=(%d,%d) l=(%d,%d) coord=%f,%f,%f\\n", i, w, h, g0, g1, l0, l1, coord[i], coord[i+1], coord[i+2]);
    const float a = 0.5;
    const float r = value_noise3D(a2vector3(coord, i), 200, noInterp);
    const float g = r;
    const float b = r;
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
            int width = 2
            int height = 2
            float z = 0
            int dim = vector3_size
            def ranges = MappingRanges.createWithBuffer(s).setDefault()
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
            def event = anlKernel.run1("map2d_image", queue, work, rangesb, z, dim, coordb, output, doutputb)
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

    @Test
    void "map2D kernel vector3"() {
        MemoryStack.stackPush().withCloseable { s ->
            def anlKernel = injector.getInstance(AnlKernelFactory).create(context)
            int width = 8
            int height = 8
            anlKernel.buildLib()
            anlKernel.compileProgram("""
#include <opencl_utils.h>
#include <noise_gen.h>
#include <imaging.h>
#include <kernel.h>

const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_NONE | CLK_FILTER_NEAREST;

global vector3 const coord[$width * $height];

kernel void map2d_image(
global struct SMappingRanges *ranges,
const float z,
const int dim,
write_only image2d_t output
) {
    const size_t g0 = get_global_id(0);
    const size_t g1 = get_global_id(1);
    const size_t w = get_global_size(0);
    const size_t h = get_global_size(1);
/*    printf("group:%ld,%ld global:%ld,%ld size:%ld,%ld local:%ld,%ld size:%ld,%ld\\n",
get_group_id(0),get_group_id(1),
g0,g1,
w,h,
get_local_id(0),get_local_id(1),
get_local_size(0),get_local_size(1));*/
    if (get_local_id(0) == 0) {
        //printf("map2D -- vector3=%d \\n", sizeof(vector3)/sizeof(float));
        map2D(coord, calc_seamless_none, *ranges, w, h, z);
        //for (int i = 0; i < w*h; ++i) {
            //printf("coord = %f/%f/%f\\n"coord[i].x,coord[i].y,coord[i].z);
        //}
    }
    const int i = (g0 * w + g1);
    const float a = 0.5;
    const float r = value_noise3D(coord[i], 200, noInterp);
    const float g = r;
    const float b = r;
    write_imagef(output, (int2)(g0, g1), (float4)(r, g, b, a));
    printf("%d coord = %f/%f/%f %f/%f/%f/%f\\n",i,coord[i].x,coord[i].y,coord[i].z,r,g,b,a);
}
""")
            anlKernel.createKernel("map2d_image")
            def err = s.mallocInt(1)
            float z = 99
            int dim = vector3_size
            def ranges = MappingRanges.createWithBuffer(s).setDefault()
            def rangesb = new LwjglBuffer(ranges.getClBuffer(s, clcontext))
            int size = width * height
            def format = CLImageFormat.calloc(s)
            format.image_channel_order(CL_RGBA)
            format.image_channel_data_type(CL_UNORM_INT8)
            def image = BufferUtils.createShortBuffer(size * channel_size)
            def output = new LwjglImage(clCreateImage2D(clcontext, CL_MEM_WRITE_ONLY | CL_MEM_USE_HOST_PTR, format, width, height, 0, image, err));
            checkCLError(err.get(0))
            def global = new WorkSize(width, height)
            def local = new WorkSize(2, 2)
            def event = anlKernel.run2("map2d_image", queue, global, local, rangesb, z, dim, output)
            //def event = anlKernel.run1("map2d_image", queue, global, rangesb, z, dim, output)
            event.waitForFinished()

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
                    //println "($a,$r,$g,$b ${out.get(a)&0xFF}/${out.get(r)&0xFF}/${out.get(g)&0xFF}/${out.get(b)&0xFF})"
                }
            }
        }
    }

    @Test
    void "map2D kernel split coordinates"() {
        MemoryStack.stackPush().withCloseable { s ->
            def anlKernel = injector.getInstance(AnlKernelFactory).create(context)
            int width = 8
            int height = 8
            int localSize = 2
            anlKernel.buildLib()
            anlKernel.compileProgram("""
#include <opencl_utils.h>
#include <noise_gen.h>
#include <imaging.h>
#include <kernel.h>

kernel void map2d_image(
global struct SMappingRanges *g_ranges,
const float c_z,
const int c_dim,
write_only image2d_t output
) {
    const size_t g0 = get_global_id(0);
    const size_t g1 = get_global_id(1);
    const size_t w = get_global_size(0);
    const size_t h = get_global_size(1);
    const size_t l0 = get_local_id(0);
    const size_t l1 = get_local_id(1);
    const size_t lw = get_local_size(0);
    const size_t lh = get_local_size(1);
    local vector3 coord[$localSize * $localSize];
    local struct SMappingRanges ranges;
    if (l0 == 0 && l1 == 0) {
        const REAL sw = (g_ranges->mapx1 - g_ranges->mapx0) / w;
        const REAL sh = (g_ranges->mapy1 - g_ranges->mapy0) / h;
printf("%f %f [%f/%f - %f/%f]\\n",sw,sh,g_ranges->mapx0,g_ranges->mapx1,g_ranges->mapy0,g_ranges->mapy1);
        const REAL x0 = g_ranges->mapx0 + g0 * sw;
        const REAL x1 = g_ranges->mapx0 + g0 * sw + sw * lw;
        const REAL y0 = g_ranges->mapy0 + g1 * sh;
        const REAL y1 = g_ranges->mapy0 + g1 * sh + sh * lh;
        set_ranges_map2D(&ranges, x0, x1, y0, y1);
        printf("group:%ld=%ld global:%ld=%ld size:%ld=%ld local:%ld=%ld size:%ld=%ld [%f/%f - %f/%f]\\n",
get_group_id(0),get_group_id(1),
g0,g1,
w,h,
get_local_id(0),get_local_id(1),
get_local_size(0),get_local_size(1),
x0,
x1,
y0,
y1);
        map2D(coord, calc_seamless_none, ranges, lw, lh, c_z);
        for (int i = 0; i < lw*lh; ++i) {
            printf("%ld,%ld coord = %f/%f/%f\\n", g0,g1,coord[i].x,coord[i].y,coord[i].z);
        }
    }
    work_group_barrier(CLK_LOCAL_MEM_FENCE);
    const int i = (l0 + l1 * lh);
    printf("%ld,%ld %ld coord = %f/%f/%f\\n", g0,g1,i,coord[i].x,coord[i].y,coord[i].z);
    const float a = 0.5;
    const float r = value_noise3D(coord[i], 200, noInterp);
    const float g = r;
    const float b = r;
    write_imagef(output, (int2)(g0, g1), (float4)(r, g, b, a));
    //printf("%d coord = %f/%f/%f %f/%f/%f/%f\\n",i,coord[i].x,coord[i].y,coord[i].z,r,g,b,a);
}
""")
            anlKernel.createKernel("map2d_image")
            def err = s.mallocInt(1)
            float z = 99
            int dim = vector3_size
            def ranges = MappingRanges.createWithBuffer(s).setDefault()
            def rangesb = new LwjglBuffer(ranges.getClBuffer(s, clcontext))
            int size = width * height
            def format = CLImageFormat.calloc(s)
            format.image_channel_order(CL_RGBA)
            format.image_channel_data_type(CL_UNORM_INT8)
            def image = BufferUtils.createShortBuffer(size * channel_size)
            def output = new LwjglImage(clCreateImage2D(clcontext, CL_MEM_WRITE_ONLY | CL_MEM_USE_HOST_PTR, format, width, height, 0, image, err));
            checkCLError(err.get(0))
            def global = new WorkSize(width, height)
            def local = new WorkSize(2, 2)
            def event = anlKernel.run2("map2d_image", queue, global, local, rangesb, z, dim, output)
            //def event = anlKernel.run1("map2d_image", queue, global, rangesb, z, dim, output)
            event.waitForFinished()

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
                    print "($a,$r,$g,$b ${out.get(a)&0xFF}/${out.get(r)&0xFF}/${out.get(g)&0xFF}/${out.get(b)&0xFF})"
                }
                println()
            }
        }
    }

    @Test
    void "map2D kernel split coordinates groovy"() {
        MemoryStack.stackPush().withCloseable { s ->
            def anlKernel = injector.getInstance(AnlKernelFactory).create(context)
            int width = 8
            int height = 8
            int localSize = 2
            float z = 99
            int dim = vector3_size
            anlKernel.buildLib()
            def variables = [localSize: localSize, z: z]
            anlKernel.compileProgram('''
#include <opencl_utils.h>
#include <noise_gen.h>
#include <imaging.h>
#include <kernel.h>

kernel void map2d_image(
global struct SMappingRanges *g_ranges,
write_only image2d_t output
) {
    $insert_localMapRange
    const float a = 0.5;
    const float r = value_noise3D(coord[i], 200, noInterp);
    const float g = r;
    const float b = r;
    write_imagef(output, (int2)(g0, g1), (float4)(r, g, b, a));
}
''', variables)
            anlKernel.createKernel("map2d_image")
            def err = s.mallocInt(1)
            def ranges = MappingRanges.createWithBuffer(s).setDefault()
            def rangesb = new LwjglBuffer(ranges.getClBuffer(s, clcontext))
            int size = width * height
            def format = CLImageFormat.calloc(s)
            format.image_channel_order(CL_RGBA)
            format.image_channel_data_type(CL_UNORM_INT8)
            def image = BufferUtils.createShortBuffer(size * channel_size)
            def output = new LwjglImage(clCreateImage2D(clcontext, CL_MEM_WRITE_ONLY | CL_MEM_USE_HOST_PTR, format, width, height, 0, image, err));
            checkCLError(err.get(0))
            def global = new WorkSize(width, height)
            def local = new WorkSize(2, 2)
            def event = anlKernel.run2("map2d_image", queue, global, local, rangesb, output)
            //def event = anlKernel.run1("map2d_image", queue, global, rangesb, z, dim, output)
            event.waitForFinished()

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
                    print "($a,$r,$g,$b ${out.get(a)&0xFF}/${out.get(r)&0xFF}/${out.get(g)&0xFF}/${out.get(b)&0xFF})"
                }
                println()
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
