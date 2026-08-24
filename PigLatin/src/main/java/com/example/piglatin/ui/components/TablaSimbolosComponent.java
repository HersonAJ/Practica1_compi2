package com.example.piglatin.ui.components;

import com.example.piglatin.analizador.semantica.TablaSimbolos;
import com.example.piglatin.analizador.semantica.TablaSimbolos.EntradaSimbolo;
import com.example.piglatin.analizador.semantica.TablaSimbolos.Parametro;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class TablaSimbolosComponent {

    private final BorderPane view;
    private final TableView<EntradaSimbolo> tablaSimbolos = new TableView<>();
    private final Label lblConteo = new Label();

    public TablaSimbolosComponent() {
        this.view = new BorderPane();
        this.view.setPadding(new Insets(10));
        this.view.setStyle("-fx-background-color: #1e1e1e;");

        initTable();
        initUI();
    }

    public Node getView() {
        return view;
    }

    public void cargarTabla(TablaSimbolos tabla) {
        if (tabla == null) {
            limpiar();
            return;
        }
        List<EntradaSimbolo> registro = tabla.getRegistroCompleto();
        tablaSimbolos.setItems(FXCollections.observableArrayList(registro));
        lblConteo.setText(registro.size() + " símbolo(s)");
    }

    public void limpiar() {
        tablaSimbolos.getItems().clear();
        lblConteo.setText("0 símbolo(s)");
    }

    private void initUI() {
        Label titulo = new Label("Tabla de Símbolos");
        titulo.setStyle("-fx-text-fill: #d4d4d4; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblConteo.setStyle("-fx-text-fill: #858585;");

        VBox top = new VBox(4, titulo, lblConteo);
        view.setTop(top);
        view.setCenter(tablaSimbolos);
    }

    private void initTable() {
        TableColumn<EntradaSimbolo, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().id())));
        colId.setPrefWidth(50);

        TableColumn<EntradaSimbolo, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombre()));

        TableColumn<EntradaSimbolo, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(formatearCategoria(data.getValue().categoria())));

        TableColumn<EntradaSimbolo, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tipo()));

        TableColumn<EntradaSimbolo, String> colNumParams = new TableColumn<>("# Parámetros");
        colNumParams.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().numParametros())));
        colNumParams.setPrefWidth(90);

        TableColumn<EntradaSimbolo, String> colParams = new TableColumn<>("Parámetros / Campos");
        colParams.setCellValueFactory(data ->
                new SimpleStringProperty(formatearParametros(data.getValue().parametros())));
        colParams.setPrefWidth(220);

        TableColumn<EntradaSimbolo, String> colAmbito = new TableColumn<>("Ámbito");
        colAmbito.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().ambito()));
        colAmbito.setPrefWidth(160);

        TableColumn<EntradaSimbolo, String> colAlcance = new TableColumn<>("Alcance");
        colAlcance.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().alcance())));
        colAlcance.setPrefWidth(70);

        tablaSimbolos.getColumns().addAll(
                colId, colNombre, colCategoria, colTipo, colNumParams, colParams, colAmbito, colAlcance);
        tablaSimbolos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private String formatearCategoria(TablaSimbolos.Categoria categoria) {
        if (categoria == null) return "-";
        return switch (categoria) {
            case VARIABLE -> "Variable";
            case ARREGLO -> "Arreglo";
            case PARAMETRO -> "Parámetro";
            case STRUCT_INSTANCIA -> "Instancia de Struct";
            case STRUCT_DEF -> "Definición de Struct";
            case FUNCION -> "Función";
        };
    }

    private String formatearParametros(List<Parametro> parametros) {
        if (parametros == null || parametros.isEmpty()) {
            return "-";
        }
        return parametros.stream()
                .map(p -> p.nombre() + ": " + p.tipo())
                .collect(Collectors.joining(", "));
    }
}