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
import com.jme3.opencl.Program
import com.jme3.opencl.ProgramCache
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
    void "program get device"() {
    }

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
        def sources = new SourceResourcesProvider()
        def libSources = new LibSourcesProvider(sources.get());
        def extraSources = new KernelExtraSourcesProvider(sources.get())
        def lprogramLib = null
        def err = MemoryStack.stackMallocInt(1)

        def clprogramLib = clCreateProgramWithSource(clcontext, libSources.get(), err)
        log.debug("Program created: {}", clprogramLib)
        checkCLError(err.get(0))

        def programLib = new LwjglProgramEx(clprogramLib, context)
        programLib.compile("-DANLOPENCL_USE_OPENCL", null, null, null)
        lprogramLib = LwjglProgramEx.link(context, "-create-library", programLib)
        log.debug("Library linked: {}", lprogramLib)

        def programCache = new ProgramCache(context);
        def cacheID = getClass().getName() + ".compile-program-with-headers";
        programCache.saveToCache(cacheID, programLib);
        def lprogramFromCache = programCache.loadFromCache(cacheID);
        log.debug("Library program from cache: {}", lprogramFromCache)

        def kiss09cl = sources.get().get("kiss09.cl")
        def randomcl = sources.get().get("random.cl")
        def clprogramKernel = clCreateProgramWithSource(clcontext, """
${kiss09cl}
${randomcl}
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
        log.debug("Kernel program created: {}", clprogramKernel)
        checkCLError(err.get(0))
        def headersBuilder = new HeaderProgramsBuilder(sources.get())
        headersBuilder.createPrograms(context)
        def programKernel = new LwjglProgramEx(clprogramKernel, context)
        try {
            programKernel.compile("-DANLOPENCL_USE_OPENCL", headersBuilder.headers, headersBuilder.headerNames)
            log.debug("Kernel program compiled: {}", programKernel)
        } catch (KernelCompilationException e) {
            log.error("Error compile {}", e.log)
            throw e
        }
        try {
            def lprogramKernel = LwjglProgramEx.link(context, "", [lprogramLib, programKernel] as Program[])
            log.debug("Kernel program linked: {}", lprogramKernel)
        } catch (KernelCompilationException e) {
            log.error("Error link {}", e.log)
            throw e
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

    static void checkCLError(IntBuffer errcode) {
        checkCLError(errcode.get(errcode.position()));
    }

    static void checkCLError(int errcode) {
        if (errcode != CL_SUCCESS) {
            throw new RuntimeException(String.format("OpenCL error [%d]", errcode));
        }
    }
}
