package com.anrisoftware.anlopencl.jmeapp.states;

import com.badlogic.ashley.core.Engine;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MainPanelsUiTestModule extends AbstractModule {

    private final MainPanelsUiTest owner;

    @Provides
    public Application getApp() {
        return owner;
    }

    @Provides
    public InputManager getInputManager() {
        return owner.getInputManager();
    }

    @Provides
    public Camera getCamera() {
        return owner.getCamera();
    }

    @Provides
    public Engine getEngine() {
        return owner.engine;
    }

}
