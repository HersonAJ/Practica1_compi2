package com.example.piglatin.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class EditorComponent {

    private BorderPane view;
    private TextArea textArea;
    private TextArea lineNumbers;
    private HBox editorContainer;

    public EditorComponent() {
        view = new BorderPane();
        view.getStyleClass().add("editor-container");

        textArea = new TextArea();
        textArea.setPromptText("// Escribe tu codigo Latinus aqui");
        textArea.setWrapText(false);
        textArea.getStyleClass().add("code-input");


        lineNumbers = new TextArea();
        lineNumbers.setEditable(false);
        lineNumbers.setFocusTraversable(false);
        lineNumbers.setMouseTransparent(true);
        lineNumbers.setWrapText(false);
        lineNumbers.getStyleClass().add("line-numbers");

        // Sincronizar scroll vertical entre textArea y lineNumbers
        textArea.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            lineNumbers.setScrollTop(newVal.doubleValue());
        });

        // Actualizar numeros de linea cuando cambia el texto
        textArea.textProperty().addListener((obs, oldVal, newVal) -> {
            updateLineNumbers();
            adjustLineNumberWidth();
        });

        // Contenedor principal
        editorContainer = new HBox();
        editorContainer.setPadding(Insets.EMPTY);
        editorContainer.getChildren().addAll(lineNumbers, textArea);
        HBox.setHgrow(textArea, Priority.ALWAYS);

        // Inicializar
        updateLineNumbers();
        adjustLineNumberWidth();

        view.setCenter(editorContainer);
    }

    private void updateLineNumbers() {
        String text = textArea.getText();
        int lines = text.split("\n", -1).length;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append(i).append("\n");
        }
        lineNumbers.setText(sb.toString());
    }

    private void adjustLineNumberWidth() {
        String text = textArea.getText();
        int lines = text.split("\n", -1).length;
        int digits = String.valueOf(lines).length();
        int width = Math.max(40, digits * 10 + 28);
        lineNumbers.setPrefWidth(width);
        lineNumbers.setMinWidth(width);
        lineNumbers.setMaxWidth(width);
    }

    public BorderPane getView() {
        return view;
    }

    public String getCode() {
        return textArea.getText();
    }

    public void setCode(String code) {
        textArea.setText(code);
        updateLineNumbers();
        adjustLineNumberWidth();
    }

    public void clear() {
        textArea.clear();
        updateLineNumbers();
        adjustLineNumberWidth();
    }
}