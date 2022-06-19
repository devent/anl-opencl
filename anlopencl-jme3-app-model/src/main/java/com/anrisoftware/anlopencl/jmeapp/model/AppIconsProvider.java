package com.anrisoftware.anlopencl.jmeapp.model;

import javax.inject.Inject;
import javax.inject.Provider;

import com.anrisoftware.resources.images.external.Images;
import com.anrisoftware.resources.images.external.ImagesFactory;

/**
 * Provides the {@link Images} from {@code AppImages.properties}
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class AppIconsProvider implements Provider<Images> {

    private final Images images;

    @Inject
    public AppIconsProvider(ImagesFactory images) {
        this.images = images.create("AppIcons");
    }

    @Override
    public Images get() {
        return images;
    }

}
