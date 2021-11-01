package com.anrisoftware.anlopencl.jmeapp.view.states;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

/**
 * Shows the noise image in a quad object.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class NoiseImageQuad {

    /**
     * Factory to create a new {@link NoiseImageQuad}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface NoiseImageQuadFactory {
        NoiseImageQuad create(ImageComponent c);
    }

    private final Picture pic;

    private final Texture2D unsetTex;

    private final AssetManager assetManager;

    @Inject
    public NoiseImageQuad(AssetManager assetManager, @Assisted ImageComponent c) {
        this.pic = new Picture(NoiseImageQuad.class.getSimpleName());
        this.unsetTex = (Texture2D) assetManager.loadTexture("Textures/unset-image.png");
        this.assetManager = assetManager;
        pic.setQueueBucket(Bucket.Opaque);
        pic.setTexture(assetManager, unsetTex, true);
        pic.setPosition(-c.width / 2f, -c.height / 2f);
        pic.setWidth(c.width);
        pic.setHeight(c.height);
    }

    public void setTex(Texture2D tex) {
        pic.setTexture(assetManager, tex, true);
    }

    public Picture getPic() {
        return pic;
    }

}
