package com.anrisoftware.anlopencl.jmeapp.view.states;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class NoiseImageQuad {

    public interface NoiseImageQuadFactory {
        NoiseImageQuad create(ImageComponent c, Texture2D tex);
    }

    private final Texture2D tex;

    private final Picture pic;

    @Inject
    public NoiseImageQuad(AssetManager assetManager, @Assisted ImageComponent c, @Assisted Texture2D tex) {
        this.pic = new Picture("");
        this.tex = tex;
        pic.setTexture(assetManager, tex, true);
        pic.setPosition(0, 0);
        pic.setWidth(c.width);
        pic.setHeight(c.height);
    }

    public Picture getPic() {
        return pic;
    }

    public Texture2D getTex() {
        return tex;
    }

}
