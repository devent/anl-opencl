/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Model
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
 * ANL-OpenCL :: JME3 - App - Model is a derivative work based on Josua Tippetts' C++ library:
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
 * ANL-OpenCL :: JME3 - App - Model bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl.jmeapp.model;

import com.anrisoftware.anlopencl.jme.opencl.AnlKernel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanBooleanProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanDoubleProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatProperty;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanLongPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@ToString
public class ObservableGameMainPaneProperties {

    /**
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @Data
    public static class GameMainPaneProperties {

        public double splitMainPosition = 0.5;

        public String lastExpandedPane = "kernelInputsPane";

        public String kernelName = "map2d_image";

        public String kernelLog = "";

        public int seed = 0;

        public int width = 1024;

        public int height = 1024;

        public double z = 0.0;

        public int dim = 2;

        public int columns = 1;

        public int rows = 1;

        public float mapx0 = -1;

        public float mapy0 = -1;

        public float mapz0 = 0;

        public float mapx1 = 1;

        public float mapy1 = 1;

        public float mapz1 = 1;

        public boolean map3d = false;

        public float cameraPosX = 0.002901543f;

        public float cameraPosY = -0.013370683f;

        public float cameraPosZ = 28.217747f;

        public float cameraRotX = -4.8154507E-6f;

        public float cameraRotY = 0.9999911f;

        public float cameraRotZ = 0.0012241602f;

        public float cameraRotW = 0.004027171f;

        // @formatter:off
        public String kernelCode = "#include <opencl_utils.h>\n"
                + "#include <noise_gen.h>\n"
                + "#include <imaging.h>\n"
                + "#include <kernel.h>\n"
                + "\n"
                + "kernel void map2d_image(\n"
                + "global struct SMappingRanges *ranges,\n"
                + "const float z,\n"
                + "const int dim,\n"
                + "global float *coord,\n"
                + "write_only image2d_t output,\n"
                + ") {\n"
                + "    const int g0 = get_global_id(0);\n"
                + "    const int g1 = get_global_id(1);\n"
                + "    const int w = get_global_size(0);\n"
                + "    const int h = get_global_size(1);\n"
                + "    if (l0 == get_local_id(0)) {\n"
                + "        map2D(coord, calc_seamless_none, *ranges, w, h, z);\n"
                + "    }\n"
                + "    const int i = (g0 * w + g1) * dim;\n"
                + "    const float a = 0.5;\n"
                + "    const float r = value_noise3D(coord[i], 200, noInterp);\n"
                + "    const float g = value_noise3D(coord[i], 200, noInterp);\n"
                + "    const float b = value_noise3D(coord[i], 200, noInterp);\n"
                + "    write_imagef(output, (int2)(g0, g1), (float4)(r, g, b, a));\n"
                + "}\n"
                + "";
        // @formatter:on

        public long codeLastChange = 0;

        public long codeLastCompiled = 0;

        public String fileName = "";

        @JsonIgnore
        public transient AnlKernel kernel;

    }

    public final DoubleProperty splitMainPosition;

    public final StringProperty lastExpandedPane;

    public final IntegerProperty seed;

    public final IntegerProperty width;

    public final IntegerProperty height;

    public final IntegerProperty dim;

    public final IntegerProperty rows;

    public final IntegerProperty columns;

    public final FloatProperty mapx0;

    public final FloatProperty mapy0;

    public final FloatProperty mapz0;

    public final FloatProperty mapx1;

    public final FloatProperty mapy1;

    public final FloatProperty mapz1;

    public final StringProperty kernelName;

    public final StringProperty kernelCode;

    public final LongProperty codeLastChange;

    public final LongProperty codeLastCompiled;

    public final StringProperty kernelLog;

    public final ObjectProperty<AnlKernel> kernel;

    public final StringProperty fileName;

    public final JavaBeanDoubleProperty z;

    public final JavaBeanBooleanProperty map3d;

    public final JavaBeanFloatProperty cameraPosX;

    public final JavaBeanFloatProperty cameraPosY;

    public final JavaBeanFloatProperty cameraPosZ;

    public final JavaBeanFloatProperty cameraRotX;

    public final JavaBeanFloatProperty cameraRotY;

    public final JavaBeanFloatProperty cameraRotZ;

    public final JavaBeanFloatProperty cameraRotW;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public ObservableGameMainPaneProperties(GameMainPaneProperties p) {
        this.splitMainPosition = JavaBeanDoublePropertyBuilder.create().bean(p).name("splitMainPosition").build();
        this.lastExpandedPane = JavaBeanStringPropertyBuilder.create().bean(p).name("lastExpandedPane").build();
        this.seed = JavaBeanIntegerPropertyBuilder.create().bean(p).name("seed").build();
        this.width = JavaBeanIntegerPropertyBuilder.create().bean(p).name("width").build();
        this.height = JavaBeanIntegerPropertyBuilder.create().bean(p).name("height").build();
        this.z = JavaBeanDoublePropertyBuilder.create().bean(p).name("z").build();
        this.dim = JavaBeanIntegerPropertyBuilder.create().bean(p).name("dim").build();
        this.rows = JavaBeanIntegerPropertyBuilder.create().bean(p).name("rows").build();
        this.columns = JavaBeanIntegerPropertyBuilder.create().bean(p).name("columns").build();
        this.mapx0 = JavaBeanFloatPropertyBuilder.create().bean(p).name("mapx0").build();
        this.mapy0 = JavaBeanFloatPropertyBuilder.create().bean(p).name("mapy0").build();
        this.mapz0 = JavaBeanFloatPropertyBuilder.create().bean(p).name("mapz0").build();
        this.mapx1 = JavaBeanFloatPropertyBuilder.create().bean(p).name("mapx1").build();
        this.mapy1 = JavaBeanFloatPropertyBuilder.create().bean(p).name("mapy1").build();
        this.mapz1 = JavaBeanFloatPropertyBuilder.create().bean(p).name("mapz1").build();
        this.map3d = JavaBeanBooleanPropertyBuilder.create().bean(p).name("map3d").build();
        this.kernelName = JavaBeanStringPropertyBuilder.create().bean(p).name("kernelName").build();
        this.kernelCode = JavaBeanStringPropertyBuilder.create().bean(p).name("kernelCode").build();
        this.codeLastChange = JavaBeanLongPropertyBuilder.create().bean(p).name("codeLastChange").build();
        this.codeLastCompiled = JavaBeanLongPropertyBuilder.create().bean(p).name("codeLastCompiled").build();
        this.kernelLog = JavaBeanStringPropertyBuilder.create().bean(p).name("kernelLog").build();
        this.kernel = JavaBeanObjectPropertyBuilder.create().bean(p).name("kernel").build();
        this.fileName = JavaBeanStringPropertyBuilder.create().bean(p).name("fileName").build();
        this.cameraPosX = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosX").build();
        this.cameraPosY = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosY").build();
        this.cameraPosZ = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraPosZ").build();
        this.cameraRotX = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotX").build();
        this.cameraRotY = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotY").build();
        this.cameraRotZ = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotZ").build();
        this.cameraRotW = JavaBeanFloatPropertyBuilder.create().bean(p).name("cameraRotW").build();
    }

    public void copy(GameMainPaneProperties other) {
        splitMainPosition.set(other.splitMainPosition);
        lastExpandedPane.set(other.lastExpandedPane);
        seed.set(other.seed);
        width.set(other.width);
        height.set(other.height);
        z.set(other.z);
        dim.set(other.dim);
        rows.set(other.rows);
        columns.set(other.columns);
        mapx0.set(other.mapx0);
        mapy0.set(other.mapy0);
        mapz0.set(other.mapz0);
        mapx1.set(other.mapx1);
        mapy1.set(other.mapy1);
        mapz1.set(other.mapz1);
        map3d.set(other.map3d);
        kernelName.set(other.kernelName);
        kernelCode.set(other.kernelCode);
        codeLastChange.set(other.codeLastChange);
        codeLastCompiled.set(other.codeLastCompiled);
        kernel.set(other.kernel);
        kernelLog.set(other.kernelLog);
        fileName.set(other.fileName);
    }

    public Vector3f getCameraPos() {
        return new Vector3f(cameraPosX.get(), cameraPosY.get(), cameraPosZ.get());
    }

    public Quaternion getCameraRot() {
        return new Quaternion(cameraRotX.get(), cameraRotY.get(), cameraRotZ.get(), cameraRotW.get());
    }
}
