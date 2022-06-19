package com.anrisoftware.anlopencl.jmeapp.model;

import javax.inject.Inject;
import javax.inject.Provider;

import com.anrisoftware.resources.texts.external.Texts;
import com.anrisoftware.resources.texts.external.TextsFactory;

/**
 * Provides the {@link Texts} from {@code AppTexts.properties}
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class AppTextsProvider implements Provider<Texts> {

    private final Texts texts;

    @Inject
    public AppTextsProvider(TextsFactory images) {
        this.texts = images.create("AppTexts");
    }

    @Override
    public Texts get() {
        return texts;
    }

}
