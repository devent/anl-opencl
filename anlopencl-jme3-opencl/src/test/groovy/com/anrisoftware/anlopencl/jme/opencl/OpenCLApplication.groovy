package com.anrisoftware.anlopencl.jme.opencl

import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.google.inject.Provides
import com.jme3.app.SimpleApplication
import com.jme3.asset.AssetManager
import com.jme3.system.AppSettings

class OpenCLApplication extends SimpleApplication {

    SourceResourcesLoader sourceResourcesLoader

    Injector injector

    OpenCLApplication() {
        def settings = new AppSettings(true);
        settings.setOpenCLSupport(true);
        settings.setVSync(true);
        setShowSettings(false);
        setSettings(settings);
    }

    public void start(Injector injector) {
        this.injector = injector.createChildInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                    }
                    @Provides
                    AssetManager getAssetManager() {
                        OpenCLApplication.this.assetManager
                    }
                })
        super.start();
    }

    @Override
    public void simpleInitApp() {
        sourceResourcesLoader = injector.getInstance(SourceResourcesLoader)
        sourceResourcesLoader.load()
    }
}
