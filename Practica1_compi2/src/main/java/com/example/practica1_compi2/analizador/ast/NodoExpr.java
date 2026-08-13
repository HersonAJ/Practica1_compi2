package com.example.practica1_compi2.analizador.ast;

import com.example.practica1_compi2.analizador.ast.nodo.TipoNodoExpr;

import java.util.List;
import java.util.Map;

public sealed interface NodoExpr extends NodoAST {
    TipoNodoExpr tipoNodo();


    record Binaria(int linea, String operador, NodoExpr izquierda, NodoExpr derecha) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo(){
            return TipoNodoExpr.BINARIA;
        }
    }

    // prefijo=true para ++x / --x / non x ; prefijo=false para x++ / x--
    record Unaria(int linea, String operador, NodoExpr operando, boolean prefijo) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo(){
            return TipoNodoExpr.UNARIA;
        }
    }

    record LiteralEntero(int linea, int valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_ENTERO;
        }
    }
    record LiteralDecimal(int linea, double valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_DECIMAL;
        }
    }
    record LiteralTexto(int linea, String valor) implements NodoExpr {
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_TEXTO;
        }
    }
    record LiteralCaracter(int linea, char valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_CARACTER;
        }
    }
    record LiteralBooleano(int linea, boolean valor) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_BOOLEANO;
        }
    }

    record Identificador(int linea, String nombre) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.IDENTIFICADOR;
        }
    }

    record AccesoArray(int linea, NodoExpr arreglo, NodoExpr indice) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.ACCESO_ARRAY;
        }
    }
    record AccesoAtributo(int linea, NodoExpr objeto, String atributo) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.ACCESO_ATRIBUTO;
        }
    }

    record LlamadaFuncion(int linea, String nombre, List<NodoExpr> argumentos) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LLAMADA_FUNCION;
        }
    }

    // Literal anonimo de struct: { campo: expr, campo: expr, ... }
    record LiteralStruct(int linea, Map<String, NodoExpr> campos) implements NodoExpr {
        @Override
        public TipoNodoExpr tipoNodo() {
            return TipoNodoExpr.LITERAL_STRUCT;
        }
    }
}