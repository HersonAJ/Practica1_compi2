package com.example.piglatin.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EditorComponent {

    private BorderPane view;
    private TextArea textArea;
    private HBox editorContainer;

    public EditorComponent() {
        view = new BorderPane();
        view.getStyleClass().add("editor-container");

        textArea = new TextArea();
        textArea.setPromptText("// Escribe tu codigo Latinus aqui");
        textArea.setWrapText(false);
        textArea.getStyleClass().add("code-input");

        editorContainer = new HBox();
        editorContainer.setPadding(Insets.EMPTY);
        HBox.setHgrow(textArea, Priority.ALWAYS);
        editorContainer.getChildren().add(textArea);

        view.setCenter(editorContainer);
    }

    public BorderPane getView() {
        return view;
    }

    public String getCode() {
        return textArea.getText();
    }

    public void setCode(String code) {
        textArea.setText(code);
    }

    public void clear() {
        textArea.clear();
    }
}