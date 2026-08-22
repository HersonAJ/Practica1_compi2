package com.example.piglatin.ui.components;

import com.example.piglatin.analizador.pila.PasoPila;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;

public class VisualizadorPilaView extends BorderPane {

    private final List<PasoPila> pasos;
    private int pasoActualIndex = 0;

    // Elementos de la Interfaz
    private final VBox pilaContainer = new VBox(6);
    private final TableView<PasoPila> tablaLog = new TableView<>();
    private final Slider sliderTimeline = new Slider();
    private final Label lblPasoActual = new Label("Paso: 0 / 0");
    private final Label lblOperacionActual = new Label("-");
    private final Button btnPlay = new Button("▶ Play");

    // Motor de Reproduccion
    private Timeline timeline;
    private double velocidadSegundos = 1.0;

    public VisualizadorPilaView(List<PasoPila> pasos) {
        this.pasos = pasos;
        initUI();
        initTimeline();
        if (pasos != null && !pasos.isEmpty()) {
            actualizarEstado(0);
        }
    }

    private void initUI() {
        this.setStyle("-fx-background-color: #1e1e2e; -fx-padding: 15;");

        HBox barraControles = new HBox(12);
        barraControles.setAlignment(Pos.CENTER_LEFT);
        barraControles.setStyle("-fx-padding: 10 15; -fx-background-color: #2b2b3b; -fx-background-radius: 8;");

        Button btnFirst = new Button("⏮");
        Button btnPrev = new Button("◀");
        Button btnNext = new Button("▶");
        Button btnLast = new Button("⏭");

        btnFirst.setOnAction(e -> { pausar(); actualizarEstado(0); });
        btnPrev.setOnAction(e -> { pausar(); actualizarEstado(pasoActualIndex - 1); });
        btnNext.setOnAction(e -> { pausar(); actualizarEstado(pasoActualIndex + 1); });
        btnLast.setOnAction(e -> { pausar(); actualizarEstado(pasos.size() - 1); });
        btnPlay.setOnAction(e -> togglePlay());

        // Slider para salto directo
        sliderTimeline.setMin(1);
        sliderTimeline.setMax(pasos.isEmpty() ? 1 : pasos.size());
        sliderTimeline.setValue(1);
        sliderTimeline.setBlockIncrement(1);
        sliderTimeline.setPrefWidth(220);

        sliderTimeline.valueProperty().addListener((obs, oldVal, newVal) -> {
            int targetStep = newVal.intValue() - 1;
            if (targetStep != pasoActualIndex && targetStep >= 0 && targetStep < pasos.size()) {
                actualizarEstado(targetStep);
            }
        });

        // Control de velocidad
        ComboBox<String> cbVelocidad = new ComboBox<>(FXCollections.observableArrayList("0.5x", "1.0x", "2.0x", "5.0x"));
        cbVelocidad.setValue("1.0x");
        cbVelocidad.setOnAction(e -> {
            switch (cbVelocidad.getValue()) {
                case "0.5x" -> velocidadSegundos = 2.0;
                case "1.0x" -> velocidadSegundos = 1.0;
                case "2.0x" -> velocidadSegundos = 0.5;
                case "5.0x" -> velocidadSegundos = 0.2;
            }
            if (timeline != null) {
                boolean corriendo = timeline.getStatus() == Animation.Status.RUNNING;
                pausar();
                initTimeline();
                if (corriendo) reproducir();
            }
        });

        lblPasoActual.setStyle("-fx-text-fill: #cdd6f4; -fx-font-weight: bold; -fx-font-size: 13px;");

        barraControles.getChildren().addAll(
                btnFirst, btnPrev, btnPlay, btnNext, btnLast,
                lblPasoActual,
                sliderTimeline,
                new Label("Vel:"), cbVelocidad
        );

        VBox panelPila = new VBox(10);
        panelPila.setPrefWidth(240);
        panelPila.setStyle("-fx-padding: 12; -fx-background-color: #181825; -fx-background-radius: 8;");

        Label lblTituloPila = new Label("Pila de Llamadas");
        lblTituloPila.setStyle("-fx-text-fill: #89b4fa; -fx-font-weight: bold; -fx-font-size: 14px;");

        pilaContainer.setAlignment(Pos.BOTTOM_CENTER);

        ScrollPane scrollPila = new ScrollPane(pilaContainer);
        scrollPila.setFitToWidth(true);
        scrollPila.setStyle("-fx-background: #181825; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPila, Priority.ALWAYS);

        lblOperacionActual.setStyle("-fx-text-fill: #f9e2af; -fx-font-weight: bold; -fx-font-size: 12px; -fx-alignment: center;");

        panelPila.getChildren().addAll(
                lblTituloPila,
                scrollPila,
                new Label("Accion Ejecutada:"),
                lblOperacionActual
        );

        VBox panelLog = new VBox(10);
        panelLog.setStyle("-fx-padding: 12; -fx-background-color: #181825; -fx-background-radius: 8;");
        HBox.setHgrow(panelLog, Priority.ALWAYS);

        configurarTablaLog();
        VBox.setVgrow(tablaLog, Priority.ALWAYS);

        panelLog.getChildren().addAll(new Label("Log de Transiciones:"), tablaLog);

        // Layout Split
        SplitPane splitPane = new SplitPane(panelPila, panelLog);
        splitPane.setDividerPositions(0.32);

        this.setTop(barraControles);
        this.setCenter(splitPane);
    }

    private void configurarTablaLog() {
        TableColumn<PasoPila, Integer> colNumero = new TableColumn<>("Nº");
        colNumero.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().numero()).asObject());
        colNumero.setPrefWidth(50);

        TableColumn<PasoPila, String> colTipo = new TableColumn<>("Operacion");
        colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().operacion().name()));
        colTipo.setPrefWidth(100);

        TableColumn<PasoPila, String> colDesc = new TableColumn<>("Detalle / Regla");
        colDesc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().descripcion()));
        colDesc.setPrefWidth(220);

        TableColumn<PasoPila, String> colPila = new TableColumn<>("Estado Pila");
        colPila.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().pila().toString()));
        colPila.setPrefWidth(200);

        tablaLog.getColumns().addAll(colNumero, colTipo, colDesc, colPila);
        tablaLog.setItems(FXCollections.observableArrayList(pasos));

        tablaLog.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0) {
                int index = newVal.intValue();
                if (index != pasoActualIndex) {
                    pausar();
                    actualizarEstado(index);
                }
            }
        });
    }

    private void actualizarEstado(int index) {
        if (index < 0 || index >= pasos.size()) return;

        this.pasoActualIndex = index;
        PasoPila paso = pasos.get(pasoActualIndex);

        lblPasoActual.setText(String.format("Paso: %d / %d", pasoActualIndex + 1, pasos.size()));
        lblOperacionActual.setText(paso.descripcion());
        sliderTimeline.setValue(pasoActualIndex + 1);

        // Seleccion sincronizada en la tabla
        tablaLog.getSelectionModel().select(pasoActualIndex);
        tablaLog.scrollTo(pasoActualIndex);

        // Renderizado visual de la Pila (LIFO)
        pilaContainer.getChildren().clear();
        List<String> snapshot = paso.pila();

        if (snapshot.isEmpty()) {
            Label vacia = new Label("(v) Pila Vacia");
            vacia.setStyle("-fx-text-fill: #6c7086; -fx-font-style: italic;");
            pilaContainer.getChildren().add(vacia);
            return;
        }

        // Se iteran los elementos para colocar el TOP en la cima visual de la VBox
        for (int i = snapshot.size() - 1; i >= 0; i--) {
            String simbolo = snapshot.get(i);
            Label celda = new Label(simbolo);
            celda.setMaxWidth(Double.MAX_VALUE);
            celda.setAlignment(Pos.CENTER);

            boolean isTop = (i == snapshot.size() - 1);
            if (isTop) {
                celda.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 5;");
            } else {
                celda.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-padding: 6; -fx-background-radius: 5;");
            }
            pilaContainer.getChildren().add(celda);
        }
    }

    private void initTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(velocidadSegundos), e -> {
            if (pasoActualIndex < pasos.size() - 1) {
                actualizarEstado(pasoActualIndex + 1);
            } else {
                pausar();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void togglePlay() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            pausar();
        } else {
            reproducir();
        }
    }

    private void reproducir() {
        if (pasoActualIndex >= pasos.size() - 1) {
            actualizarEstado(0);
        }
        timeline.play();
        btnPlay.setText("⏸ Pausa");
    }

    private void pausar() {
        timeline.pause();
        btnPlay.setText("▶ Play");
    }
}