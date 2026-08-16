package com.example.piglatin.ui.components;
import com.example.piglatin.analizador.semantica.errores.ErrorSemantico;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class ErrorComponent {

    private BorderPane view;
    private TableView<ErrorItem> table;

    public ErrorComponent() {
        view = new BorderPane();
        view.getStyleClass().add("error-container");

        // Título
        Label title = new Label("Errores de Compilación");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #d4d4d4; -fx-padding: 10 0 10 0;");

        // Tabla de errores
        table = new TableView<>();
        table.getStyleClass().add("error-table");

        // Columna Línea
        TableColumn<ErrorItem, Integer> colLinea = new TableColumn<>("Línea");
        colLinea.setCellValueFactory(new PropertyValueFactory<>("linea"));
        colLinea.setPrefWidth(80);

        // Columna Tipo
        TableColumn<ErrorItem, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setPrefWidth(150);

        // Columna Mensaje
        TableColumn<ErrorItem, String> colMensaje = new TableColumn<>("Mensaje");
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colMensaje.setPrefWidth(600);

        table.getColumns().addAll(colLinea, colTipo, colMensaje);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Contenedor
        VBox content = new VBox();
        content.setPadding(new Insets(16));
        content.setSpacing(10);
        content.getChildren().addAll(title, table);
        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);

        view.setCenter(content);
    }

    public BorderPane getView() {
        return view;
    }

    public void setErrors(List<String> erroresSintacticos, List<ErrorSemantico> erroresSemanticos, List<String> errores) {
        table.getItems().clear();

        // Agregar errores sintacticos
        for (String error : erroresSintacticos) {
            table.getItems().add(new ErrorItem(0, "Sintáctico", error));
        }

        // Agregar errores semanticos
        for (ErrorSemantico error : erroresSemanticos) {
            table.getItems().add(new ErrorItem(error.linea(), "Semántico", error.mensaje()));
        }

        // Agregar errores internos
        for (String error : errores) {
            table.getItems().add(new ErrorItem(0, "Interno", error));
        }
    }

    public void clear() {
        table.getItems().clear();
    }

    // Clase interna para los items de la tabla
    public static class ErrorItem {
        private final int linea;
        private final String tipo;
        private final String mensaje;

        public ErrorItem(int linea, String tipo, String mensaje) {
            this.linea = linea;
            this.tipo = tipo;
            this.mensaje = mensaje;
        }

        public int getLinea() { return linea; }
        public String getTipo() { return tipo; }
        public String getMensaje() { return mensaje; }
    }
}