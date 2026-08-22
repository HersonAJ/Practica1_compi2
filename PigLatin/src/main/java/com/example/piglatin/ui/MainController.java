package com.example.piglatin.ui;

import com.example.piglatin.ui.components.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.SplitPane;

public class MainController {

    private BorderPane root;
    private HeaderComponent header;
    private EditorComponent editor;
    private TraduccionComponent traduccion;
    private ErrorComponent errorComponent;
    private ASTComponent astComponent;

    public MainController() {
        root = new BorderPane();

        header = new HeaderComponent();
        editor = new EditorComponent();
        traduccion = new TraduccionComponent();
        errorComponent = new ErrorComponent();
        astComponent = new ASTComponent();

        // Conectar header con editor y traduccion
        header.setEditor(editor);
        header.setTraduccion(traduccion);
        header.setErrorComponent(errorComponent);
        header.setASTComponent(astComponent);
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