package com.anrisoftware.anlopencl.jmeapp.states;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.components.ImageComponent;
import com.google.inject.assistedinject.Assisted;
import com.jme3.asset.AssetManager;
import com.jme3.opencl.CommandQueue;
import com.jme3.opencl.Context;
import com.jme3.opencl.Image;
import com.jme3.opencl.Kernel;
import com.jme3.opencl.MemoryAccess;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class ImageQuad {

    private final Texture2D tex;

    private final Picture pic;

    private final Context context;

    private final CommandQueue queue;

    private final ImageComponent c;

    private Image texCL;

    @Inject
    public ImageQuad(AssetManager assetManager, com.jme3.opencl.Context context, @Assisted ImageComponent c) {
        this.c = c;
        this.context = context;
        tex = new Texture2D(c.width, c.height, 1, com.jme3.texture.Image.Format.RGBA8);
        this.pic = new Picture("");
        pic.setTexture(assetManager, tex, true);
        pic.setPosition(0, 0);
        pic.setWidth(c.width);
        pic.setHeight(c.height);
        this.queue = context.createQueue().register();
    }

    public void bindImage() {
        texCL = context.bindImage(tex, MemoryAccess.WRITE_ONLY).register();
    }

    private void updateOpenCL(float tpf) {
        texCL.acquireImageForSharingNoEvent(queue);
        var ws = new Kernel.WorkSize(c.width, c.height);
        c.kernel.Run1NoEvent(queue, ws, texCL, C, 16);
        texCL.releaseImageForSharingNoEvent(clQueue);
    }

    public Picture getPic() {
        return pic;
    }

    public Texture2D getTex() {
        return tex;
    }

    public Image getTexCL() {
        return texCL;
    }
}
