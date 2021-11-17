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
 */
package com.anrisoftware.anlopencl.jme.opencl;

import static com.anrisoftware.anlopencl.jme.opencl.IOUtil.ioResourceToByteBuffer;
import static com.anrisoftware.anlopencl.jme.opencl.InfoUtil.checkCLError;
import static com.anrisoftware.anlopencl.jme.opencl.InfoUtil.getPlatformInfoStringUTF8;
import static com.anrisoftware.anlopencl.jme.opencl.InfoUtil.getProgramBuildInfoInt;
import static com.anrisoftware.anlopencl.jme.opencl.InfoUtil.getProgramBuildInfoStringASCII;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.nglfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.nglfwGetWindowSize;
import static org.lwjgl.glfw.GLFWNativeGLX.glfwGetGLXContext;
import static org.lwjgl.glfw.GLFWNativeWGL.glfwGetWGLContext;
import static org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Display;
import static org.lwjgl.opencl.CL10.CL_CONTEXT_PLATFORM;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_CPU;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_GPU;
import static org.lwjgl.opencl.CL10.CL_MEM_COPY_HOST_PTR;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_ONLY;
import static org.lwjgl.opencl.CL10.CL_MEM_WRITE_ONLY;
import static org.lwjgl.opencl.CL10.CL_PLATFORM_VENDOR;
import static org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_LOG;
import static org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_STATUS;
import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.clBuildProgram;
import static org.lwjgl.opencl.CL10.clCreateBuffer;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clCreateContext;
import static org.lwjgl.opencl.CL10.clCreateKernel;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.opencl.CL10.clEnqueueNDRangeKernel;
import static org.lwjgl.opencl.CL10.clFinish;
import static org.lwjgl.opencl.CL10.clGetDeviceIDs;
import static org.lwjgl.opencl.CL10.clReleaseEvent;
import static org.lwjgl.opencl.CL10.clReleaseMemObject;
import static org.lwjgl.opencl.CL10.clReleaseProgram;
import static org.lwjgl.opencl.CL10.clSetKernelArg1d;
import static org.lwjgl.opencl.CL10.clSetKernelArg1f;
import static org.lwjgl.opencl.CL10.clSetKernelArg1i;
import static org.lwjgl.opencl.CL10.clSetKernelArg1p;
import static org.lwjgl.opencl.CL10GL.clCreateFromGLTexture2D;
import static org.lwjgl.opencl.CL10GL.clEnqueueAcquireGLObjects;
import static org.lwjgl.opencl.CL10GL.clEnqueueReleaseGLObjects;
import static org.lwjgl.opencl.KHRGLSharing.CL_GLX_DISPLAY_KHR;
import static org.lwjgl.opencl.KHRGLSharing.CL_GL_CONTEXT_KHR;
import static org.lwjgl.opencl.KHRGLSharing.CL_WGL_HDC_KHR;
import static org.lwjgl.opengl.ARBCLEvent.glCreateSyncFromCLeventARB;
import static org.lwjgl.opengl.CGL.CGLGetCurrentContext;
import static org.lwjgl.opengl.CGL.CGLGetShareGroup;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDeleteTextures;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL11C.glFinish;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glCompileShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;
import static org.lwjgl.opengl.GL20C.glCreateShader;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glGetAttribLocation;
import static org.lwjgl.opengl.GL20C.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20C.glGetProgrami;
import static org.lwjgl.opengl.GL20C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20C.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glGetUniformLocation;
import static org.lwjgl.opengl.GL20C.glLinkProgram;
import static org.lwjgl.opengl.GL20C.glShaderSource;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.opengl.GL20C.glUniform2f;
import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.GL_RGBA8UI;
import static org.lwjgl.opengl.GL30C.GL_RGBA_INTEGER;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.WGL.wglGetCurrentDC;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APPLEGLSharing;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.opencl.CLProgramCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import com.anrisoftware.anlopencl.jme.opencl.CLGLInteropDemo.GLFWWindow;

public class Mandelbrot {

    // The fractal bounds in world space
    private static final double
        WIDTH  = 4.0,
        HEIGHT = 3.0;

    private static final int COLOR_MAP_SIZE = 32 * 2 * 4;

    private static final ByteBuffer source;

    static {
        try {
            source = ioResourceToByteBuffer("Mandelbrot.cl", 4096);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** The event callbacks run on the main thread. We use this queue to apply any changes in the rendering thread. */
    private final Queue<Runnable> events = new ConcurrentLinkedQueue<>();

    private final GLFWWindow window;

    private final int maxIterations;

    private boolean shouldInitBuffers = true;
    private boolean rebuild;

    // OPENCL

    private final IntBuffer errcode_ret;

    private final long platform;
    private final long device;

    private final CLCapabilities deviceCaps;

    private final CLContextCallback clContextCB;

    private final long clContext;
    private final long clColorMap;
    private final long clQueue;
    private       long clProgram;
    private       long clKernel;
    private       long clTexture;

    private final PointerBuffer kernel2DGlobalWorkSize = BufferUtils.createPointerBuffer(2);

    private boolean doublePrecision = true;

    // OPENGL

    private int glTexture;

    private int vao;
    private int vbo;
    private int vsh;
    private int fsh;
    private int glProgram;

    private int projectionUniform;
    private int sizeUniform;

    // VIEWPORT

    private int
        ww,
        wh;

    private int
        fbw,
        fbh;

        private double
        offsetX = -0.5;

    private double offsetY;

    private double zoom = 1.0;

    // EVENT SYNCING

    private final PointerBuffer syncBuffer = BufferUtils.createPointerBuffer(1);

    private boolean syncGLtoCL; // false if we can make GL wait on events generated from CL queues.
    private long    clEvent;
    private long    glFenceFromCLEvent;

    private boolean syncCLtoGL; // false if we can make CL wait on sync objects generated from GL.

    // INPUT

    private double mouseX;
    private double mouseY;

    private boolean ctrlDown;

    private boolean dragging;

    private double dragX;
    private double dragY;

    private double dragOffsetX;
    private double dragOffsetY;

    // CALLBACKS

    Callback debugProc;

    public Mandelbrot(long platform, CLCapabilities platformCaps, GLFWWindow window, int deviceType, boolean debugGL, int maxIterations) {
        this.platform = platform;

        this.window = window;
        this.maxIterations = maxIterations;

        IntBuffer size = BufferUtils.createIntBuffer(2);

        nglfwGetWindowSize(window.handle, memAddress(size), memAddress(size) + 4);
        ww = size.get(0);
        wh = size.get(1);

        nglfwGetFramebufferSize(window.handle, memAddress(size), memAddress(size) + 4);
        fbw = size.get(0);
        fbh = size.get(1);

        glfwMakeContextCurrent(window.handle);
        GLCapabilities glCaps = GL.createCapabilities();
        if (!glCaps.OpenGL30) {
            throw new RuntimeException("OpenGL 3.0 is required to run this demo.");
        }

        debugProc = debugGL ? GLUtil.setupDebugMessageCallback() : null;

        glfwSwapInterval(0);

        errcode_ret = BufferUtils.createIntBuffer(1);

        try {
            // Find devices with GL sharing support
            {
                long device = getDevice(platform, platformCaps, deviceType);
                if (device == NULL) {
                    device = getDevice(platform, platformCaps, CL_DEVICE_TYPE_CPU);
                }

                if (device == NULL) {
                    throw new RuntimeException("No OpenCL devices found with OpenGL sharing support.");
                }

                this.device = device;
                this.deviceCaps = CL.createDeviceCapabilities(device, platformCaps);
            }

            // Create the context
            PointerBuffer ctxProps = BufferUtils.createPointerBuffer(7);
            switch (Platform.get()) {
                case WINDOWS:
                    ctxProps
                        .put(CL_GL_CONTEXT_KHR)
                        .put(glfwGetWGLContext(window.handle))
                        .put(CL_WGL_HDC_KHR)
                        .put(wglGetCurrentDC());
                    break;
                case LINUX:
                    ctxProps
                        .put(CL_GL_CONTEXT_KHR)
                        .put(glfwGetGLXContext(window.handle))
                        .put(CL_GLX_DISPLAY_KHR)
                        .put(glfwGetX11Display());
                    break;
                case MACOSX:
                    ctxProps
                        .put(APPLEGLSharing.CL_CONTEXT_PROPERTY_USE_CGL_SHAREGROUP_APPLE)
                        .put(CGLGetShareGroup(CGLGetCurrentContext()));
            }
            ctxProps
                .put(CL_CONTEXT_PLATFORM)
                .put(platform)
                .put(NULL)
                .flip();
            clContext = clCreateContext(ctxProps, device, clContextCB = CLContextCallback.create(
                (errinfo, private_info, cb, user_data) -> log(String.format("cl_context_callback\n\tInfo: %s", memUTF8(errinfo)))
            ), NULL, errcode_ret);
            checkCLError(errcode_ret);

            // create command queues for every GPU, setup colormap and init kernels

            IntBuffer colorMapBuffer = BufferUtils.createIntBuffer(32 * 2);
            initColorMap(colorMapBuffer, 32, Color.BLUE, Color.GREEN, Color.RED);

            clColorMap = clCreateBuffer(clContext, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, colorMapBuffer, errcode_ret);
            checkCLError(errcode_ret);

            // create command queue and upload color map buffer
            clQueue = clCreateCommandQueue(clContext, device, NULL, errcode_ret);
            checkCLError(errcode_ret);

            // load program(s)
            if (deviceType == CL_DEVICE_TYPE_GPU) {
                log("OpenCL Device Type: GPU (Use -forceCPU to use CPU)");
            } else {
                log("OpenCL Device Type: CPU");
            }

            log("Max Iterations: " + maxIterations + " (Use -iterations <count> to change)");
            log("Display resolution: " + ww + "x" + wh + " (Use -res <width> <height> to change)");

            log("OpenGL glCaps.GL_ARB_sync = " + glCaps.GL_ARB_sync);
            log("OpenGL glCaps.GL_ARB_cl_event = " + glCaps.GL_ARB_cl_event);

            buildProgram();

            // Detect GLtoCL synchronization method
            syncGLtoCL = !glCaps.GL_ARB_cl_event; // GL3.2 or ARB_sync implied
            log(syncGLtoCL
                ? "GL to CL sync: Using clFinish"
                : "GL to CL sync: Using OpenCL events"
            );

            // Detect CLtoGL synchronization method
            syncCLtoGL = !deviceCaps.cl_khr_gl_event;
            log(syncCLtoGL
                ? "CL to GL sync: Using glFinish"
                : "CL to GL sync: Using implicit sync (cl_khr_gl_event)"
            );

            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);

            try (MemoryStack stack = stackPush()) {
                glBufferData(GL_ARRAY_BUFFER, stack.floats(
                    0.0f, 0.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 1.0f, 1.0f
                ), GL_STATIC_DRAW);
            }

            vsh = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vsh,
                "#version 150\n" +
                "\n" +
                "uniform mat4 projection;\n" +
                "\n" +
                "uniform vec2 size;\n" +
                "\n" +
                "in vec2 posIN;\n" +
                "in vec2 texIN;\n" +
                "\n" +
                "out vec2 texCoord;\n" +
                "\n" +
                "void main(void) {\n" +
                "\tgl_Position = projection * vec4(posIN * size, 0.0, 1.0);\n" +
                "\ttexCoord = texIN;\n" +
                "}");
            glCompileShader(vsh);
            String log = glGetShaderInfoLog(vsh, glGetShaderi(vsh, GL_INFO_LOG_LENGTH));
            if (!log.isEmpty()) {
                log(String.format("VERTEX SHADER LOG: %s", log));
            }

            fsh = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fsh,
                "#version 150\n" +
                "\n" +
                "uniform isampler2D mandelbrot;\n" +
                "\n" +
                "in vec2 texCoord;\n" +
                "\n" +
                "out vec4 fragColor;\n" +
                "\n" +
                "void main(void) {\n" +
                "\tfragColor = texture(mandelbrot, texCoord) / 255.0;\n" +
                "}");
            glCompileShader(fsh);
            log = glGetShaderInfoLog(fsh, glGetShaderi(fsh, GL_INFO_LOG_LENGTH));
            if (!log.isEmpty()) {
                log(String.format("FRAGMENT SHADER LOG: %s", log));
            }

            glProgram = glCreateProgram();
            glAttachShader(glProgram, vsh);
            glAttachShader(glProgram, fsh);
            glLinkProgram(glProgram);
            log = glGetProgramInfoLog(glProgram, glGetProgrami(glProgram, GL_INFO_LOG_LENGTH));
            if (!log.isEmpty()) {
                log(String.format("PROGRAM LOG: %s", log));
            }

            int posIN = glGetAttribLocation(glProgram, "posIN");
            int texIN = glGetAttribLocation(glProgram, "texIN");

            glVertexAttribPointer(posIN, 2, GL_FLOAT, false, 4 * 4, 0);
            glVertexAttribPointer(texIN, 2, GL_FLOAT, false, 4 * 4, 2 * 4);

            glEnableVertexAttribArray(posIN);
            glEnableVertexAttribArray(texIN);

            projectionUniform = glGetUniformLocation(glProgram, "projection");
            sizeUniform = glGetUniformLocation(glProgram, "size");

            glUseProgram(glProgram);

            glUniform1i(glGetUniformLocation(glProgram, "mandelbrot"), 0);
        } catch (Exception e) {
            // TODO: cleanup
            throw new RuntimeException(e);
        }

        glDisable(GL_DEPTH_TEST);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        initGLObjects();
        glFinish();

        setKernelConstants();

        glfwSetWindowSizeCallback(window.handle, (windowHandle, width, height) -> {
            if (width == 0 || height == 0) {
                return;
            }

            events.add(() -> {
                this.ww = width;
                this.wh = height;

                shouldInitBuffers = true;
            });
        });

        glfwSetFramebufferSizeCallback(window.handle, (windowHandle, width, height) -> {
            if (width == 0 || height == 0) {
                return;
            }

            events.add(() -> {
                this.fbw = width;
                this.fbh = height;

                shouldInitBuffers = true;
            });
        });

        glfwSetKeyCallback(window.handle, (windowHandle, key, scancode, action, mods) -> {
            switch (key) {
                case GLFW_KEY_LEFT_CONTROL:
                case GLFW_KEY_RIGHT_CONTROL:
                    ctrlDown = action == GLFW_PRESS;
                    return;
            }

            if (action != GLFW_PRESS) {
                return;
            }

            switch (key) {
                case GLFW_KEY_ESCAPE:
                    glfwSetWindowShouldClose(windowHandle, true);
                    break;
                case GLFW_KEY_D:
                    events.offer(() -> {
                        doublePrecision = !doublePrecision;
                        log("DOUBLE PRECISION IS NOW: " + (doublePrecision ? "ON" : "OFF"));
                        rebuild = true;
                    });
                    break;
                case GLFW_KEY_HOME:
                    events.offer(() -> {
                        offsetX = -0.5;
                        offsetY = 0.0;
                        zoom = 1.0;
                    });
                    break;
            }
        });

        glfwSetMouseButtonCallback(window.handle, (windowHandle, button, action, mods) -> {
            if (button != GLFW_MOUSE_BUTTON_LEFT) {
                return;
            }

            dragging = action == GLFW_PRESS;

            if (dragging) {
                dragging = true;

                dragX = mouseX;
                dragY = mouseY;

                dragOffsetX = offsetX;
                dragOffsetY = offsetY;
            }
        });

        glfwSetCursorPosCallback(window.handle, (windowHandle, xpos, ypos) -> {
            mouseX = xpos;
            mouseY = wh - ypos;

            if (dragging) {
                offsetX = dragOffsetX + transformX(dragX - mouseX);
                offsetY = dragOffsetY + transformY(dragY - mouseY);
            }
        });

        glfwSetScrollCallback(window.handle, (windowHandle, xoffset, yoffset) -> {
            if (yoffset == 0) {
                return;
            }

            double scrollX = mouseX - ww * 0.5;
            double scrollY = mouseY - wh * 0.5;

            double zoomX = transformX(scrollX);
            double zoomY = transformY(scrollY);

            zoom *= (1.0 - yoffset * (ctrlDown ? 0.25 : 0.05));

            offsetX += zoomX - transformX(scrollX);
            offsetY += zoomY - transformY(scrollY);
        });
    }

    private static long getDevice(long platform, CLCapabilities platformCaps, int deviceType) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pi = stack.mallocInt(1);
            checkCLError(clGetDeviceIDs(platform, deviceType, null, pi));

            PointerBuffer devices = stack.mallocPointer(pi.get(0));
            checkCLError(clGetDeviceIDs(platform, deviceType, devices, (IntBuffer)null));

            for (int i = 0; i < devices.capacity(); i++) {
                long device = devices.get(i);

                CLCapabilities caps = CL.createDeviceCapabilities(device, platformCaps);
                if (!(caps.cl_khr_gl_sharing || caps.cl_APPLE_gl_sharing)) {
                    continue;
                }

                return device;
            }
        }

        return NULL;
    }

    private void log(String msg) {
        System.err.format("[%s] %s\n", window.ID, msg);
    }

    void renderLoop() {
        long startTime = System.currentTimeMillis() + 5000;
        long fps       = 0;

        while (!glfwWindowShouldClose(window.handle)) {
            Runnable event;
            while ((event = events.poll()) != null) {
                event.run();
            }

            try {
                display();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            glfwSwapBuffers(window.handle);

            if (startTime > System.currentTimeMillis()) {
                fps++;
            } else {
                long timeUsed = 5000 + (startTime - System.currentTimeMillis());
                startTime = System.currentTimeMillis() + 5000;
                log(String.format(
                    "%s: %d frames in 5 seconds = %.2f",
                    getPlatformInfoStringUTF8(platform, CL_PLATFORM_VENDOR),
                    fps,
                    fps / (timeUsed / 1000f)
                ));
                fps = 0;
            }
        }

        cleanup();

        window.signal.countDown();
    }

    private interface CLReleaseFunction {
        int invoke(long object);
    }

    private static void release(long object, CLReleaseFunction release) {
        if (object == NULL) {
            return;
        }

        int errcode = release.invoke(object);
        checkCLError(errcode);
    }

    private void cleanup() {
        release(clTexture, CL10::clReleaseMemObject);
        release(clColorMap, CL10::clReleaseMemObject);

        release(clKernel, CL10::clReleaseKernel);
        release(clProgram, CL10::clReleaseProgram);
        release(clQueue, CL10::clReleaseCommandQueue);
        release(clContext, CL10::clReleaseContext);

        clContextCB.free();

        glDeleteProgram(glProgram);
        glDeleteShader(fsh);
        glDeleteShader(vsh);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);

        if (debugProc != null) {
            debugProc.free();
        }

        GL.setCapabilities(null);
    }

    private void display() {
        // make sure GL does not use our objects before we start computing
        if (syncCLtoGL || shouldInitBuffers) {
            glFinish();
        }

        if (shouldInitBuffers) {
            initGLObjects();
            setKernelConstants();
        }

        if (rebuild) {
            buildProgram();
            setKernelConstants();
        }
        computeCL(doublePrecision);

        renderGL();
    }

    // OpenCL

    private double transformX(double x) {
        return x * zoom * (WIDTH / ww);
    }

    private double transformY(double y) {
        return y * zoom * (HEIGHT / wh);
    }

    private void computeCL(boolean is64bit) {
        double minX = transformX(-ww * 0.5) + offsetX;
        double maxX = transformX(ww * 0.5) + offsetX;
        double minY = transformY(-wh * 0.5) + offsetY;
        double maxY = transformY(wh * 0.5) + offsetY;

        double rangeX = maxX - minX;
        double rangeY = maxY - minY;

        kernel2DGlobalWorkSize.put(0, ww).put(1, wh);

        // start computation

        clSetKernelArg1i(clKernel, 0, ww);
        clSetKernelArg1i(clKernel, 1, wh);
        if (!is64bit || !isDoubleFPAvailable(deviceCaps)) {
            clSetKernelArg1f(clKernel, 2, (float)minX);
            clSetKernelArg1f(clKernel, 3, (float)minY);

            clSetKernelArg1f(clKernel, 4, (float)rangeX);
            clSetKernelArg1f(clKernel, 5, (float)rangeY);
        } else {
            clSetKernelArg1d(clKernel, 2, minX);
            clSetKernelArg1d(clKernel, 3, minY);

            clSetKernelArg1d(clKernel, 4, rangeX);
            clSetKernelArg1d(clKernel, 5, rangeY);
        }

        // acquire GL objects, and enqueue a kernel with a probe from the list
        int errcode = clEnqueueAcquireGLObjects(clQueue, clTexture, null, null);
        checkCLError(errcode);

        errcode = clEnqueueNDRangeKernel(clQueue, clKernel, 2,
            null,
            kernel2DGlobalWorkSize,
            null,
            null, null);
        checkCLError(errcode);

        errcode = clEnqueueReleaseGLObjects(clQueue, clTexture, null, !syncGLtoCL ? syncBuffer : null);
        checkCLError(errcode);

        if (!syncGLtoCL) {
            clEvent = syncBuffer.get(0);
            glFenceFromCLEvent = glCreateSyncFromCLeventARB(clContext, clEvent, 0);
        }

        // block until done (important: finish before doing further gl work)
        if (syncGLtoCL) {
            errcode = clFinish(clQueue);
            checkCLError(errcode);
        }
    }

    // OpenGL

    private void renderGL() {
        glClear(GL_COLOR_BUFFER_BIT);

        //draw slices

        if (!syncGLtoCL) {
            if (glFenceFromCLEvent != NULL) {
                GL32.glWaitSync(glFenceFromCLEvent, 0, 0);
                GL32.glDeleteSync(glFenceFromCLEvent);
                glFenceFromCLEvent = NULL;
            }

            int errcode = clReleaseEvent(clEvent);
            clEvent = NULL;
            checkCLError(errcode);
        }

        glBindTexture(GL_TEXTURE_2D, glTexture);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    private static boolean isDoubleFPAvailable(CLCapabilities caps) {
        return caps.cl_khr_fp64 || caps.cl_amd_fp64;
    }

    private void buildProgram() {
        if (clProgram != NULL) {
            int errcode = clReleaseProgram(clProgram);
            checkCLError(errcode);
        }

        PointerBuffer strings = BufferUtils.createPointerBuffer(1);
        PointerBuffer lengths = BufferUtils.createPointerBuffer(1);

        strings.put(0, source);
        lengths.put(0, source.remaining());

        clProgram = clCreateProgramWithSource(clContext, strings, lengths, errcode_ret);
        checkCLError(errcode_ret);

        CountDownLatch latch = new CountDownLatch(1);

        // disable 64bit floating point math if not available
        StringBuilder options = new StringBuilder("-D USE_TEXTURE");
        if (doublePrecision && isDoubleFPAvailable(deviceCaps)) {
            //cl_khr_fp64
            options.append(" -D DOUBLE_FP");

            // AMD's verson of double precision floating point math
            if (!deviceCaps.cl_khr_fp64 && deviceCaps.cl_amd_fp64) {
                options.append(" -D AMD_FP");
            }
        }

        log("OpenCL COMPILER OPTIONS: " + options);

        CLProgramCallback buildCallback;
        int errcode = clBuildProgram(clProgram, device, options, buildCallback = CLProgramCallback.create((program, user_data) -> {
            log(String.format(
                "The cl_program [0x%X] was built %s",
                program,
                getProgramBuildInfoInt(program, device, CL_PROGRAM_BUILD_STATUS) == CL_SUCCESS ? "successfully" : "unsuccessfully"
            ));
            String log = getProgramBuildInfoStringASCII(program, device, CL_PROGRAM_BUILD_LOG);
            if (!log.isEmpty()) {
                log(String.format("BUILD LOG:\n----\n%s\n-----", log));
            }

            latch.countDown();
        }), NULL);
        checkCLError(errcode);

        // Make sure the program has been built before proceeding
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        buildCallback.free();
        rebuild = false;

        // init kernel with constants
        clKernel = clCreateKernel(clProgram, "mandelbrot", errcode_ret);
        checkCLError(errcode_ret);
    }

    private void initGLObjects() {
        if (clTexture != NULL) {
            checkCLError(clReleaseMemObject(clTexture));
            glDeleteTextures(glTexture);
        }

        glTexture = glGenTextures();

        // Init textures
        glBindTexture(GL_TEXTURE_2D, glTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8UI, ww, wh, 0, GL_RGBA_INTEGER, GL_UNSIGNED_BYTE, (ByteBuffer)null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        clTexture = clCreateFromGLTexture2D(clContext, CL_MEM_WRITE_ONLY, GL_TEXTURE_2D, 0, glTexture, errcode_ret);
        checkCLError(errcode_ret);
        glBindTexture(GL_TEXTURE_2D, 0);

        glViewport(0, 0, fbw, fbh);

        glUniform2f(sizeUniform, ww, wh);

        FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(4 * 4);
        glOrtho(0.0f, ww, 0.0f, wh, 0.0f, 1.0f, projectionMatrix);
        glUniformMatrix4fv(projectionUniform, false, projectionMatrix);

        shouldInitBuffers = false;
    }

    private static void glOrtho(float l, float r, float b, float t, float n, float f, FloatBuffer m) {
        m.put(new float[] {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
        });
        m.flip();

        m.put(0 * 4 + 0, 2.0f / (r - l));
        m.put(1 * 4 + 1, 2.0f / (t - b));
        m.put(2 * 4 + 2, -2.0f / (f - n));

        m.put(3 * 4 + 0, -(r + l) / (r - l));
        m.put(3 * 4 + 1, -(t + b) / (t - b));
        m.put(3 * 4 + 2, -(f + n) / (f - n));
    }

    // init kernels with constants

    private void setKernelConstants() {
        clSetKernelArg1p(clKernel, 6, clTexture);
        clSetKernelArg1p(clKernel, 7, clColorMap);
        clSetKernelArg1i(clKernel, 8, COLOR_MAP_SIZE);
        clSetKernelArg1i(clKernel, 9, maxIterations);
    }

    private enum Color {

        RED(255, 0, 0),
        GREEN(0, 255, 0),
        BLUE(0, 0, 255);

        final int red;
        final int green;
        final int blue;

        Color(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        private int getRed() {
            return red;
        }

        private int getGreen() {
            return green;
        }

        private int getBlue() {
            return blue;
        }

    }

    private static void initColorMap(IntBuffer colorMap, int stepSize, Color... colors) {
        for (int n = 0; n < colors.length - 1; n++) {
            Color color = colors[n];

            int r0 = color.getRed();
            int g0 = color.getGreen();
            int b0 = color.getBlue();

            color = colors[n + 1];

            int r1 = color.getRed();
            int g1 = color.getGreen();
            int b1 = color.getBlue();

            int deltaR = r1 - r0;
            int deltaG = g1 - g0;
            int deltaB = b1 - b0;

            for (int step = 0; step < stepSize; step++) {
                float alpha = (float)step / (stepSize - 1);

                int r = (int)(r0 + alpha * deltaR);
                int g = (int)(g0 + alpha * deltaG);
                int b = (int)(b0 + alpha * deltaB);

                colorMap.put((r << 0) | (g << 8) | (b << 16));
            }
        }
        colorMap.flip();
    }

}