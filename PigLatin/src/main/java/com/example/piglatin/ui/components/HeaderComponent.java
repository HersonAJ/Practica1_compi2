package com.example.piglatin.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class HeaderComponent {

    private HBox view;
    private EditorComponent editor;
    private File currentFile;

    private Button btnNuevo;
    private Button btnAbrir;
    private Button btnGuardar;
    private Button btnDescargar;
    private Button btnAnalizar;
    private Button btnTraducir;
    private Button btnAST;
    private Button btnTabla;
    private Button btnPila;
    private Button btnErrores;
    private Label lblFileName;

    public HeaderComponent() {
        view = new HBox();
        view.setPadding(new Insets(8, 16, 8, 16));
        view.setSpacing(8);
        view.setAlignment(Pos.CENTER_LEFT);
        view.getStyleClass().add("header");

        // Botones de archivo
        btnNuevo = createButton("📄 Nuevo");
        btnAbrir = createButton("📂 Abrir");
        btnGuardar = createButton("💾 Guardar");
        btnDescargar = createButton("⬇️ Descargar");

        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Botones de acción
        btnAnalizar = createButton("🔍 Analizar");
        btnAnalizar.getStyleClass().add("btn-primary");

        btnTraducir = createButton("🔄 Traducir");
        btnTraducir.getStyleClass().add("btn-success");

        Separator sep2 = new Separator();
        sep2.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Botones de visualización
        btnAST = createButton("AST");
        btnTabla = createButton("📊 Tabla");
        btnPila = createButton("📚 Pila");
        btnErrores = createButton("❌ Errores");

        // Nombre del archivo
        lblFileName = new Label("sin_titulo.lat");
        lblFileName.getStyleClass().add("file-name");

        // Espaciador
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Agregar todos al view
        view.getChildren().addAll(
                btnNuevo, btnAbrir, btnGuardar, btnDescargar,
                sep1,
                btnAnalizar, btnTraducir,
                sep2,
                btnAST, btnTabla, btnPila, btnErrores,
                spacer,
                lblFileName
        );

        // Eventos
        btnNuevo.setOnAction(e -> onNuevo());
        btnAbrir.setOnAction(e -> onAbrir());
        btnGuardar.setOnAction(e -> onGuardar());
        btnDescargar.setOnAction(e -> onDescargar());
        btnAnalizar.setOnAction(e -> onAnalizar());
        btnTraducir.setOnAction(e -> onTraducir());
        btnAST.setOnAction(e -> onAST());
        btnTabla.setOnAction(e -> onTabla());
        btnPila.setOnAction(e -> onPila());
        btnErrores.setOnAction(e -> onErrores());
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn");
        return btn;
    }

    public void setEditor(EditorComponent editor) {
        this.editor = editor;
    }

    public HBox getView() {
        return view;
    }

    private void onNuevo() {
        if (editor != null) {
            editor.setCode("// Nuevo archivo Latinus\n// Escribe tu codigo aqui\n\n");
            currentFile = null;
            lblFileName.setText("sin_titulo.lat");
        }
    }

    private void onAbrir() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Latinus", "*.lat")
        );

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                if (editor != null) {
                    editor.setCode(content);
                }
                currentFile = file;
                lblFileName.setText(file.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void onGuardar() {
        if (editor == null) return;

        if (currentFile == null) {
            onDescargar();
            return;
        }

        try {
            Files.writeString(currentFile.toPath(), editor.getCode(), StandardOpenOption.WRITE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void onDescargar() {
        if (editor == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Latinus", "*.lat")
        );
        fileChooser.setInitialFileName(currentFile != null ? currentFile.getName() : "codigo.lat");

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                Files.writeString(file.toPath(), editor.getCode());
                currentFile = file;
                lblFileName.setText(file.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void onAnalizar() {
        if (editor != null) {
            System.out.println("Analizando codigo...");
            System.out.println(editor.getCode());
        }
    }

    private void onTraducir() {
        if (editor != null) {
            System.out.println("Traduciendo a PigLatin...");
        }
    }

    private void onAST() {
        System.out.println("Mostrando AST");
    }

    private void onTabla() {
        System.out.println("Mostrando Tabla de Simbolos");
    }

    private void onPila() {
        System.out.println("Mostrando Pila de Llamadas");
    }

    private void onErrores() {
        System.out.println("Mostrando Errores");
    }
}