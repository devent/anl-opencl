package com.anrisoftware.anlopencl.jmeapp.controllers;

import com.google.inject.AbstractModule;

/**
 * @see GlobalKeys
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class ControllersModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GlobalKeys.class).asEagerSingleton();
    }
}
