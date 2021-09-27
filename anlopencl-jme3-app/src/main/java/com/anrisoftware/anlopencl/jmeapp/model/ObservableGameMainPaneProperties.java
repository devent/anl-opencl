package com.anrisoftware.anlopencl.jmeapp.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
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

    }

    public final DoubleProperty splitMainPosition;

    public final StringProperty lastExpandedPane;

    public final IntegerProperty seed;

    public final IntegerProperty width;

    public final IntegerProperty height;

    public final StringProperty kernelCode;

    public final StringProperty fileName;

    @SneakyThrows
    public ObservableGameMainPaneProperties(GameMainPaneProperties p) {
        this.splitMainPosition = JavaBeanDoublePropertyBuilder.create().bean(p).name("splitMainPosition").build();
        this.lastExpandedPane = JavaBeanStringPropertyBuilder.create().bean(p).name("lastExpandedPane").build();
        this.seed = JavaBeanIntegerPropertyBuilder.create().bean(p).name("seed").build();
        this.width = JavaBeanIntegerPropertyBuilder.create().bean(p).name("width").build();
        this.height = JavaBeanIntegerPropertyBuilder.create().bean(p).name("height").build();
        this.kernelCode = JavaBeanStringPropertyBuilder.create().bean(p).name("kernelCode").build();
        this.fileName = JavaBeanStringPropertyBuilder.create().bean(p).name("fileName").build();
    }

    public void copy(GameMainPaneProperties other) {
        splitMainPosition.set(other.splitMainPosition);
        lastExpandedPane.set(other.lastExpandedPane);
        seed.set(other.seed);
        width.set(other.width);
        height.set(other.height);
        kernelCode.set(other.kernelCode);
        fileName.set(other.fileName);
    }

}
