package com.example.piglatin.ui.components;

import com.example.piglatin.analizador.pila.PasoPila;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class PilaComponent {

    private final BorderPane view;
    private List<PasoPila> pasos = new ArrayList<>();
    private int pasoActualIndex = 0;

    private final VBox pilaContainer = new VBox(5);
    private final TableView<PasoPila> tablaLog = new TableView<>();
    private final Slider sliderTimeline = new Slider();
    private final Label lblPasoActual = new Label("Paso: 0 / 0");
    private final Label lblOperacionActual = new Label("-");
    private final Button btnPlay = new Button("▶ Play");

    private Timeline timeline;
    private double velocidadSegundos = 1.0;

    public PilaComponent() {
        this.view = new BorderPane();
        initUI();
        initTimeline();
    }

    public Node getView() {
        return view;
    }

    public void cargarPasos(List<PasoPila> nuevosPasos) {
        pausar();
        this.pasos = (nuevosPasos != null) ? nuevosPasos : new ArrayList<>();
        this.pasoActualIndex = 0;

        sliderTimeline.setMax(pasos.isEmpty() ? 1 : pasos.size());
        sliderTimeline.setValue(1);
        tablaLog.setItems(FXCollections.observableArrayList(pasos));

        if (!pasos.isEmpty()) {
            actualizarEstado(0);
        } else {
            pilaContainer.getChildren().clear();
            lblPasoActual.setText("Paso: 0 / 0");
            lblOperacionActual.setText("-");
        }
    }

    private void initUI() {
        view.setStyle("-fx-background-color: #1e1e2e; -fx-padding: 10;");

        HBox controles = new HBox(10);
        controles.setAlignment(Pos.CENTER_LEFT);
        controles.setStyle("-fx-padding: 8 12; -fx-background-color: #2b2b3b; -fx-background-radius: 6;");

        Button btnFirst = new Button("⏮");
        Button btnPrev = new Button("◀");
        Button btnNext = new Button("▶");
        Button btnLast = new Button("⏭");

        btnFirst.setOnAction(e -> { pausar(); actualizarEstado(0); });
        btnPrev.setOnAction(e -> { pausar(); actualizarEstado(pasoActualIndex - 1); });
        btnNext.setOnAction(e -> { pausar(); actualizarEstado(pasoActualIndex + 1); });
        btnLast.setOnAction(e -> { pausar(); actualizarEstado(pasos.size() - 1); });
        btnPlay.setOnAction(e -> togglePlay());

        sliderTimeline.setMin(1);
        sliderTimeline.setPrefWidth(180);
        sliderTimeline.valueProperty().addListener((obs, oldVal, newVal) -> {
            int targetStep = newVal.intValue() - 1;
            if (targetStep != pasoActualIndex && targetStep >= 0 && targetStep < pasos.size()) {
                actualizarEstado(targetStep);
            }
        });

        ComboBox<String> cbVelocidad = new ComboBox<>(FXCollections.observableArrayList("0.5x", "1.0x", "2.0x"));
        cbVelocidad.setValue("1.0x");
        cbVelocidad.setOnAction(e -> {
            switch (cbVelocidad.getValue()) {
                case "0.5x" -> velocidadSegundos = 2.0;
                case "1.0x" -> velocidadSegundos = 1.0;
                case "2.0x" -> velocidadSegundos = 0.5;
            }
            if (timeline != null) {
                boolean corriendo = timeline.getStatus() == Animation.Status.RUNNING;
                pausar();
                initTimeline();
                if (corriendo) reproducir();
            }
        });

        lblPasoActual.setStyle("-fx-text-fill: #cdd6f4; -fx-font-weight: bold;");
        controles.getChildren().addAll(btnFirst, btnPrev, btnPlay, btnNext, btnLast, lblPasoActual, sliderTimeline, cbVelocidad);

        VBox panelPila = new VBox(8);
        panelPila.setPrefWidth(200);
        panelPila.setStyle("-fx-padding: 10; -fx-background-color: #181825; -fx-background-radius: 6;");

        pilaContainer.setAlignment(Pos.BOTTOM_CENTER);
        ScrollPane scrollPila = new ScrollPane(pilaContainer);
        scrollPila.setFitToWidth(true);
        scrollPila.setStyle("-fx-background: #181825; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPila, Priority.ALWAYS);

        lblOperacionActual.setStyle("-fx-text-fill: #f9e2af; -fx-font-weight: bold;");
        panelPila.getChildren().addAll(new Label("Pila de Llamadas:"), scrollPila, lblOperacionActual);

        VBox panelLog = new VBox(8);
        panelLog.setStyle("-fx-padding: 10; -fx-background-color: #181825; -fx-background-radius: 6;");
        configurarTablaLog();
        VBox.setVgrow(tablaLog, Priority.ALWAYS);
        panelLog.getChildren().addAll(new Label("Log de Transiciones:"), tablaLog);

        SplitPane splitPane = new SplitPane(panelPila, panelLog);
        splitPane.setDividerPositions(0.35);

        view.setTop(controles);
        view.setCenter(splitPane);
    }

    private void configurarTablaLog() {
        TableColumn<PasoPila, Integer> colNumero = new TableColumn<>("Nº");
        colNumero.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().numero()).asObject());

        TableColumn<PasoPila, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().operacion().name()));

        TableColumn<PasoPila, String> colDesc = new TableColumn<>("Accion");
        colDesc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().descripcion()));

        tablaLog.getColumns().addAll(colNumero, colTipo, colDesc);

        tablaLog.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0 && newVal.intValue() < pasos.size()) {
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

        tablaLog.getSelectionModel().select(pasoActualIndex);
        tablaLog.scrollTo(pasoActualIndex);

        pilaContainer.getChildren().clear();
        List<String> snapshot = paso.pila();

        for (int i = snapshot.size() - 1; i >= 0; i--) {
            String simbolo = snapshot.get(i);
            Label celda = new Label(simbolo);
            celda.setMaxWidth(Double.MAX_VALUE);
            celda.setAlignment(Pos.CENTER);

            boolean isTop = (i == snapshot.size() - 1);
            celda.setStyle(isTop
                    ? "-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-padding: 6; -fx-background-radius: 4;"
                    : "-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-padding: 5; -fx-background-radius: 4;");
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
        if (timeline.getStatus() == Animation.Status.RUNNING) pausar();
        else reproducir();
    }

    private void reproducir() {
        if (pasoActualIndex >= pasos.size() - 1) actualizarEstado(0);
        timeline.play();
        btnPlay.setText("⏸ Pausa");
    }

    private void pausar() {
        if (timeline != null) timeline.pause();
        btnPlay.setText("▶ Play");
    }
}