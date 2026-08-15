package com.example.piglatin.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TraduccionComponent {

    private BorderPane view;
    private TextArea textArea;
    private Label statusLabel;

    public TraduccionComponent() {
        view = new BorderPane();
        view.getStyleClass().add("traduccion-container");

        // Área de texto (solo lectura)
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setPromptText("La traducción aparecera aqui...");
        textArea.getStyleClass().add("code-input");

        // Estado
        statusLabel = new Label("Esperando compilacion...");
        statusLabel.setPadding(new Insets(8, 16, 8, 16));
        statusLabel.getStyleClass().add("status");

        VBox content = new VBox();
        content.getChildren().addAll(textArea, statusLabel);
        VBox.setVgrow(textArea, javafx.scene.layout.Priority.ALWAYS);

        view.setCenter(content);
    }

    public BorderPane getView() {
        return view;
    }

    public void setTranslation(String translation) {
        textArea.setText(translation);
        statusLabel.setText("Codigo compilado");
        statusLabel.getStyleClass().add("success");
    }

    public void clear() {
        textArea.clear();
        statusLabel.setText("Esperando compilacion...");
        statusLabel.getStyleClass().remove("success");
    }
}