package com.example.piglatin.ui.components;

import com.example.piglatin.analizador.errores.ErrorPosicional;
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

        Label title = new Label("Errores de Compilación");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #d4d4d4; -fx-padding: 10 0 10 0;");

        table = new TableView<>();
        table.getStyleClass().add("error-table");

        TableColumn<ErrorItem, Integer> colLinea = new TableColumn<>("Línea");
        colLinea.setCellValueFactory(new PropertyValueFactory<>("linea"));
        colLinea.setPrefWidth(80);

        TableColumn<ErrorItem, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setPrefWidth(150);

        TableColumn<ErrorItem, String> colMensaje = new TableColumn<>("Mensaje");
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colMensaje.setPrefWidth(600);

        table.getColumns().addAll(colLinea, colTipo, colMensaje);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

    public void setErrors(List<ErrorPosicional> erroresLexicos,
                          List<ErrorPosicional> erroresSintacticos,
                          List<ErrorSemantico> erroresSemanticos,
                          List<String> errores) {
        table.getItems().clear();

        for (ErrorPosicional error : erroresLexicos) {
            table.getItems().add(new ErrorItem(error.linea(), "Léxico", error.mensaje()));
        }
        for (ErrorPosicional error : erroresSintacticos) {
            table.getItems().add(new ErrorItem(error.linea(), "Sintáctico", error.mensaje()));
        }
        for (ErrorSemantico error : erroresSemanticos) {
            table.getItems().add(new ErrorItem(error.linea(), "Semántico", error.mensaje()));
        }
        for (String error : errores) {
            table.getItems().add(new ErrorItem(0, "Interno", error));
        }
    }

    public void clear() {
        table.getItems().clear();
    }

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