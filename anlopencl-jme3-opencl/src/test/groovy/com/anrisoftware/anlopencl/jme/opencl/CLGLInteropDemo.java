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

import static com.anrisoftware.anlopencl.jme.opencl.InfoUtil.checkCLError;
import static com.anrisoftware.anlopencl.jme.opencl.InfoUtil.getDeviceInfoLong;
import static com.anrisoftware.anlopencl.jme.opencl.InfoUtil.getPlatformInfoStringASCII;
import static com.anrisoftware.anlopencl.jme.opencl.InfoUtil.getPlatformInfoStringUTF8;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opencl.CL10.CL_DEVICE_NOT_FOUND;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_ALL;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_CPU;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_GPU;
import static org.lwjgl.opencl.CL10.CL_PLATFORM_VENDOR;
import static org.lwjgl.opencl.CL10.clGetDeviceIDs;
import static org.lwjgl.opencl.CL10.clGetPlatformIDs;
import static org.lwjgl.opencl.KHRICD.CL_PLATFORM_ICD_SUFFIX_KHR;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

/*
        THIS DEMO USES CODE PORTED FROM JogAmp.org
		Original code: http://github.com/mbien/jocl-demos
		Original author: Michael Bien
   ___         ___                      ___
	  /  /\       /  /\         ___        /  /\    http://jocl.jogamp.org/
	 /  /:/      /  /::\       /__/\      /  /::\   a http://jogamp.org/ project.
	/__/::\     /  /:/\:\      \  \:\    /  /:/\:\
	\__\/\:\   /  /:/~/::\      \  \:\  /  /:/~/::\
	   \  \:\ /__/:/ /:/\:\ ___  \__\:\/__/:/ /:/\:\
		\__\:\\  \:\/:/__\//__/\ |  |:|\  \:\/:/__\/
		/  /:/ \  \::/     \  \:\|  |:| \  \::/
	   /__/:/   \  \:\      \  \:\__|:|  \  \:\
	   \__\/     \  \:\      \__\::::/    \  \:\
				  \__\/          ~~~~      \__\/
			   ___          ___       ___          ___          ___
			  /  /\        /  /\     /  /\        /__/\        /  /\
			 /  /::\      /  /::\   /  /:/_       \  \:\      /  /:/
			/  /:/\:\    /  /:/\:\ /  /:/ /\       \  \:\    /  /:/      ___     ___
		   /  /:/  \:\  /  /:/~/://  /:/ /:/_  _____\__\:\  /  /:/  ___ /__/\   /  /\
		  /__/:/ \__\:\/__/:/ /://__/:/ /:/ /\/__/::::::::\/__/:/  /  /\\  \:\ /  /:/
		  \  \:\ /  /:/\  \:\/:/ \  \:\/:/ /:/\  \:\~~\~~\/\  \:\ /  /:/ \  \:\  /:/
		   \  \:\  /:/  \  \::/   \  \::/ /:/  \  \:\  ~~~  \  \:\  /:/   \  \:\/:/
			\  \:\/:/    \  \:\    \  \:\/:/    \  \:\       \  \:\/:/     \  \::/
			 \  \::/      \  \:\    \  \::/      \  \:\       \  \::/       \__\/
			  \__\/        \__\/     \__\/        \__\/        \__\/
		 _____          ___           ___           ___           ___
		/  /::\        /  /\         /__/\         /  /\         /  /\
	   /  /:/\:\      /  /:/_       |  |::\       /  /::\       /  /:/_
	  /  /:/  \:\    /  /:/ /\      |  |:|:\     /  /:/\:\     /  /:/ /\
	 /__/:/ \__\:|  /  /:/ /:/_   __|__|:|\:\   /  /:/  \:\   /  /:/ /::\
	 \  \:\ /  /:/ /__/:/ /:/ /\ /__/::::| \:\ /__/:/ \__\:\ /__/:/ /:/\:\
	  \  \:\  /:/  \  \:\/:/ /:/ \  \:\~~\__\/ \  \:\ /  /:/ \  \:\/:/~/:/
	   \  \:\/:/    \  \::/ /:/   \  \:\        \  \:\  /:/   \  \::/ /:/
		\  \::/      \  \:\/:/     \  \:\        \  \:\/:/     \__\/ /:/
		 \__\/        \  \::/       \  \:\        \  \::/        /__/:/
					   \__\/         \__\/         \__\/         \__\/
*/

public final class CLGLInteropDemo {

    private static final Set<String> params = new HashSet<>(8);

    // max per pixel iterations to compute the fractal
    private static int maxIterations = 500;

    private static int initWidth  = 512;
    private static int initHeight = 512;

    // ------------------

    private CLGLInteropDemo() {
    }

    public static void main(String... args) {
        parseArgs(args);

        glfwSetErrorCallback(GLFWErrorCallback.createPrint());
        if (!glfwInit()) {
            System.out.println("Unable to initialize glfw");
            System.exit(-1);
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        } else {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        }
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        boolean debugGL = params.contains("debugGL");
        if (debugGL) {
            glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        }

        List<Long> platforms;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pi = stack.mallocInt(1);
            checkCLError(clGetPlatformIDs(null, pi));
            if (pi.get(0) == 0) {
                throw new IllegalStateException("No OpenCL platforms found.");
            }

            PointerBuffer platformIDs = stack.mallocPointer(pi.get(0));
            checkCLError(clGetPlatformIDs(platformIDs, (IntBuffer)null));

            platforms = new ArrayList<>(platformIDs.capacity());

            for (int i = 0; i < platformIDs.capacity(); i++) {
                long           platform = platformIDs.get(i);
                CLCapabilities caps     = CL.createPlatformCapabilities(platform);
                if (caps.cl_khr_gl_sharing || caps.cl_APPLE_gl_sharing) {
                    platforms.add(platform);
                }
            }
        }

        if (platforms.isEmpty()) {
            throw new IllegalStateException("No OpenCL platform found that supports OpenGL context sharing.");
        }

        platforms.sort((p1, p2) -> {
            // Prefer platforms that support GPU devices
            boolean gpu1 = !getDevices(p1, CL_DEVICE_TYPE_GPU).isEmpty();
            boolean gpu2 = !getDevices(p2, CL_DEVICE_TYPE_GPU).isEmpty();
            int     cmp  = gpu1 == gpu2 ? 0 : (gpu1 ? -1 : 1);
            if (cmp != 0) {
                return cmp;
            }

            return getPlatformInfoStringUTF8(p1, CL_PLATFORM_VENDOR).compareTo(getPlatformInfoStringUTF8(p2, CL_PLATFORM_VENDOR));
        });

        long           platform     = platforms.get(0);
        CLCapabilities platformCaps = CL.createPlatformCapabilities(platform);

        String platformID;
        if (platformCaps.cl_khr_icd) {
            platformID = getPlatformInfoStringASCII(platform, CL_PLATFORM_ICD_SUFFIX_KHR); // less spammy
        } else {
            platformID = getPlatformInfoStringUTF8(platform, CL_PLATFORM_VENDOR);
        }

        boolean hasCPU = false;
        boolean hasGPU = false;
        for (Long device : getDevices(platform, CL_DEVICE_TYPE_ALL)) {
            long type = getDeviceInfoLong(device, CL_DEVICE_TYPE);
            if (type == CL_DEVICE_TYPE_CPU) {
                hasCPU = true;
            } else if (type == CL_DEVICE_TYPE_GPU) {
                hasGPU = true;
            }
        }

        Thread[]     threads = new Thread[hasCPU && hasGPU ? 2 : 1];
        GLFWWindow[] windows = new GLFWWindow[threads.length];

        CountDownLatch latch   = new CountDownLatch(windows.length);
        CyclicBarrier  barrier = new CyclicBarrier(windows.length + 1);

        for (int i = 0; i < threads.length; i++) {
            int deviceType = i == 1 || !hasGPU ? CL_DEVICE_TYPE_CPU : CL_DEVICE_TYPE_GPU;

            String     ID     = platformID + " - " + (deviceType == CL_DEVICE_TYPE_CPU ? "CPU" : "GPU");
            GLFWWindow window = new GLFWWindow(glfwCreateWindow(initWidth, initHeight, ID, NULL, NULL), ID, new CountDownLatch(1));
            glfwSetWindowPos(window.handle, 200 + initWidth * i + 32 * i, 200);

            windows[i] = window;
            threads[i] = new Thread(platformID) {
                @Override
                public void run() {
                    Mandelbrot demo = null;
                    try {
                        demo = new Mandelbrot(platform, platformCaps, window, deviceType, debugGL, maxIterations);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        barrier.await();
                        if (demo != null) {
                            demo.renderLoop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            };
            threads[i].start();
        }

        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < windows.length; i++) {
            glfwShowWindow(windows[i].handle);
        }

        System.out.println("GAME ON!");

        while (latch.getCount() != 0) {
            glfwPollEvents();

            for (int i = 0; i < windows.length; i++) {
                if (windows[i] != null && windows[i].signal.getCount() == 0) {
                    windows[i].destroy();
                    windows[i] = null;
                }
            }
        }

        CL.destroy();
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

        System.out.println("GAME OVER!");
    }

    private static void parseArgs(String... args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.charAt(0) != '-' && arg.charAt(0) != '/') {
                throw new IllegalArgumentException("Invalid command-line argument: " + args[i]);
            }

            String param = arg.substring(1);

            if ("forceCPU".equalsIgnoreCase(param)) {
                params.add("forceCPU");
            } else if ("debugGL".equalsIgnoreCase(param)) {
                params.add("debugGL");
            } else if ("iterations".equalsIgnoreCase(param)) {
                if (args.length < i + 1 + 1) {
                    throw new IllegalArgumentException("Invalid iterations argument specified.");
                }

                try {
                    maxIterations = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number of iterations specified.");
                }
            } else if ("res".equalsIgnoreCase(param)) {
                if (args.length < i + 2 + 1) {
                    throw new IllegalArgumentException("Invalid res argument specified.");
                }

                try {
                    initWidth = Integer.parseInt(args[++i]);
                    initHeight = Integer.parseInt(args[++i]);

                    if (initWidth < 1 || initHeight < 1) {
                        throw new IllegalArgumentException("Invalid res dimensions specified.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid res dimensions specified.");
                }
            }
        }
    }

    private static List<Long> getDevices(long platform, int deviceType) {
        List<Long> devices;
        try (MemoryStack stack = stackPush()) {
            IntBuffer pi      = stack.mallocInt(1);
            int       errcode = clGetDeviceIDs(platform, deviceType, null, pi);
            if (errcode == CL_DEVICE_NOT_FOUND) {
                devices = Collections.emptyList();
            } else {
                checkCLError(errcode);

                PointerBuffer deviceIDs = stack.mallocPointer(pi.get(0));
                checkCLError(clGetDeviceIDs(platform, deviceType, deviceIDs, (IntBuffer)null));

                devices = new ArrayList<>(deviceIDs.capacity());

                for (int i = 0; i < deviceIDs.capacity(); i++) {
                    devices.add(deviceIDs.get(i));
                }
            }
        }

        return devices;
    }

    static class GLFWWindow {

        final long handle;

        final String ID;

        /** Used to signal that the rendering thread has completed. */
        final CountDownLatch signal;

        GLFWWindowSizeCallback      windowsizefun;
        GLFWFramebufferSizeCallback framebuffersizefun;
        GLFWKeyCallback             keyfun;
        GLFWMouseButtonCallback     mousebuttonfun;
        GLFWCursorPosCallback       cursorposfun;
        GLFWScrollCallback          scrollfun;

        private GLFWWindow(long handle, String ID, CountDownLatch signal) {
            this.handle = handle;
            this.ID = ID;
            this.signal = signal;
        }

        void destroy() {
            glfwFreeCallbacks(handle);
            glfwDestroyWindow(handle);
        }

    }

}