package com.example.piglatin.ui.components;

import com.example.piglatin.analizador.ast.NodoPrograma;
import com.example.piglatin.service.CompileService;
import com.example.piglatin.service.ResultadoCompilacion;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class HeaderComponent {

    private final HBox view;
    private EditorComponent editor;
    private TraduccionComponent traduccion;
    private ErrorComponent errorComponent;
    private File currentFile;
    private Button btnNuevo;
    private Button btnAbrir;
    private Button btnGuardar;
    private Button btnDescargar;
    private Button btnAnalizar;
    private Button btnEditar;
    private Button btnTraducir;
    private Button btnAST;
    private Button btnTabla;
    private Button btnPila;
    private Button btnErrores;
    private Button btnLeyenda;
    private Label lblFileName;
    private ASTComponent astComponent;

    public HeaderComponent() {
        this.view = new HBox();
        this.view.setPadding(new Insets(8, 16, 8, 16));
        this.view.setSpacing(8);
        this.view.setAlignment(Pos.CENTER_LEFT);
        this.view.getStyleClass().add("header");

        inicializarBotones();
        construirLayout();
        registrarEventos();
    }

    private void inicializarBotones() {
        // Archivo
        btnNuevo = createButton("📄 Nuevo");
        btnAbrir = createButton("📂 Abrir");
        btnGuardar = createButton("💾 Guardar");
        btnDescargar = createButton("⬇️ Descargar");

        btnAnalizar = createButton("🔍 Analizar");
        btnAnalizar.getStyleClass().add("btn-primary");

        btnEditar = createButton("✏ Editar");
        btnEditar.getStyleClass().add("btn-warning");

        btnTraducir = createButton("🔄 Traducir");
        btnTraducir.getStyleClass().add("btn-success");

        btnAST = createButton("AST");
        btnTabla = createButton("📊 Tabla");
        btnPila = createButton("📚 Pila");
        btnErrores = createButton("❌ Errores");
        btnLeyenda = createButton("🎨 Info Color");

        lblFileName = new Label("sin_titulo.lat");
        lblFileName.getStyleClass().add("file-name");
    }

    private void construirLayout() {
        Separator sep1 = crearSeparador();
        Separator sep2 = crearSeparador();

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        view.getChildren().addAll(
                btnNuevo, btnAbrir, btnGuardar, btnDescargar,
                sep1,
                btnAnalizar, btnEditar, btnTraducir,
                sep2,
                btnAST, btnTabla, btnPila, btnErrores, btnLeyenda,
                spacer,
                lblFileName
        );
    }

    private void registrarEventos() {
        btnNuevo.setOnAction(e -> onNuevo());
        btnAbrir.setOnAction(e -> onAbrir());
        btnGuardar.setOnAction(e -> onGuardar());
        btnDescargar.setOnAction(e -> onDescargar());
        btnAnalizar.setOnAction(e -> onAnalizar());
        btnEditar.setOnAction(e -> onEditar());
        btnTraducir.setOnAction(e -> onTraducir());
        btnAST.setOnAction(e -> onAST());
        btnTabla.setOnAction(e -> onTabla());
        btnPila.setOnAction(e -> onPila());
        btnErrores.setOnAction(e -> onErrores());
        btnLeyenda.setOnAction(e -> onMostrarLeyendaColores());
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
        if (editor == null) return;

        String codigo = editor.getCode();

        if (codigo == null || codigo.trim().isEmpty()) {
            if (errorComponent != null) {
                errorComponent.setErrors(
                        List.of("El codigo esta vacio"),
                        List.of(),
                        List.of()
                );
            }
            editor.cambiarAModoEdicion();
            return;
        }

        CompileService service = new CompileService();
        ResultadoCompilacion resultado = service.analizar(codigo);

        if (errorComponent != null) {
            errorComponent.setErrors(
                    resultado.erroresSintacticos(),
                    resultado.erroresSemanticos(),
                    resultado.errores()
            );
        }

        if (resultado.exito() && traduccion != null) {
            String trad = resultado.traduccion();
            traduccion.setTranslation(trad != null ? trad : "// La traduccion esta vacia");
        } else if (traduccion != null) {
            traduccion.setTranslation("// Compilación fallida - Revisar errores");
        }

        if (resultado.coloreado() != null && !resultado.coloreado().isEmpty()) {
            editor.aplicarColores(resultado.coloreado());
        } else {
            editor.cambiarAModoEdicion();
        }
    }

    private void onEditar() {
        if (editor != null) {
            editor.cambiarAModoEdicion();
        }
    }

    private void onTraducir() {
        if (editor != null) {
        }
    }

    private void onAST() {
        if (editor == null || astComponent == null) return;
        String codigo = editor.getCode();
        CompileService service = new CompileService();
        ResultadoCompilacion resultado = service.analizar(codigo);
        NodoPrograma programa = resultado.ast();
        if (programa != null) {
            astComponent.mostrarAST(programa);
        }
    }

    private void onTabla() {
    }

    private void onPila() {
        System.out.println("Mostrando Pila de Llamadas");
    }

    private void onErrores() {
        if (errorComponent != null) {
            Stage stage = new Stage();
            stage.setTitle("Errores de compilacion");
            stage.setScene(new Scene(errorComponent.getView(), 800, 400));
            stage.show();
        }
    }

    private void onMostrarLeyendaColores() {
        Stage modal = new Stage();
        modal.setTitle("Mapa de Colores de Sintaxis");
        modal.initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        agregarFilaLeyenda(grid, 0, "#DCDCAA", "Identificadores", "variables, funciones, estructuras");
        agregarFilaLeyenda(grid, 1, "#569CD6", "Palabras Clave / Secciones", "VARIABILES, MUNERA, MAIOR, si, dum, etc.");
        agregarFilaLeyenda(grid, 2, "#4EC9B0", "Tipos de Datos", "numerus, textum, decimalis, littera");
        agregarFilaLeyenda(grid, 3, "#B5CEA8", "Constantes y Booleanos", "enteros, decimales, verum, falsus");
        agregarFilaLeyenda(grid, 4, "#CE9178", "Textos y Caracteres", "cadenas de texto (\"...\") y caracteres ('...')");
        agregarFilaLeyenda(grid, 5, "#D16969", "Operadores", "+, -, *, /, =, ==, !=, <<, >>, etc.");
        agregarFilaLeyenda(grid, 6, "#808080", "Puntuación y Delimitadores", "; , . : { } [ ] ( )");
        agregarFilaLeyenda(grid, 7, "#FFFFFF", "Comentarios / No reconocido", "comentarios //, ## y espacios");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #1E1E1E;");

        Label titulo = new Label("Convención de Colores - Latinus");
        titulo.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-font-weight: bold;");

        layout.getChildren().addAll(titulo, grid);

        Scene scene = new Scene(layout);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn");
        return btn;
    }

    private Separator crearSeparador() {
        Separator sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        return sep;
    }

    private void agregarFilaLeyenda(GridPane grid, int fila, String colorHex, String categoria, String descripcion) {
        Rectangle muestra = new Rectangle(16, 16);
        muestra.setFill(Color.web(colorHex));
        muestra.setStroke(Color.GRAY);

        Label lblCategoria = new Label(categoria);
        lblCategoria.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-weight: bold;");

        Label lblDesc = new Label("(" + descripcion + ")");
        lblDesc.setStyle("-fx-text-fill: #AAAAAA;");

        grid.add(muestra, 0, fila);
        grid.add(lblCategoria, 1, fila);
        grid.add(lblDesc, 2, fila);
    }

    public HBox getView() {
        return view;
    }

    public void setEditor(EditorComponent editor) {
        this.editor = editor;
    }

    public void setTraduccion(TraduccionComponent traduccion) {
        this.traduccion = traduccion;
    }

    public void setErrorComponent(ErrorComponent errorComponent) {
        this.errorComponent = errorComponent;
    }

    public void setASTComponent(ASTComponent astComponent) {
        this.astComponent = astComponent;
    }
}