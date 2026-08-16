package com.example.piglatin.ui;

import com.example.piglatin.ui.components.EditorComponent;
import com.example.piglatin.ui.components.ErrorComponent;
import com.example.piglatin.ui.components.HeaderComponent;
import com.example.piglatin.ui.components.TraduccionComponent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.SplitPane;

public class MainController {

    private BorderPane root;
    private HeaderComponent header;
    private EditorComponent editor;
    private TraduccionComponent traduccion;
    private ErrorComponent errorComponent;

    public MainController() {
        root = new BorderPane();

        header = new HeaderComponent();
        editor = new EditorComponent();
        traduccion = new TraduccionComponent();
        errorComponent = new ErrorComponent();

        // Conectar header con editor y traduccion
        header.setEditor(editor);
        header.setTraduccion(traduccion);
        header.setErrorComponent(errorComponent);
        root.setTop(header.getView());

        // Editor + Traducción (SplitPane) - usar las mismas instancias
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(editor.getView(), traduccion.getView());
        splitPane.setDividerPositions(0.5);

        root.setCenter(splitPane);
    }

    public BorderPane getView() {
        return root;
    }
}