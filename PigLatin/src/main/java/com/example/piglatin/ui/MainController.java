// src/main/java/com/example/piglatin/ui/MainController.java
package com.example.piglatin.ui;

import com.example.piglatin.ui.components.EditorComponent;
import com.example.piglatin.ui.components.HeaderComponent;
import com.example.piglatin.ui.components.TraduccionComponent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.SplitPane;

public class MainController {

    private BorderPane root;
    private HeaderComponent header;
    private EditorComponent editor;
    private TraduccionComponent traduccion;

    public MainController() {
        root = new BorderPane();

        // Header
        header = new HeaderComponent();
        root.setTop(header.getView());

        // Editor + Traducción (SplitPane)
        editor = new EditorComponent();
        traduccion = new TraduccionComponent();

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(editor.getView(), traduccion.getView());
        splitPane.setDividerPositions(0.5);

        root.setCenter(splitPane);

        // Conectar header con editor
        header.setEditor(editor);
    }

    public BorderPane getView() {
        return root;
    }
}