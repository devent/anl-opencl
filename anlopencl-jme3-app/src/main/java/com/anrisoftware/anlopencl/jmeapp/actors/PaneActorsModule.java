package com.anrisoftware.anlopencl.jmeapp.actors;

import com.anrisoftware.anlopencl.jmeapp.actors.GameMainPanelActor.GameMainPanelActorFactory;
import com.anrisoftware.anlopencl.jmeapp.actors.ToolbarButtonsActor.ToolbarButtonsActorFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class PaneActorsModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(AbstractMainPanelActor.class, GameMainPanelActor.class)
                .build(GameMainPanelActorFactory.class));
        install(new FactoryModuleBuilder().implement(ToolbarButtonsActor.class, ToolbarButtonsActor.class)
                .build(ToolbarButtonsActorFactory.class));
    }
}
