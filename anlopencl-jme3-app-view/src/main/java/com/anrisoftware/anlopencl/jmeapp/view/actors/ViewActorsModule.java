package com.anrisoftware.anlopencl.jmeapp.view.actors;

import com.anrisoftware.anlopencl.jmeapp.view.actors.ViewActor.ViewActorFactory;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @see ViewActorFactory
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
public class ViewActorsModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(ViewActor.class, ViewActor.class).build(ViewActorFactory.class));
    }

}
