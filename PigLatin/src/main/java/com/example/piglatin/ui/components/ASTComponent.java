package com.example.piglatin.ui.components;

import com.example.piglatin.analizador.ast.*;
import guru.nidi.graphviz.engine.*;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ASTComponent {

    private Stage stage;
    private ImageView imageView;
    private ScrollPane scrollPane;
    private AtomicInteger nodeCounter;
    private Label zoomLabel;

    public ASTComponent() {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Group imageGroup = new Group(imageView);

        scrollPane = new ScrollPane(imageGroup);
        scrollPane.setPannable(true);

        // Zoom responsivo con Ctrl + Rueda del mouse
        scrollPane.setOnScroll(e -> {
            if (e.isControlDown()) {
                e.consume();
                double delta = e.getDeltaY() > 0 ? 1.15 : 0.85;
                aplicarZoom(delta);
            }
        });
    }

    private void aplicarZoom(double factor) {
        double newScaleX = imageView.getScaleX() * factor;
        double newScaleY = imageView.getScaleY() * factor;

        // Rango de zoom permitido: 20% a 2000%
        if (newScaleX >= 0.2 && newScaleX <= 20.0) {
            imageView.setScaleX(newScaleX);
            imageView.setScaleY(newScaleY);
            if (zoomLabel != null) {
                zoomLabel.setText(String.format("%.0f%%", newScaleX * 100));
            }
        }
    }

    private void resetearZoom() {
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);
        if (zoomLabel != null) {
            zoomLabel.setText("100%");
        }
    }

    private HBox crearBarraHerramientas() {
        Button btnZoomIn = new Button("+");
        btnZoomIn.setStyle("-fx-font-weight: bold; -fx-min-width: 30px;");
        btnZoomIn.setOnAction(e -> aplicarZoom(1.2));

        Button btnZoomOut = new Button("-");
        btnZoomOut.setStyle("-fx-font-weight: bold; -fx-min-width: 30px;");
        btnZoomOut.setOnAction(e -> aplicarZoom(0.8));

        Button btnReset = new Button("100%");
        btnReset.setOnAction(e -> resetearZoom());

        zoomLabel = new Label("100%");
        zoomLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 5 0 5;");

        HBox toolbar = new HBox(10, btnZoomOut, zoomLabel, btnZoomIn, btnReset);
        toolbar.setAlignment(Pos.CENTER);
        toolbar.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 8px; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
        return toolbar;
    }

    public void mostrarAST(NodoPrograma programa) {
        if (programa == null) return;
        if (scrollPane.getScene() != null) {
            scrollPane.getScene().setCursor(javafx.scene.Cursor.WAIT);
        }

        javafx.concurrent.Task<Image> task = new javafx.concurrent.Task<>() {
            @Override
            protected Image call() throws Exception {
                String dot = generarDOT(programa);
                byte[] imageBytes = generarImagenBytes(dot);

                if (imageBytes == null || imageBytes.length == 0) {
                    return null;
                }
                return new Image(new ByteArrayInputStream(imageBytes));
            }
        };

        task.setOnSucceeded(e -> {
            if (scrollPane.getScene() != null) {
                scrollPane.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
            }

            Image fxImage = task.getValue();
            if (fxImage == null) return;

            resetearZoom();
            imageView.setImage(fxImage);

            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Árbol Sintáctico Abstracto (AST)");
                stage.initModality(Modality.WINDOW_MODAL);

                BorderPane layout = new BorderPane();
                layout.setTop(crearBarraHerramientas());
                layout.setCenter(scrollPane);

                Scene scene = new Scene(layout);
                stage.setScene(scene);

                // Dimensiones explícitas
                stage.setMinWidth(800);
                stage.setMinHeight(500);
                stage.setWidth(1100);
                stage.setHeight(700);

                stage.setOnCloseRequest(ev -> stage.hide());
            }

            if (!stage.isShowing()) {
                stage.centerOnScreen();
            }

            stage.show();
            stage.toFront();
        });

        task.setOnFailed(e -> {
            if (scrollPane.getScene() != null) {
                scrollPane.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
            }
            if (task.getException() != null) {
                task.getException().printStackTrace();
            }
        });

        new Thread(task).start();
    }

    private String generarDOT(NodoPrograma programa) {
        nodeCounter = new AtomicInteger(0);
        StringBuilder dot = new StringBuilder();
        dot.append("digraph AST {\n");
        dot.append("    rankdir=TB;\n");
        dot.append("    node [shape=ellipse, fontname=\"sans-serif\"];\n\n");

        String rootId = crearNodo(dot, "Programa", "#4A90D9");

        if (programa.variablesGlobales() != null && !programa.variablesGlobales().isEmpty()) {
            String globalesId = crearNodo(dot, "Variables Globales", "#50C878");
            dot.append("    ").append(rootId).append(" -> ").append(globalesId).append(";\n");
            for (NodoSentencia sentencia : programa.variablesGlobales()) {
                String sentenciaId = convertirSentenciaADOT(dot, sentencia);
                dot.append("    ").append(globalesId).append(" -> ").append(sentenciaId).append(";\n");
            }
        }

        if (programa.funciones() != null && !programa.funciones().isEmpty()) {
            String funcionesId = crearNodo(dot, "Funciones", "#F5A623");
            dot.append("    ").append(rootId).append(" -> ").append(funcionesId).append(";\n");
            for (NodoFuncion funcion : programa.funciones()) {
                String funcionId = convertirFuncionADOT(dot, funcion);
                dot.append("    ").append(funcionesId).append(" -> ").append(funcionId).append(";\n");
            }
        }

        if (programa.main() != null && !programa.main().isEmpty()) {
            String mainId = crearNodo(dot, "Main", "#7B68EE");
            dot.append("    ").append(rootId).append(" -> ").append(mainId).append(";\n");
            for (NodoSentencia sentencia : programa.main()) {
                String sentenciaId = convertirSentenciaADOT(dot, sentencia);
                dot.append("    ").append(mainId).append(" -> ").append(sentenciaId).append(";\n");
            }
        }

        dot.append("}\n");
        return dot.toString();
    }

    private String crearNodo(StringBuilder dot, String label, String color) {
        int id = nodeCounter.incrementAndGet();
        String labelEscapado = escaparLabel(label);
        dot.append("    n").append(id)
                .append(" [label=\"").append(labelEscapado)
                .append("\", fillcolor=\"").append(color)
                .append("\", style=filled, fontcolor=black];\n");
        return "n" + id;
    }

    private String escaparLabel(String label) {
        return label
                .replace("\"", "\\\"")
                .replace("\r", "");
    }

    private String convertirSentenciaADOT(StringBuilder dot, NodoSentencia sentencia) {
        return switch (sentencia) {
            case NodoSentencia.DeclaracionVariable d -> {
                String label = "Declarar\n" + d.nombre() + " : " + d.tipo();
                if (d.valorInicial() != null) {
                    label += "\n= " + exprToString(d.valorInicial());
                }
                yield crearNodo(dot, label, "#4EC9B0");
            }
            case NodoSentencia.DeclaracionArreglo d -> {
                String label = "Arreglo\n" + d.nombre() + "[" + d.tamano() + "] : " + d.tipo();
                yield crearNodo(dot, label, "#4EC9B0");
            }
            case NodoSentencia.DefinicionStruct d -> {
                String id = crearNodo(dot, "Struct: " + d.nombre(), "#F5A623");
                for (NodoSentencia.CampoStruct campo : d.campos()) {
                    String campoId = crearNodo(dot, campo.nombre() + " : " + campo.tipo(), "#F5A623");
                    dot.append("    ").append(id).append(" -> ").append(campoId).append(";\n");
                }
                yield id;
            }
            case NodoSentencia.InstanciaStruct d -> {
                String label = "Instancia\n" + d.nombre() + " : " + d.tipoStruct();
                yield crearNodo(dot, label, "#F5A623");
            }
            case NodoSentencia.Asignacion d -> {
                String id = crearNodo(dot, "Asignar", "#D16969");
                String refId = convertirExprADOT(dot, d.referencia());
                String valId = convertirExprADOT(dot, d.valor());
                dot.append("    ").append(id).append(" -> ").append(refId).append(";\n");
                dot.append("    ").append(id).append(" -> ").append(valId).append(";\n");
                yield id;
            }
            case NodoSentencia.AsignacionStructLiteral d -> {
                String id = crearNodo(dot, "Asignar Struct", "#D16969");
                String refId = convertirExprADOT(dot, d.referencia());
                dot.append("    ").append(id).append(" -> ").append(refId).append(";\n");
                yield id;
            }
            case NodoSentencia.Condicional d -> {
                String id = crearNodo(dot, "Condicional", "#FFD700");
                for (NodoSentencia.Rama rama : d.ramas()) {
                    String ramaId = crearNodo(dot, "Si", "#FFD700");
                    String condId = convertirExprADOT(dot, rama.condicion());
                    dot.append("    ").append(id).append(" -> ").append(ramaId).append(";\n");
                    dot.append("    ").append(ramaId).append(" -> ").append(condId).append(";\n");
                    for (NodoSentencia s : rama.cuerpo()) {
                        String sId = convertirSentenciaADOT(dot, s);
                        dot.append("    ").append(ramaId).append(" -> ").append(sId).append(";\n");
                    }
                }
                if (d.elseCuerpo() != null && !d.elseCuerpo().isEmpty()) {
                    String elseId = crearNodo(dot, "Sino", "#FFD700");
                    dot.append("    ").append(id).append(" -> ").append(elseId).append(";\n");
                    for (NodoSentencia s : d.elseCuerpo()) {
                        String sId = convertirSentenciaADOT(dot, s);
                        dot.append("    ").append(elseId).append(" -> ").append(sId).append(";\n");
                    }
                }
                yield id;
            }
            case NodoSentencia.CicloDum d -> {
                String id = crearNodo(dot, "Dum", "#FF6B6B");
                String condId = convertirExprADOT(dot, d.condicion());
                dot.append("    ").append(id).append(" -> ").append(condId).append(";\n");
                for (NodoSentencia s : d.cuerpo()) {
                    String sId = convertirSentenciaADOT(dot, s);
                    dot.append("    ").append(id).append(" -> ").append(sId).append(";\n");
                }
                yield id;
            }
            case NodoSentencia.CicloFacere d -> {
                String id = crearNodo(dot, "Facere", "#FF6B6B");
                String condId = convertirExprADOT(dot, d.condicion());
                dot.append("    ").append(id).append(" -> ").append(condId).append(";\n");
                for (NodoSentencia s : d.cuerpo()) {
                    String sId = convertirSentenciaADOT(dot, s);
                    dot.append("    ").append(id).append(" -> ").append(sId).append(";\n");
                }
                yield id;
            }
            case NodoSentencia.CicloPer d -> {
                String id = crearNodo(dot, "Per", "#FF6B6B");
                String initId = convertirSentenciaADOT(dot, d.inicializacion());
                String condId = convertirExprADOT(dot, d.condicion());
                dot.append("    ").append(id).append(" -> ").append(initId).append(";\n");
                dot.append("    ").append(id).append(" -> ").append(condId).append(";\n");
                if (d.incremento() != null) {
                    String incId = convertirSentenciaADOT(dot, d.incremento());
                    dot.append("    ").append(id).append(" -> ").append(incId).append(";\n");
                }
                for (NodoSentencia s : d.cuerpo()) {
                    String sId = convertirSentenciaADOT(dot, s);
                    dot.append("    ").append(id).append(" -> ").append(sId).append(";\n");
                }
                yield id;
            }
            case NodoSentencia.Retorno d -> {
                String id = crearNodo(dot, "Retornar", "#FF6B6B");
                String valId = convertirExprADOT(dot, d.valor());
                dot.append("    ").append(id).append(" -> ").append(valId).append(";\n");
                yield id;
            }
            case NodoSentencia.Lectura d -> {
                String label = "Leer" + (d.variable() != null ? "\n" + d.variable() : "");
                yield crearNodo(dot, label, "#45B7D1");
            }
            case NodoSentencia.Escritura d -> {
                String id = crearNodo(dot, "Escribir", "#45B7D1");
                for (NodoExpr expr : d.valores()) {
                    String exprId = convertirExprADOT(dot, expr);
                    dot.append("    ").append(id).append(" -> ").append(exprId).append(";\n");
                }
                yield id;
            }
            case NodoSentencia.InterrupcionCiclo d -> {
                yield crearNodo(dot, d.tipo(), "#FF6B6B");
            }
            case NodoSentencia.LlamadaFuncionSentencia d -> {
                String label = "Llamada\n" + d.llamada().nombre();
                yield crearNodo(dot, label, "#4EC9B0");
            }
            default -> crearNodo(dot, "Desconocido", "#D4D4D4");
        };
    }

    private String convertirFuncionADOT(StringBuilder dot, NodoFuncion funcion) {
        String label = "Funcion\n" + funcion.nombre();
        if (funcion.tipoRetorno() != null) {
            label += "\n-> " + funcion.tipoRetorno();
        }
        String id = crearNodo(dot, label, "#F5A623");

        if (funcion.parametros() != null && !funcion.parametros().isEmpty()) {
            String paramsId = crearNodo(dot, "Parametros", "#F5A623");
            dot.append("    ").append(id).append(" -> ").append(paramsId).append(";\n");
            for (NodoFuncion.Parametro p : funcion.parametros()) {
                String pId = crearNodo(dot, p.nombre() + " : " + p.tipo(), "#F5A623");
                dot.append("    ").append(paramsId).append(" -> ").append(pId).append(";\n");
            }
        }

        if (funcion.variablesLocales() != null && !funcion.variablesLocales().isEmpty()) {
            String varsId = crearNodo(dot, "Variables Locales", "#50C878");
            dot.append("    ").append(id).append(" -> ").append(varsId).append(";\n");
            for (NodoSentencia s : funcion.variablesLocales()) {
                String sId = convertirSentenciaADOT(dot, s);
                dot.append("    ").append(varsId).append(" -> ").append(sId).append(";\n");
            }
        }

        if (funcion.cuerpo() != null && !funcion.cuerpo().isEmpty()) {
            String cuerpoId = crearNodo(dot, "Cuerpo", "#50C878");
            dot.append("    ").append(id).append(" -> ").append(cuerpoId).append(";\n");
            for (NodoSentencia s : funcion.cuerpo()) {
                String sId = convertirSentenciaADOT(dot, s);
                dot.append("    ").append(cuerpoId).append(" -> ").append(sId).append(";\n");
            }
        }

        return id;
    }

    private String convertirExprADOT(StringBuilder dot, NodoExpr expr) {
        return switch (expr) {
            case NodoExpr.LiteralEntero l -> crearNodo(dot, String.valueOf(l.valor()), "#CE9178");
            case NodoExpr.LiteralDecimal l -> crearNodo(dot, String.valueOf(l.valor()), "#CE9178");
            case NodoExpr.LiteralTexto l -> {
                String label = "'" + l.valor() + "'";
                yield crearNodo(dot, label, "#CE9178");
            }
            case NodoExpr.LiteralCaracter l -> {
                String label = "'" + l.valor() + "'";
                yield crearNodo(dot, label, "#CE9178");
            }
            case NodoExpr.LiteralBooleano l -> crearNodo(dot, String.valueOf(l.valor()), "#CE9178");
            case NodoExpr.Identificador i -> crearNodo(dot, i.nombre(), "#D4D4D4");
            case NodoExpr.Binaria b -> {
                String id = crearNodo(dot, b.operador(), "#D16969");
                String izqId = convertirExprADOT(dot, b.izquierda());
                String derId = convertirExprADOT(dot, b.derecha());
                dot.append("    ").append(id).append(" -> ").append(izqId).append(";\n");
                dot.append("    ").append(id).append(" -> ").append(derId).append(";\n");
                yield id;
            }
            case NodoExpr.Unaria u -> {
                String op = u.prefijo() ? u.operador() + " (prefijo)" : u.operador() + " (postfijo)";
                String id = crearNodo(dot, op, "#D16969");
                String opId = convertirExprADOT(dot, u.operando());
                dot.append("    ").append(id).append(" -> ").append(opId).append(";\n");
                yield id;
            }
            case NodoExpr.LlamadaFuncion l -> {
                String id = crearNodo(dot, "Llamada: " + l.nombre(), "#4EC9B0");
                for (NodoExpr arg : l.argumentos()) {
                    String argId = convertirExprADOT(dot, arg);
                    dot.append("    ").append(id).append(" -> ").append(argId).append(";\n");
                }
                yield id;
            }
            case NodoExpr.AccesoArray a -> {
                String id = crearNodo(dot, "Acceso Array", "#D16969");
                String arrId = convertirExprADOT(dot, a.arreglo());
                String idxId = convertirExprADOT(dot, a.indice());
                dot.append("    ").append(id).append(" -> ").append(arrId).append(";\n");
                dot.append("    ").append(id).append(" -> ").append(idxId).append(";\n");
                yield id;
            }
            case NodoExpr.AccesoAtributo a -> {
                String id = crearNodo(dot, "Acceso: " + a.atributo(), "#D16969");
                String objId = convertirExprADOT(dot, a.objeto());
                dot.append("    ").append(id).append(" -> ").append(objId).append(";\n");
                yield id;
            }
            case NodoExpr.LiteralStruct l -> {
                String id = crearNodo(dot, "Literal Struct", "#F5A623");
                for (Map.Entry<String, NodoExpr> entry : l.campos().entrySet()) {
                    String campoId = crearNodo(dot, entry.getKey() + ":", "#F5A623");
                    String valId = convertirExprADOT(dot, entry.getValue());
                    dot.append("    ").append(id).append(" -> ").append(campoId).append(";\n");
                    dot.append("    ").append(campoId).append(" -> ").append(valId).append(";\n");
                }
                yield id;
            }
            default -> crearNodo(dot, "Expr", "#D4D4D4");
        };
    }

    private String exprToString(NodoExpr expr) {
        return switch (expr) {
            case NodoExpr.LiteralEntero l -> String.valueOf(l.valor());
            case NodoExpr.LiteralDecimal l -> String.valueOf(l.valor());
            case NodoExpr.LiteralTexto l -> "\"" + l.valor() + "\"";
            case NodoExpr.LiteralCaracter l -> "'" + l.valor() + "'";
            case NodoExpr.LiteralBooleano l -> String.valueOf(l.valor());
            case NodoExpr.Identificador i -> i.nombre();
            case NodoExpr.Binaria b -> "(" + exprToString(b.izquierda()) + " " + b.operador() + " " + exprToString(b.derecha()) + ")";
            case NodoExpr.Unaria u -> (u.prefijo() ? u.operador() : "") + exprToString(u.operando()) + (!u.prefijo() ? u.operador() : "");
            case NodoExpr.LlamadaFuncion l -> {
                StringBuilder sb = new StringBuilder(l.nombre() + "(");
                for (int i = 0; i < l.argumentos().size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(exprToString(l.argumentos().get(i)));
                }
                sb.append(")");
                yield sb.toString();
            }
            case NodoExpr.AccesoArray a -> exprToString(a.arreglo()) + "[" + exprToString(a.indice()) + "]";
            case NodoExpr.AccesoAtributo a -> exprToString(a.objeto()) + "." + a.atributo();
            case NodoExpr.LiteralStruct l -> {
                StringBuilder sb = new StringBuilder("{");
                var it = l.campos().entrySet().iterator();
                while (it.hasNext()) {
                    var entry = it.next();
                    sb.append(entry.getKey()).append(": ").append(exprToString(entry.getValue()));
                    if (it.hasNext()) sb.append(", ");
                }
                sb.append("}");
                yield sb.toString();
            }
            default -> "?";
        };
    }

    public void clear() {
        imageView.setImage(null);
    }

    private byte[] generarImagenBytes(String dot) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Graphviz.fromString(dot)
                    .totalMemory(64 * 1024 * 1024)
                    .render(Format.PNG)
                    .toOutputStream(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}