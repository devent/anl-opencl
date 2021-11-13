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
import static org.lwjgl.opencl.CL11.*;

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        programLib.compile("-DANLOPENCL_USE_OPENCL")
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
}