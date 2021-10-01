package com.anrisoftware.anlopencl.jme.opencl

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provides
import com.jme3.app.SimpleApplication
import com.jme3.asset.AssetManager
import com.jme3.system.AppSettings

class SourceResourcesLoaderApp extends SimpleApplication {

    public static void main(String[] args){
        def i = Guice.createInjector()
        def app = i.getInstance(SourceResourcesLoaderApp)
        app.start(i)
    }

    SourceResourcesProvider sourceResourcesLoader

    Injector injector

    SourceResourcesLoaderApp() {
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
                        SourceResourcesLoaderApp.this.assetManager
                    }
                })
        super.start();
    }

    @Override
    public void simpleInitApp() {
        sourceResourcesLoader = injector.getInstance(SourceResourcesProvider)
        sourceResourcesLoader.load()
    }
}
