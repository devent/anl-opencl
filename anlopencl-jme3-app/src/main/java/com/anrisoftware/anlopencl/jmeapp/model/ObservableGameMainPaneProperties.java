package com.anrisoftware.anlopencl.jmeapp.model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

@ToString
public class ObservableGameMainPaneProperties {

    @Data
    public static class GameMainPaneProperties {

        public String fileName;
    }

    public final StringProperty fileName;

    @SneakyThrows
    public ObservableGameMainPaneProperties(GameMainPaneProperties p) {
        this.fileName = JavaBeanStringPropertyBuilder.create().bean(p).name("fileName").build();
    }

    public void copy(GameMainPaneProperties other) {
        fileName.set(other.fileName);
    }

}
