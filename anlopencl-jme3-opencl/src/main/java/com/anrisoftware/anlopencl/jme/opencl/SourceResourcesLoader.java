/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JOCL
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
 * ANL-OpenCL :: JOCL is a derivative work based on Josua Tippetts' C++ library:
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
 */
package com.anrisoftware.anlopencl.jme.opencl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;

/**
 * Loads a source from the classpath.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class SourceResourcesLoader {

    /**
     * Loads the specified source from the classpath.
     *
     * @param name the {@link String} name of the source, for example
     *             <code>hashing.h</code>
     * @return the {@link String} content of the resource.
     * @throws IOException if there was an error reading the resource.
     */
    public static String loadResource(String name) throws IOException {
        var resname = String.format("/%s", name);
        var r = AnlKernel.class.getResource(resname);
        notNull(r, "Resource '%s' is null", name);
        return IOUtils.toString(r, UTF_8);
    }

    @Inject
    private AssetManager assetManager;

    public void load() {
        assetManager.registerLoader(SourceFileImporter.class, "h", "c", "cl");
        putResource("opencl_utils.h");
        putResource("opencl_utils.c");
        putResource("qsort.h");
        putResource("qsort.c");
        putResource("utility.h");
        putResource("utility.c");
        putResource("hashing.h");
        putResource("hashing.c");
        putResource("noise_lut.h");
        putResource("noise_lut.c");
        putResource("noise_gen.h");
        putResource("noise_gen.c");
        putResource("imaging.h");
        putResource("imaging.c");
        putResource("kernel.h");
        putResource("kernel.c");
        putResource("extern/RandomCL/generators/kiss09.cl");
        putResource("random.cl");
    }

    private void putResource(String name) {
        String res = String.format("/%s", name);
        assetManager.loadAssetFromStream(new AssetKey<>(FilenameUtils.getName(name)),
                getClass().getResourceAsStream(res));
    }
}
