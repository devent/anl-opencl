package com.anrisoftware.anlopencl.jmeapp.actors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.jocl.cl_context;

import com.anrisoftware.easycl.corejocl.ContextFactory;
import com.anrisoftware.easycl.corejocl.DeviceFactory;
import com.anrisoftware.easycl.corejocl.PlatformFactory;

public class CLContextBuild {

    @Inject
    private PlatformFactory platformFactory;

    @Inject
    private DeviceFactory deviceFactory;

    @Inject
    private ContextFactory contextFactory;

    public CompletionStage<Supplier<cl_context>> createClContext(Executor ex) {
        return CompletableFuture.supplyAsync(() -> {
            var platform = platformFactory.create();
            var device = deviceFactory.create(platform);
            var context = contextFactory.create(platform, device);
            return context;
        }, ex);
    }

}
