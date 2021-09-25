package com.anrisoftware.anlopencl.jmeapp.states;

import static com.google.inject.name.Names.named;

import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public class EditorGuiStatesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<Map<String, KeyMapping>>() {
        }).annotatedWith(named("keyMappings")).toProvider(KeyMappingsProvider.class).asEagerSingleton();
        bind(new TypeLiteral<Map<String, JmeMapping>>() {
        }).annotatedWith(named("jmeMappings")).toProvider(JmeMappingsProvider.class).asEagerSingleton();
    }
}
