package com.anrisoftware.anlopencl.jmeapp.view.states;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.view.components.ImageComponent;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.opencl.Image;
import com.jme3.opencl.MemoryAccess;
import com.jme3.opencl.lwjgl.LwjglContext;
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

    private Image texCL;

    private boolean textureSet = false;

    private boolean imageBoundOpenCL = false;

    private final LwjglContext context;

    private Texture2D tex;

    @Inject
    public NoiseImageQuad(AssetManager assetManager, com.jme3.opencl.Context context, @Assisted ImageComponent c) {
        this.pic = new Picture(NoiseImageQuad.class.getSimpleName());
        this.unsetTex = (Texture2D) assetManager.loadTexture("Textures/unset-image.png");
        this.assetManager = assetManager;
        this.context = (LwjglContext) context;
        pic.setQueueBucket(Bucket.Opaque);
        pic.setTexture(assetManager, unsetTex, true);
        pic.setPosition(-c.width / 2f, -c.height / 2f);
        pic.setWidth(c.width);
        pic.setHeight(c.height);
    }

    public void setNotSetTexture(boolean b) {
        if (b) {
            pic.setTexture(assetManager, unsetTex, true);
            texCL.release();
            textureSet = false;
            imageBoundOpenCL = false;
        }
    }

    public boolean isTextureSet() {
        return textureSet;
    }

    public boolean isTextureUploaded() {
        return tex != null && tex.getImage().getId() != -1;
    }

    public boolean isImageBoundOpenCL() {
        return imageBoundOpenCL;
    }

    public void setTex(Texture2D tex) {
        pic.setTexture(assetManager, tex, true);
        this.tex = tex;
        textureSet = true;
    }

    /**
     * Bind the texture to OpenCL after the texture was uploaded to OpenGL.
     */
    public void bindTextureToImage() {
        texCL = context.bindImage(tex, MemoryAccess.WRITE_ONLY);
        imageBoundOpenCL = true;
    }

    public Image getTexCL() {
        return texCL;
    }

    public Picture getPic() {
        return pic;
    }

}
