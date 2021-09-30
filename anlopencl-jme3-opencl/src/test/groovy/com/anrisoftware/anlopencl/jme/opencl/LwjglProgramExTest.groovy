package com.anrisoftware.anlopencl.jme.opencl

import static org.lwjgl.opencl.CL11.*;
import static org.lwjgl.system.MemoryStack.*;

import java.nio.IntBuffer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack

class LwjglProgramExTest {

    long platform

    long device

    long context

    @Test
    void "compile program with headers"() {
        MemoryStack stack = stackPush()
        stack.withCloseable {
            def err = stack.mallocInt(1)
            def program = clCreateProgramWithSource(context, "", err)
            checkCLError(err.get(0))
        }
    }

    @BeforeEach
    void setupContext() {
        platform = createPlatform()
        device = createDevice(platform)
        context = createContext(device)
    }

    static long createPlatform() {
        MemoryStack stack = stackPush()
        stack.withCloseable {
            def platforms = stack.mallocPointer(1)
            checkCLError(clGetPlatformIDs(platforms, null));
            if (platforms.get(0) == 0) {
                throw new RuntimeException("No OpenCL platforms found.");
            }
            def platform = platforms.get(0)
            return platform;
        }
    }

    static long createDevice(long platform) {
        MemoryStack stack = stackPush()
        stack.withCloseable {
            def devices = stack.mallocPointer(1)
            checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, devices, null));
            if (devices.get(0) == 0) {
                throw new RuntimeException("No GPU device found.");
            }
            def device = devices.get(0)
            return device;
        }
    }

    static long createContext(long device) {
        MemoryStack stack = stackPush()
        stack.withCloseable {
            def err = stack.mallocInt(1)
            def context = clCreateContext((PointerBuffer)null, device, null, 0, err)
            checkCLError(err.get(0))
            if (context == 0) {
                throw new RuntimeException("No GPU device found.");
            }
            return context;
        }
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
