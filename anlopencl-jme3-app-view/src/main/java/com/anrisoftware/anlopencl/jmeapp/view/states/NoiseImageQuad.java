/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View
 * ****************************************************************************
 *
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View bundles and uses the RandomCL library:
 * https://github.com/bstatcomp/RandomCL
 * ****************************************************************************
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
