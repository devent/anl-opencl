package com.anrisoftware.anlopencl.jmeapp.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;

public class ModelModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GameMainPanePropertiesProvider.class).asEagerSingleton();
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
    }
}
