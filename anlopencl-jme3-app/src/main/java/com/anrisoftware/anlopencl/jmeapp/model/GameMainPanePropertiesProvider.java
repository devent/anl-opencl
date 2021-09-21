package com.anrisoftware.anlopencl.jmeapp.model;

import javax.inject.Provider;

import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties.GameMainPaneProperties;

public class GameMainPanePropertiesProvider implements Provider<ObservableGameMainPaneProperties> {

    private final GameMainPaneProperties p;

    private final ObservableGameMainPaneProperties op;

    public GameMainPanePropertiesProvider() {
        this.p = new GameMainPaneProperties();
        this.op = new ObservableGameMainPaneProperties(p);
    }

    @Override
    public ObservableGameMainPaneProperties get() {
        return op;
    }

}
