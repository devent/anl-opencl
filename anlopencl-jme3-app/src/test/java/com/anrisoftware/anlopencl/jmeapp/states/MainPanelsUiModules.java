package com.anrisoftware.anlopencl.jmeapp.states;

import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProviderModule;
import com.anrisoftware.anlopencl.jmeapp.actors.MainActorsModule;
import com.anrisoftware.anlopencl.jmeapp.actors.PaneActorsModule;
import com.anrisoftware.anlopencl.jmeapp.model.ModelModule;
import com.anrisoftware.resources.binary.internal.binaries.BinariesResourcesModule;
import com.anrisoftware.resources.binary.internal.maps.BinariesDefaultMapsModule;
import com.anrisoftware.resources.images.internal.images.ImagesResourcesModule;
import com.anrisoftware.resources.images.internal.mapcached.ResourcesImagesCachedMapModule;
import com.anrisoftware.resources.images.internal.scaling.ResourcesSmoothScalingModule;
import com.anrisoftware.resources.texts.internal.texts.TextsResourcesDefaultModule;
import com.google.inject.AbstractModule;

public class MainPanelsUiModules extends AbstractModule {

    @Override
    protected void configure() {
        install(new ActorSystemProviderModule());
        install(new MainActorsModule());
        install(new PaneActorsModule());
        install(new EditorGuiStatesModule());
        install(new GuiStatesModule());
        install(new ModelModule());
        // Resources
        install(new ImagesResourcesModule());
        install(new ResourcesImagesCachedMapModule());
        install(new ResourcesSmoothScalingModule());
        install(new TextsResourcesDefaultModule());
        install(new BinariesResourcesModule());
        install(new BinariesDefaultMapsModule());
    }
}
