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

import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_GPU;
import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clCreateContext;
import static org.lwjgl.opencl.CL10.clGetDeviceIDs;
import static org.lwjgl.opencl.CL10.clGetPlatformIDs;

import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

/**
 * Test utils.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class LwjglUtils {

    public static long createPlatform() {
        try (var s = MemoryStack.stackPush()) {
            var platforms = s.mallocPointer(1);
            checkCLError(clGetPlatformIDs(platforms, (IntBuffer) null));
            if (platforms.get(0) == 0) {
                throw new RuntimeException("No OpenCL platforms found.");
            }
            var platform = platforms.get(0);
            return platform;
        }
    }

    public static long createDevice(long platform) {
        try (var s = MemoryStack.stackPush()) {
            var devices = s.mallocPointer(1);
            checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, devices, (IntBuffer) null));
            if (devices.get(0) == 0) {
                throw new RuntimeException("No GPU device found.");
            }
            var device = devices.get(0);
            return device;
        }
    }

    public static long createContext(long device) {
        try (var s = MemoryStack.stackPush()) {
            var err = s.mallocInt(1);
            var context = clCreateContext((PointerBuffer) null, device, null, 0, err);
            checkCLError(err.get(0));
            if (context == 0) {
                throw new RuntimeException("No GPU device found.");
            }
            return context;
        }
    }

    public static long createQueue(long context, long device) {
        try (var s = MemoryStack.stackPush()) {
            var err = s.mallocInt(1);
            var queue = clCreateCommandQueue(context, device, 0, err);
            checkCLError(err.get(0));
            if (queue == 0) {
                throw new RuntimeException("No queue created.");
            }
            return queue;
        }
    }

    public static void checkCLError(IntBuffer errcode) {
        checkCLError(errcode.get(errcode.position()));
    }

    public static void checkCLError(int errcode) {
        if (errcode != CL_SUCCESS) {
            throw new RuntimeException(String.format("OpenCL error [%d]", errcode));
        }
    }
}