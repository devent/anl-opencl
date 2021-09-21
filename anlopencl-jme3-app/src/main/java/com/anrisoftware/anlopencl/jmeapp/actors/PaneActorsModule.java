package com.anrisoftware.anlopencl.jmeapp.actors;

import com.anrisoftware.anlopencl.jmeapp.actors.GameMainPanelActor.GameMainPanelActorFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class PaneActorsModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(AbstractMainPanelActor.class, GameMainPanelActor.class)
                .build(GameMainPanelActorFactory.class));
    }
}
