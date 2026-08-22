package com.example.piglatin.ui.components;

import com.example.piglatin.analizador.semantica.TablaSimbolos;
import com.example.piglatin.analizador.semantica.TablaSimbolos.DefinicionFuncion;
import com.example.piglatin.analizador.semantica.TablaSimbolos.DefinicionStruct;
import com.example.piglatin.analizador.semantica.TablaSimbolos.SimboloVariable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.List;
import java.util.Map;

public class TablaSimbolosComponent {

    private final BorderPane view;
    private final TableView<SimboloVariable> tablaVariables = new TableView<>();
    private final TableView<DefinicionFuncion> tablaFunciones = new TableView<>();
    private final TableView<DefinicionStruct> tablaStructs = new TableView<>();

    public TablaSimbolosComponent() {
        this.view = new BorderPane();
        this.view.setPadding(new Insets(10));
        this.view.setStyle("-fx-background-color: #1e1e2e;");

        initTables();
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

        tablaVariables.setItems(FXCollections.observableArrayList(tabla.getTodasLasVariables()));
        tablaFunciones.setItems(FXCollections.observableArrayList(tabla.getFunciones().values()));
        tablaStructs.setItems(FXCollections.observableArrayList(tabla.getTodosLosStructs()));
    }

    public void limpiar() {
        tablaVariables.getItems().clear();
        tablaFunciones.getItems().clear();
        tablaStructs.getItems().clear();
    }

    private void initUI() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-width: 100px;");

        Tab tabVars = new Tab("Variables (" + tablaVariables.getItems().size() + ")", tablaVariables);
        tabVars.setClosable(false);

        Tab tabFuncs = new Tab("Funciones", tablaFunciones);
        tabFuncs.setClosable(false);

        Tab tabStructs = new Tab("Structs", tablaStructs);
        tabStructs.setClosable(false);

        tabPane.getTabs().addAll(tabVars, tabFuncs, tabStructs);
        view.setCenter(tabPane);
    }

    private void initTables() {
        // --- TABLA VARIABLES ---
        TableColumn<SimboloVariable, String> colVarNombre = new TableColumn<>("Nombre");
        colVarNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombre()));

        TableColumn<SimboloVariable, String> colVarTipo = new TableColumn<>("Tipo");
        colVarTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tipo()));

        TableColumn<SimboloVariable, String> colVarArreglo = new TableColumn<>("Es Arreglo");
        colVarArreglo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().esArreglo() ? "Sí" : "No"));

        TableColumn<SimboloVariable, String> colVarTamano = new TableColumn<>("Tamaño Arreglo");
        colVarTamano.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().tamanoArreglo() != null ? data.getValue().tamanoArreglo().toString() : "-"
        ));

        tablaVariables.getColumns().addAll(colVarNombre, colVarTipo, colVarArreglo, colVarTamano);
        tablaVariables.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- TABLA FUNCIONES ---
        TableColumn<DefinicionFuncion, String> colFuncNombre = new TableColumn<>("Nombre");
        colFuncNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombre()));

        TableColumn<DefinicionFuncion, String> colFuncParams = new TableColumn<>("Parámetros");
        colFuncParams.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().tipoParametros() != null ? String.join(", ", data.getValue().tipoParametros()) : "ninguno"
        ));

        TableColumn<DefinicionFuncion, String> colFuncRetorno = new TableColumn<>("Tipo Retorno");
        colFuncRetorno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().tipoRetorno()));

        tablaFunciones.getColumns().addAll(colFuncNombre, colFuncParams, colFuncRetorno);
        tablaFunciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- TABLA STRUCTS ---
        TableColumn<DefinicionStruct, String> colStructNombre = new TableColumn<>("Nombre Struct");
        colStructNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombre()));

        TableColumn<DefinicionStruct, String> colStructCampos = new TableColumn<>("Campos (Nombre: Tipo)");
        colStructCampos.setCellValueFactory(data -> {
            Map<String, String> campos = data.getValue().campos();
            if (campos == null || campos.isEmpty()) {
                return new SimpleStringProperty("{}");
            }
            StringBuilder sb = new StringBuilder("{ ");
            campos.forEach((k, v) -> sb.append(k).append(": ").append(v).append("; "));
            sb.append("}");
            return new SimpleStringProperty(sb.toString());
        });

        tablaStructs.getColumns().addAll(colStructNombre, colStructCampos);
        tablaStructs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}