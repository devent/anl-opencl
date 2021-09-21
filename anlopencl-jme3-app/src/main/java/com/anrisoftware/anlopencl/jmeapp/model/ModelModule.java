package com.anrisoftware.anlopencl.jmeapp.model;

import com.google.inject.AbstractModule;

public class ModelModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GameMainPanePropertiesProvider.class).asEagerSingleton();
    }
}
