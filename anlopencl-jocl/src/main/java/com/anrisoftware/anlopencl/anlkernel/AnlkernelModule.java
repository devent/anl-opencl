package com.anrisoftware.anlopencl.anlkernel;

import java.util.Map;

import com.anrisoftware.anlopencl.anlkernel.AnlKernel.AnlKernelFactory;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @see AnlKernelFactory
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class AnlkernelModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(AnlKernel.class, AnlKernel.class).build(AnlKernelFactory.class));
        bind(new TypeLiteral<Map<String, String>>() {
        }).toProvider(SourceResourcesProvider.class).asEagerSingleton();
    }
}
