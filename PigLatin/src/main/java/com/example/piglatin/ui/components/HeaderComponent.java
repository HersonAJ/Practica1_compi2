package com.example.piglatin.ui.components;

import com.example.piglatin.analizador.ast.NodoPrograma;
import com.example.piglatin.service.CompileService;
import com.example.piglatin.service.ResultadoCompilacion;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private PilaComponent pilaComponent;
    private TablaSimbolosComponent tablaSimbolosComponent;
    private Stage stageErrores;
    private Stage stageTabla;
    private Stage stagePila;
    private Stage stageAST;
    private ResultadoCompilacion ultimoResultado;

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
            ultimoResultado = null;
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
                ultimoResultado = null;
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
                        List.of(), List.of(), List.of(),
                        List.of("El código está vacío")
                );
            }
            editor.cambiarAModoEdicion();
            return;
        }

        btnAnalizar.setDisable(true);

        Task<ResultadoCompilacion> task = new Task<>() {
            @Override
            protected ResultadoCompilacion call() {
                CompileService service = new CompileService();
                // Ejecuta el análisis
                return service.analizar(codigo);
            }
        };

        task.setOnSucceeded(ev -> {
            btnAnalizar.setDisable(false);
            ResultadoCompilacion resultado = task.getValue();
            aplicarResultadoAnálisis(resultado);
        });

        task.setOnFailed(ev -> {
            btnAnalizar.setDisable(false);
            Throwable ex = task.getException();

            if (errorComponent != null) {
                errorComponent.setErrors(
                        List.of(), List.of(), List.of(),
                        List.of("Error interno inesperado: " + (ex != null ? ex.getMessage() : "desconocido"))
                );
            }
            editor.cambiarAModoEdicion();
        });

        Thread hilo = new Thread(task, "analisis-latinus");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void aplicarResultadoAnálisis(ResultadoCompilacion resultado) {
        this.ultimoResultado = resultado;

        if (errorComponent != null) {
            errorComponent.setErrors(
                    resultado.erroresLexicos(),
                    resultado.erroresSintacticos(),
                    resultado.erroresSemanticos(),
                    resultado.errores()
            );
        }

        if (resultado.coloreado() != null && !resultado.coloreado().isEmpty()) {
            editor.aplicarColores(resultado.coloreado());
        } else {
            editor.cambiarAModoEdicion();
        }

        if (resultado.exito()) {
            if (traduccion != null) {
                traduccion.setTranslation("// Análisis exitoso. Presiona 'Traducir' para generar PigLatin.");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Análisis Completado");
            alert.setHeaderText(null);
            alert.setContentText("¡El análisis sintáctico y semántico finalizó con éxito!");
            alert.showAndWait();

        } else {
            if (traduccion != null) {
                traduccion.setTranslation("// Compilación fallida - Corrija los errores antes de traducir.");
            }

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Análisis");
            alert.setHeaderText("Se encontraron errores durante el análisis");
            alert.setContentText("Revisa la consola de errores o presiona el botón '❌ Errores'.");
            alert.showAndWait();
        }
    }

    private void aplicarResultado(ResultadoCompilacion resultado) {
        this.ultimoResultado = resultado;

        if (errorComponent != null) {
            errorComponent.setErrors(
                    resultado.erroresLexicos(),
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

        if (resultado.exito()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Análisis Completado");
            alert.setHeaderText(null);
            alert.setContentText("¡El análisis y la traducción se completaron con éxito!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Compilación");
            alert.setHeaderText("Se encontraron errores durante el análisis");
            alert.setContentText("Revisa la sección de errores o presiona el botón '❌ Errores' para ver los detalles.");
            alert.showAndWait();
        }
    }

    private void onEditar() {
        if (editor != null) {
            editor.cambiarAModoEdicion();
        }
    }

    private void onTraducir() {
        if (ultimoResultado == null || !ultimoResultado.exito() || ultimoResultado.ast() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Traducción no permitida");
            alert.setHeaderText("No se puede traducir");
            alert.setContentText("Debes analizar el código correctamente y corregir todos los errores antes de traducir.");
            alert.showAndWait();
            return;
        }

        CompileService service = new CompileService();
        String codigoTraducido = service.traducir(ultimoResultado.ast());

        if (traduccion != null) {
            traduccion.setTranslation(codigoTraducido);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Traducción Generada");
            alert.setHeaderText(null);
            alert.setContentText("¡La traducción a PigLatin se ha generado correctamente!");
            alert.showAndWait();
        }
    }

    private void onAST() {
        if (astComponent == null) return;

        if (ultimoResultado == null || ultimoResultado.ast() == null) {
            avisarFaltaAnalisis("Analiza el código primero para poder ver el AST.");
            return;
        }

        astComponent.mostrarAST(ultimoResultado.ast());
    }

    private void onTabla() {
        if (ultimoResultado == null || ultimoResultado.tablaSimbolos() == null) {
            avisarFaltaAnalisis("Analiza el código primero para poder ver la tabla de símbolos.");
            return;
        }

        if (tablaSimbolosComponent == null) {
            tablaSimbolosComponent = new TablaSimbolosComponent();
        }
        tablaSimbolosComponent.cargarTabla(ultimoResultado.tablaSimbolos());

        if (stageTabla == null) {
            stageTabla = new Stage();
            stageTabla.setTitle("Tabla de Símbolos");
            stageTabla.setScene(new Scene((javafx.scene.Parent) tablaSimbolosComponent.getView()));
            stageTabla.setMinWidth(650);
            stageTabla.setMinHeight(350);
            stageTabla.setWidth(850);
            stageTabla.setHeight(500);
        }

        if (!stageTabla.isShowing()) {
            stageTabla.centerOnScreen();
        }

        stageTabla.show();
        stageTabla.toFront();
    }

    private void onPila() {
        if (ultimoResultado == null || ultimoResultado.pasosPila() == null) {
            avisarFaltaAnalisis("Analiza el código primero para poder ver la pila.");
            return;
        }

        if (pilaComponent == null) {
            pilaComponent = new PilaComponent();
        }
        pilaComponent.cargarPasos(ultimoResultado.pasosPila());

        if (stagePila == null) {
            stagePila = new Stage();
            stagePila.setTitle("Pila de Llamadas y Transiciones");
            stagePila.setScene(new Scene((javafx.scene.Parent) pilaComponent.getView()));
            stagePila.setMinWidth(750);
            stagePila.setMinHeight(400);
            stagePila.setWidth(950);
            stagePila.setHeight(600);
        }

        if (!stagePila.isShowing()) {
            stagePila.centerOnScreen();
        }

        stagePila.show();
        stagePila.toFront();
    }

    private void onErrores() {
        if (errorComponent == null) return;

        if (stageErrores == null) {
            stageErrores = new Stage();
            stageErrores.setTitle("Errores de Compilación");

            Scene scene = new Scene(errorComponent.getView(), 900, 500);
            stageErrores.setScene(scene);
            stageErrores.setMinWidth(800);
            stageErrores.setMinHeight(400);
            stageErrores.setWidth(950);
            stageErrores.setHeight(550);
        }

        if (!stageErrores.isShowing()) {
            stageErrores.centerOnScreen();
        }

        stageErrores.show();
        stageErrores.toFront();
    }

    private void avisarFaltaAnalisis(String mensaje) {
        if (errorComponent != null) {
            errorComponent.setErrors(List.of(), List.of(), List.of(), List.of(mensaje));
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

    public void setPilaComponent(PilaComponent pilaComponent) {
        this.pilaComponent = pilaComponent;
    }

    public void setTablaSimbolosComponent(TablaSimbolosComponent tablaSimbolosComponent) {
        this.tablaSimbolosComponent = tablaSimbolosComponent;
    }
}