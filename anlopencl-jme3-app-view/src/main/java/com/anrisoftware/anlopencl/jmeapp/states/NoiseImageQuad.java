package com.anrisoftware.anlopencl.jmeapp.states;

import java.nio.FloatBuffer;

import javax.inject.Inject;

import org.lwjgl.system.MemoryUtil;

import com.anrisoftware.anlopencl.Map2DFactory;
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

public class NoiseImageQuad {

    private final Texture2D tex;

    private final Picture pic;

    private final Context context;

    private final CommandQueue queue;

    private final ImageComponent c;

    @Inject
    private Map2DFactory map2dFactory;

    private Image texCL;

    private final FloatBuffer outb;

    @Inject
    public NoiseImageQuad(AssetManager assetManager, com.jme3.opencl.Context context, @Assisted ImageComponent c) {
        this.c = c;
        this.context = context;
        tex = new Texture2D(c.width, c.height, 1, com.jme3.texture.Image.Format.RGBA8);
        outb = MemoryUtil.memAllocFloat(c.width * c.height * c.seamless.dim);
        this.pic = new Picture("");
        pic.setTexture(assetManager, tex, true);
        pic.setPosition(0, 0);
        pic.setWidth(c.width);
        pic.setHeight(c.height);
        this.queue = context.createQueue().register();
    }

    /**
     * Bind the texture to OpenCL in frame number 2.
     */
    public void bindTextureToImage() {
        texCL = context.bindImage(tex, MemoryAccess.WRITE_ONLY).register();
    }

    private void updateOpenCL(float tpf) {
        texCL.acquireImageForSharingNoEvent(queue);
        var work = new Kernel.WorkSize(c.width, c.height);
        int threads = Runtime.getRuntime().availableProcessors();
        var map = map2dFactory.create(outb, c.seamless.seamless, c.ranges, c.width, c.height, c.z, threads);
        c.kernel.Run1NoEvent(queue, work, texCL, C, 16);
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
