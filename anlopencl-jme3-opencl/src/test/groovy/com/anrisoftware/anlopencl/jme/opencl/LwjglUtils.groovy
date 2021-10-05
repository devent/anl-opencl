package com.anrisoftware.anlopencl.jme.opencl

import static org.lwjgl.opencl.CL11.*;
import static org.lwjgl.system.MemoryStack.*;

import java.nio.IntBuffer

import org.lwjgl.PointerBuffer

/**
 * Test utils.
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class LwjglUtils {

    static long createPlatform() {
        def platforms = stackMallocPointer(1)
        checkCLError(clGetPlatformIDs(platforms, null));
        if (platforms.get(0) == 0) {
            throw new RuntimeException("No OpenCL platforms found.");
        }
        def platform = platforms.get(0)
        return platform;
    }

    static long createDevice(long platform) {
        def devices = stackMallocPointer(1)
        checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, devices, null));
        if (devices.get(0) == 0) {
            throw new RuntimeException("No GPU device found.");
        }
        def device = devices.get(0)
        return device;
    }

    static long createContext(long device) {
        def err = stackMallocInt(1)
        def context = clCreateContext((PointerBuffer)null, device, null, 0, err)
        checkCLError(err.get(0))
        if (context == 0) {
            throw new RuntimeException("No GPU device found.");
        }
        return context;
    }

    static long createQueue(long context, long device) {
        def err = stackMallocInt(1)
        def queue = clCreateCommandQueue(context, device, 0, err)
        checkCLError(err.get(0))
        if (queue == 0) {
            throw new RuntimeException("No queue created.");
        }
        return queue;
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
