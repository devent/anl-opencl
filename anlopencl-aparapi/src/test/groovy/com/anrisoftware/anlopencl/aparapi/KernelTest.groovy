package com.anrisoftware.anlopencl.aparapi

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.google.inject.Guice
import com.google.inject.Injector

/**
 * Kernel test.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class KernelTest {

    @Test
    void "test"() {
        def kernel = injector.getInstance(Kernel)
        kernel.compileKernel()
    }

    @BeforeEach
    void injectDeps() {
        injector.injectMembers(this)
    }

    static Injector injector

    @BeforeAll
    static void createInjector() {
        injector = Guice.createInjector()
    }
}
