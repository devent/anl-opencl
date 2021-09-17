package com.anrisoftware.anlopencl.anlkernel

import javax.inject.Inject

import org.jocl.CL;
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.anrisoftware.anlopencl.anlkernel.AnlKernel.AnlKernelFactory
import com.anrisoftware.easycl.corejocl.ContextFactory
import com.anrisoftware.easycl.corejocl.DeviceFactory
import com.anrisoftware.easycl.corejocl.JoclModule
import com.anrisoftware.easycl.corejocl.PlatformFactory
import com.google.inject.Guice
import com.google.inject.Injector

/**
 * Kernel test.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class KernelTest {

    @Inject
    PlatformFactory platformFactory

    @Inject
    DeviceFactory deviceFactory

    @Inject
    ContextFactory contextFactory

    @Inject
    AnlKernelFactory kernelFactory

    @Test
    void "compile kernel"() {
        def source = """
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
"""
        def platform = platformFactory.create()
        def device = deviceFactory.create(platform)
        def context = contextFactory.create(platform, device)
        def kernel = kernelFactory.create(context)
        kernel.withCloseable {
            kernel.buildLib()
            kernel.compileKernel(source)
        }
    }

    @BeforeEach
    void injectDeps() {
        injector.injectMembers(this)
    }

    static Injector injector

    @BeforeAll
    static void createInjector() {
        CL.exceptionsEnabled = true
        injector = Guice.createInjector(new JoclModule(), new AnlkernelModule())
    }
}
