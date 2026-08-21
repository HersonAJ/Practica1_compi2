package com.example.piglatin.ui.components;

import com.example.piglatin.color.ColorMapa;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public class EditorComponent {

    private BorderPane view;
    private TextArea textArea;
    private TextFlow codeFlow;
    private ScrollPane scrollPane;
    private TextArea lineNumbers;
    private HBox editorContainer;
    private boolean modoColoreado = false;

    public EditorComponent() {
        view = new BorderPane();
        view.getStyleClass().add("editor-container");

        // TextArea para edición (SIEMPRE tiene el texto original)
        textArea = new TextArea();
        textArea.setPromptText("// Escribe tu codigo Latinus aqui");
        textArea.setWrapText(false);
        textArea.getStyleClass().add("code-input");

        // TextFlow para código coloreado (SOLO VISUALIZACIÓN)
        codeFlow = new TextFlow();
        codeFlow.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-whitespace: pre;");
        codeFlow.setPadding(new Insets(10, 10, 10, 10));

        scrollPane = new ScrollPane(codeFlow);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("code-scroll");

        // Números de línea
        lineNumbers = new TextArea();
        lineNumbers.setEditable(false);
        lineNumbers.setFocusTraversable(false);
        lineNumbers.setMouseTransparent(true);
        lineNumbers.setWrapText(false);
        lineNumbers.getStyleClass().add("line-numbers");

        // Sincronizar scroll entre textArea y lineNumbers
        textArea.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            lineNumbers.setScrollTop(newVal.doubleValue());
        });

        // Sincronizar scroll del TextFlow con lineNumbers
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            if (modoColoreado) {
                lineNumbers.setScrollTop(newVal.doubleValue() * 1000);
            }
        });

        // Actualizar números de línea cuando cambia el texto
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

    public void aplicarColores(List<ColorMapa.TextoColoreado> fragmentos) {
        if (fragmentos == null || fragmentos.isEmpty()) {
            cambiarAModoEdicion();
            return;
        }

        cambiarAModoColoreado();

        codeFlow.setOnMouseClicked(e -> {
            // Doble click para editar
            if (e.getClickCount() == 2) {
                cambiarAModoEdicion();
            }
        });

        codeFlow.getChildren().clear();

        for (ColorMapa.TextoColoreado fragmento : fragmentos) {
            String texto = fragmento.texto();
            String colorHex = fragmento.color();

            // Manejar saltos de línea
            if (texto.equals("\n")) {
                Text textNode = new Text("\n");
                codeFlow.getChildren().add(textNode);
                continue;
            }

            // Para espacios, preservarlos con whitespace
            Text textNode = new Text(texto);
            // Preservar espacios en blanco
            textNode.setStyle("-fx-whitespace: pre;");

            if (colorHex != null && !colorHex.isEmpty()) {
                try {
                    Color color = Color.web(colorHex);
                    textNode.setFill(color);
                } catch (Exception e) {
                    textNode.setFill(Color.web("#000000"));
                }
            } else {
                textNode.setFill(Color.web("#000000"));
            }

            codeFlow.getChildren().add(textNode);
        }

        updateLineNumbers();
    }

    public void cambiarAModoEdicion() {
        if (!modoColoreado) return;

        modoColoreado = false;
        editorContainer.getChildren().clear();
        editorContainer.getChildren().addAll(lineNumbers, textArea);
        HBox.setHgrow(textArea, Priority.ALWAYS);
        updateLineNumbers();
        adjustLineNumberWidth();
    }

    private void cambiarAModoColoreado() {
        if (modoColoreado) return;

        modoColoreado = true;
        editorContainer.getChildren().clear();
        editorContainer.getChildren().addAll(lineNumbers, scrollPane);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
    }

    public String getCode() {
        return textArea.getText();
    }

    public void setCode(String code) {
        textArea.setText(code);
        codeFlow.getChildren().clear();
        cambiarAModoEdicion();
    }

    public void clear() {
        textArea.clear();
        codeFlow.getChildren().clear();
        cambiarAModoEdicion();
    }

    public BorderPane getView() {
        return view;
    }

    private void updateLineNumbers() {
        String text = textArea.getText();
        int lines = text.split("\n", -1).length;
        generarLineNumbers(lines);
    }

    private void generarLineNumbers(int lines) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append(i).append("\n");
        }
        lineNumbers.setText(sb.toString());
    }

    private void adjustLineNumberWidth() {
        String text = textArea.getText();
        int lines = text.split("\n", -1).length;
        adjustLineNumberWidth(lines);
    }

    private void adjustLineNumberWidth(int lines) {
        int digits = String.valueOf(lines).length();
        int width = Math.max(40, digits * 10 + 28);
        lineNumbers.setPrefWidth(width);
        lineNumbers.setMinWidth(width);
        lineNumbers.setMaxWidth(width);
    }
}