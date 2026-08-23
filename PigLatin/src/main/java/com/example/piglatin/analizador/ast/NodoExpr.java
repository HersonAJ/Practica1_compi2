package com.example.piglatin.analizador.ast;

import com.example.piglatin.analizador.ast.nodo.TipoNodoExpr;
import com.example.piglatin.analizador.traduccion.PigLatinUtil;
import java.util.List;
import java.util.Map;

public sealed interface NodoExpr extends NodoAST {
    TipoNodoExpr tipoNodo();

    record Binaria(int linea, String operador, NodoExpr izquierda, NodoExpr derecha) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.BINARIA;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append("(");
            izquierda.toPigLatin(sb);
            sb.append(" ").append(operador).append(" ");
            derecha.toPigLatin(sb);
            sb.append(")");
        }
    }

    // prefijo=true para ++x / --x / non x ; prefijo=false para x++ / x--
    record Unaria(int linea, String operador, NodoExpr operando, boolean prefijo) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.UNARIA;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            String op = PigLatinUtil.traducir(operador);
            if (prefijo) {
                sb.append(op);
                operando.toPigLatin(sb);
            } else {
                operando.toPigLatin(sb);
                sb.append(op);
            }
        }
    }

    record LiteralEntero(int linea, int valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_ENTERO;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(valor);
        }
    }

    record LiteralDecimal(int linea, double valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_DECIMAL;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(valor);
        }
    }

    record LiteralTexto(int linea, String valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_TEXTO;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append("\"").append(valor).append("\"");
        }
    }

    record LiteralCaracter(int linea, char valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_CARACTER;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append("'").append(valor).append("'");
        }
    }

    record LiteralBooleano(int linea, boolean valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_BOOLEANO;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir(valor ? "verum" : "falsus"));
        }
    }

    record Identificador(int linea, String nombre) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.IDENTIFICADOR;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir(nombre));
        }
    }

    record AccesoArray(int linea, NodoExpr arreglo, NodoExpr indice) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.ACCESO_ARRAY;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            arreglo.toPigLatin(sb);
            sb.append("[");
            indice.toPigLatin(sb);
            sb.append("]");
        }
    }

    record AccesoAtributo(int linea, NodoExpr objeto, String atributo) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.ACCESO_ATRIBUTO;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            objeto.toPigLatin(sb);
            sb.append(".").append(PigLatinUtil.traducir(atributo));
        }
    }

    record LlamadaFuncion(int linea, String nombre, List<NodoExpr> argumentos) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LLAMADA_FUNCION;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append(PigLatinUtil.traducir(nombre)).append("(");
            for (int i = 0; i < argumentos.size(); i++) {
                if (i > 0) sb.append(", ");
                argumentos.get(i).toPigLatin(sb);
            }
            sb.append(")");
        }
    }

    // Literal anonimo de struct: { campo: expr, campo: expr, ... }
    record LiteralStruct(int linea, Map<String, NodoExpr> campos) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_STRUCT;
        }

        @Override
        public void toPigLatin(StringBuilder sb) {
            sb.append("{");
            int i = 0;
            for (var entry : campos.entrySet()) {
                if (i > 0) sb.append(", ");
                sb.append(PigLatinUtil.traducir(entry.getKey()))
                        .append(": ");
                entry.getValue().toPigLatin(sb);
                i++;
            }
            sb.append("}");
        }
    }
}