/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App
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
 * ANL-OpenCL :: JME3 - App is a derivative work based on Josua Tippetts' C++ library:
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
package com.anrisoftware.anlopencl.jmeapp.model;

import com.anrisoftware.anlopencl.jme.opencl.AnlKernel;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

@ToString
public class ObservableGameMainPaneProperties {

    @Data
    public static class GameMainPaneProperties {

        public double splitMainPosition = 0.5;

        public String lastExpandedPane = "kernelInputsPane";

        public int seed = 0;

        public int width = 1024;

        public int height = 1024;

        public String kernelCode = "#include <noise_gen.h>\n" + "#include <kernel.h>\n" + "\n"
                + "kernel void value_noise2D_noInterp(\n" + "global vector2 *input,\n" + "global REAL *output\n"
                + ") {\n" + "    int id0 = get_global_id(0);\n"
                + "    output[id0] = value_noise2D(input[id0], 200, noInterp);\n" + "}\n" + "";

        public String fileName = "";

        @JsonIgnore
        public transient AnlKernel kernel;

    }

    public final DoubleProperty splitMainPosition;

    public final StringProperty lastExpandedPane;

    public final IntegerProperty seed;

    public final IntegerProperty width;

    public final IntegerProperty height;

    public final StringProperty kernelCode;

    public final ObjectProperty<AnlKernel> kernel;

    public final StringProperty fileName;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public ObservableGameMainPaneProperties(GameMainPaneProperties p) {
        this.splitMainPosition = JavaBeanDoublePropertyBuilder.create().bean(p).name("splitMainPosition").build();
        this.lastExpandedPane = JavaBeanStringPropertyBuilder.create().bean(p).name("lastExpandedPane").build();
        this.seed = JavaBeanIntegerPropertyBuilder.create().bean(p).name("seed").build();
        this.width = JavaBeanIntegerPropertyBuilder.create().bean(p).name("width").build();
        this.height = JavaBeanIntegerPropertyBuilder.create().bean(p).name("height").build();
        this.kernelCode = JavaBeanStringPropertyBuilder.create().bean(p).name("kernelCode").build();
        this.kernel = JavaBeanObjectPropertyBuilder.create().bean(p).name("kernel").build();
        this.fileName = JavaBeanStringPropertyBuilder.create().bean(p).name("fileName").build();
    }

    public void copy(GameMainPaneProperties other) {
        splitMainPosition.set(other.splitMainPosition);
        lastExpandedPane.set(other.lastExpandedPane);
        seed.set(other.seed);
        width.set(other.width);
        height.set(other.height);
        kernelCode.set(other.kernelCode);
        kernel.set(other.kernel);
        fileName.set(other.fileName);
    }

}
