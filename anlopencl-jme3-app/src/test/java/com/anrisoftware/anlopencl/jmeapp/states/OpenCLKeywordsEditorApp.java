package com.anrisoftware.anlopencl.jmeapp.states;

import org.fxmisc.flowless.VirtualizedScrollPane;

import com.anrisoftware.anlopencl.jmeapp.controllers.OpenCLKeywordsEditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class OpenCLKeywordsEditorApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var codeArea = new OpenCLKeywordsEditor().getCodeArea();
        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(codeArea)), 600, 400);
        scene.getStylesheets().add(OpenCLKeywordsEditorApp.class.getResource("/opencl-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Java Keywords Async Demo");
        primaryStage.show();
    }

    @Override
    public void stop() {
    }
}
