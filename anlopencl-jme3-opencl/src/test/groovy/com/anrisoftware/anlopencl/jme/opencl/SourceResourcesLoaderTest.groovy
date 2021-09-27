package com.anrisoftware.anlopencl.jme.opencl

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import com.google.inject.Guice
import com.google.inject.Injector

/**
 * @see SourceResourcesProvider
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class SourceResourcesLoaderTest {

    @Test
    void "load assets"() {
        def app = new OpenCLApplication();
        app.start(injector)
    }

    static Injector injector

    @BeforeAll
    static void setup() {
        injector = Guice.createInjector()
    }
}
