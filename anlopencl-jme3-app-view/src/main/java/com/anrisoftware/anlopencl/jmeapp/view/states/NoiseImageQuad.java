package com.anrisoftware.anlopencl.jmeapp.view.states;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class NoiseImageQuad {

    public interface NoiseImageQuadFactory {
        NoiseImageQuad create(ImageComponent c);
    }

    private final Picture pic;

    private final Texture2D unsetTex;

    private final AssetManager assetManager;

    @Inject
    public NoiseImageQuad(AssetManager assetManager, @Assisted ImageComponent c) {
        this.pic = new Picture("");
        this.unsetTex = (Texture2D) assetManager.loadTexture("Textures/unset-image.png");
        this.assetManager = assetManager;
        pic.setTexture(assetManager, unsetTex, true);
        pic.setPosition(0, 0);
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
