package com.example.piglatin.analizador.ast;

import com.example.piglatin.analizador.ast.nodo.TipoNodoSentencia;
import com.example.piglatin.analizador.traduccion.PigLatinUtil;

import java.util.List;
import java.util.Map;

public sealed interface NodoSentencia extends NodoAST {

    TipoNodoSentencia tipoNodo();

    // tipo: "numerus" | "textum" | "decimalis" | "littera" | "booleano"
    record DeclaracionVariable(int linea, String nombre, String tipo, NodoExpr valorInicial) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.DECLARACION_VARIABLE;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir("esto")).append(" ")
                    .append(PigLatinUtil.traducir(nombre)).append(" : ")
                    .append(PigLatinUtil.traducir(tipo));

            if (valorInicial != null) {
                sb.append(" = ");
                valorInicial.toPigLatin(sb);
            }
            sb.append(";");
        }
    }

    record DeclaracionArreglo(int linea, String nombre, int tamano, String tipo, List<NodoExpr> valoresIniciales) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.DECLARACION_ARREGLO;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir("esto")).append(" ")
                    .append(PigLatinUtil.traducir(nombre)).append("[").append(tamano).append("] : ")
                    .append(PigLatinUtil.traducir(tipo));

            if (valoresIniciales != null && !valoresIniciales.isEmpty()) {
                sb.append(" = {");
                for (int i = 0; i < valoresIniciales.size(); i++) {
                    if (i > 0) sb.append(", ");
                    valoresIniciales.get(i).toPigLatin(sb);
                }
                sb.append("}");
            }
            sb.append(";");
        }
    }

    record DefinicionStruct(int linea, String nombre, List<CampoStruct> campos) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.DEFINICION_STRUCT;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir("struct")).append(" ")
                    .append(PigLatinUtil.traducir(nombre)).append(" {\n");
            for (CampoStruct campo : campos) {
                sb.append("  ").append(PigLatinUtil.traducir(campo.nombre())).append(" : ")
                        .append(PigLatinUtil.traducir(campo.tipo())).append(";\n");
            }
            sb.append("};");
        }
    }

    // esto <nombre> : <tipoStruct> { campo: expr, ... }
    record InstanciaStruct(int linea, String nombre, String tipoStruct, Map<String, NodoExpr> valores) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.INSTANCIA_STRUCT;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir("esto")).append(" ")
                    .append(PigLatinUtil.traducir(nombre)).append(" : ")
                    .append(PigLatinUtil.traducir(tipoStruct)).append(" {");

            int i = 0;
            for (var entry : valores.entrySet()) {
                if (i > 0) sb.append(", ");
                sb.append(PigLatinUtil.traducir(entry.getKey())).append(": ");
                entry.getValue().toPigLatin(sb);
                i++;
            }
            sb.append("};");
        }
    }

    record Asignacion(int linea, NodoExpr referencia, NodoExpr valor) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.ASIGNACION;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            referencia.toPigLatin(sb);
            sb.append(" = ");
            valor.toPigLatin(sb);
            sb.append(";");
        }
    }

    // referencia = { campo: expr, ... }  (sin ';', confirmado con el auxiliar)
    record AsignacionStructLiteral(int linea, NodoExpr referencia, NodoExpr.LiteralStruct valor) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.ASIGNACION_STRUCT_LITERAL;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            referencia.toPigLatin(sb);
            sb.append(" = ");
            valor.toPigLatin(sb);
        }
    }

    record Condicional(int linea, List<Rama> ramas, List<NodoSentencia> elseCuerpo) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.CONDICIONAL;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            for (int i = 0; i < ramas.size(); i++) {
                Rama rama = ramas.get(i);
                if (i == 0) {
                    sb.append(PigLatinUtil.traducir("si")).append(" (");
                } else {
                    sb.append(" ").append(PigLatinUtil.traducir("aut")).append(" ")
                            .append(PigLatinUtil.traducir("si")).append(" (");
                }
                rama.condicion().toPigLatin(sb);
                sb.append(") {\n");

                for (NodoSentencia stmt : rama.cuerpo()) {
                    sb.append("  ");
                    stmt.toPigLatin(sb);
                    sb.append("\n");
                }
                sb.append("}");
            }

            if (elseCuerpo != null && !elseCuerpo.isEmpty()) {
                sb.append(" ").append(PigLatinUtil.traducir("aut")).append(" {\n");
                for (NodoSentencia stmt : elseCuerpo) {
                    sb.append("  ");
                    stmt.toPigLatin(sb);
                    sb.append("\n");
                }
                sb.append("}");
            }
        }
    }

    record CicloDum(int linea, NodoExpr condicion, List<NodoSentencia> cuerpo) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.CICLO_DUM;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir("dum")).append(" (");
            condicion.toPigLatin(sb);
            sb.append(") {\n");

            for (NodoSentencia stmt : cuerpo) {
                sb.append("  ");
                stmt.toPigLatin(sb);
                sb.append("\n");
            }
            sb.append("}");
        }
    }

    record CicloFacere(int linea, List<NodoSentencia> cuerpo, NodoExpr condicion) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.CICLO_FACERE;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir("facere")).append(" {\n");
            for (NodoSentencia stmt : cuerpo) {
                sb.append("  ");
                stmt.toPigLatin(sb);
                sb.append("\n");
            }
            sb.append("} ").append(PigLatinUtil.traducir("dum")).append(" (");
            condicion.toPigLatin(sb);
            sb.append(");");
        }
    }

    // incremento desazucarado siempre a un NodoSentencia.Asignacion (ver ASTBuilder)
    record CicloPer(int linea, DeclaracionVariable inicializacion, NodoExpr condicion,
                    NodoSentencia incremento, List<NodoSentencia> cuerpo) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.CICLO_PER;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir("per")).append(" (");

            if (inicializacion != null) {
                // quitamos el ';' final de la declaracion para colocarlo dentro del for ( ... ; ... ; ... )
                StringBuilder initSb = new StringBuilder();
                inicializacion.toPigLatin(initSb);
                String initStr = initSb.toString();
                if (initStr.endsWith(";")) {
                    initStr = initStr.substring(0, initStr.length() - 1);
                }
                sb.append(initStr);
            }
            sb.append("; ");

            if (condicion != null) {
                condicion.toPigLatin(sb);
            }
            sb.append("; ");

            if (incremento != null) {
                StringBuilder incSb = new StringBuilder();
                incremento.toPigLatin(incSb);
                String incStr = incSb.toString();
                if (incStr.endsWith(";")) {
                    incStr = incStr.substring(0, incStr.length() - 1);
                }
                sb.append(incStr);
            }
            sb.append(") {\n");

            for (NodoSentencia stmt : cuerpo) {
                sb.append("  ");
                stmt.toPigLatin(sb);
                sb.append("\n");
            }
            sb.append("}");
        }
    }

    record Retorno(int linea, NodoExpr valor) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.RETORNO;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir("redde"));
            if (valor != null) {
                sb.append(" ");
                valor.toPigLatin(sb);
            }
            sb.append(";");
        }
    }

    // variable == null => '<<' sin capturar valor (solo lee y descarta)
    record Lectura(int linea, String variable) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.LECTURA;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            if (variable != null) {
                sb.append(PigLatinUtil.traducir(variable)).append(" ");
            }
            sb.append(PigLatinUtil.traducir("<<")).append(";");
        }
    }

    record Escritura(int linea, List<NodoExpr> valores) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.ESCRITURA;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir(">>"));
            if (valores != null && !valores.isEmpty()) {
                sb.append(" ");
                for (int i = 0; i < valores.size(); i++) {
                    if (i > 0) sb.append(", ");
                    valores.get(i).toPigLatin(sb);
                }
            }
            sb.append(";");
        }
    }

    // tipo: "perge" | "interrumpe"
    record InterrupcionCiclo(int linea, String tipo) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.INTERRUPCION_CICLO;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir(tipo)).append(";");
        }
    }

    record LlamadaFuncionSentencia(int linea, NodoExpr.LlamadaFuncion llamada) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo() {
            return TipoNodoSentencia.LLAMADA_FUNCION_SENTENCIA;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            llamada.toPigLatin(sb);
            sb.append(";");
        }
    }

    record CampoStruct(String nombre, String tipo) {}

    record Rama(NodoExpr condicion, List<NodoSentencia> cuerpo) {}
}