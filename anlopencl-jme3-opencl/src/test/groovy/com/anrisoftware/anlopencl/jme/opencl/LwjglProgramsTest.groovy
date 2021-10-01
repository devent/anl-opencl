package com.anrisoftware.anlopencl.jme.opencl

import static org.lwjgl.opencl.CL11.*;
import static org.lwjgl.system.MemoryStack.*;

import java.nio.IntBuffer

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack

import com.jme3.opencl.KernelCompilationException
import com.jme3.opencl.lwjgl.LwjglContext
import com.jme3.opencl.lwjgl.LwjglDevice
import com.jme3.opencl.lwjgl.LwjglPlatform

import groovy.util.logging.Slf4j

@Slf4j
class LwjglProgramsTest {

    long clplatform

    long cldevice

    long clcontext

    LwjglDevice device

    LwjglContext context

    @Test
    void "create header programs"() {
        def sources = new SourceResourcesProvider()
        def programsBuilder = new HeaderProgramsBuilder(sources.get())
        def programs = programsBuilder.createPrograms(context)
        assert programs.size() == 8
        programs.each { k, v -> log.debug("Program {} created: {}", k, v) }
    }

    @Test
    void "compile program with headers"() {
        def stack = stackPush()
        def sources = new SourceResourcesProvider()
        def libSources = new LibSourcesProvider(sources.get());
        def extraSources = new KernelExtraSourcesProvider(sources.get())
        def lprogram = null
        stack.withCloseable {
            def err = stack.mallocInt(1)
            def clprogram = clCreateProgramWithSource(clcontext, libSources.get(), err)
            log.debug("Program created: {}", clprogram)
            checkCLError(err.get(0))
            def program = new LwjglProgramEx(clprogram, context)
            program.compile("-DANLOPENCL_USE_OPENCL", null, null, null)
            lprogram = program.link("-create-library")
            log.debug("Library linked: {}", lprogram)
        }
        stack = stackPush()
        stack.withCloseable {
            def err = stack.mallocInt(1)
            def clprogram = clCreateProgramWithSource(clcontext, """
#include <opencl_utils.h>
#include <noise_gen.h>
#include <kernel.h>

kernel void value_noise2D_noInterp(
global vector2 *input,
global REAL *output
) {
    int id0 = get_global_id(0);
    output[id0] = value_noise2D(input[id0], 200, noInterp);
}
""", err)
            log.debug("Program created: {}", clprogram)
            checkCLError(err.get(0))
            def headersBuilder = new HeaderProgramsBuilder(sources.get())
            headersBuilder.createPrograms(context)
            def program = new LwjglProgramEx(clprogram, context)
            try {
                program.compile("-DANLOPENCL_USE_OPENCL", headersBuilder.headers, headersBuilder.headerNames, null)
            } catch (KernelCompilationException e) {
                log.error("Error compile {}", e.log)
                throw e
            }
        }
    }

    @BeforeEach
    void setupContext() {
        ToStringBuilder.defaultStyle = ToStringStyle.SHORT_PREFIX_STYLE
        clplatform = createPlatform()
        cldevice = createDevice(clplatform)
        clcontext = createContext(cldevice)
        device = new LwjglDevice(cldevice, new LwjglPlatform(clplatform))
        context = new LwjglContext(clcontext, [device])
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
