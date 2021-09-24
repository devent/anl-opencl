package com.anrisoftware.anlopencl.jmeapp.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
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

        public String fileName = "";

    }

    public final DoubleProperty splitMainPosition;

    public final StringProperty lastExpandedPane;

    public final StringProperty fileName;

    @SneakyThrows
    public ObservableGameMainPaneProperties(GameMainPaneProperties p) {
        this.splitMainPosition = JavaBeanDoublePropertyBuilder.create().bean(p).name("splitMainPosition").build();
        this.lastExpandedPane = JavaBeanStringPropertyBuilder.create().bean(p).name("lastExpandedPane").build();
        this.fileName = JavaBeanStringPropertyBuilder.create().bean(p).name("fileName").build();
    }

    public void copy(GameMainPaneProperties other) {
        splitMainPosition.set(other.splitMainPosition);
        lastExpandedPane.set(other.lastExpandedPane);
        fileName.set(other.fileName);
    }

}
