package com.anrisoftware.anlopencl.jmeapp.model;

import javax.inject.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ObjectMapperProvider implements Provider<ObjectMapper> {

    private final ObjectMapper mapper;

    public ObjectMapperProvider() {
        this.mapper = new ObjectMapper(new YAMLFactory());
    }

    @Override
    public ObjectMapper get() {
        return mapper;
    }

}
