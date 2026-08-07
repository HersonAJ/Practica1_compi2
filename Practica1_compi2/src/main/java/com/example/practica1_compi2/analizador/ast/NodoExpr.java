package com.example.practica1_compi2.analizador.ast;

import java.util.List;
import java.util.Map;

public sealed interface NodoExpr extends NodoAST {

    record Binaria(int linea, String operador, NodoExpr izquierda, NodoExpr derecha) implements NodoExpr {}

    // prefijo=true para ++x / --x / non x ; prefijo=false para x++ / x--
    record Unaria(int linea, String operador, NodoExpr operando, boolean prefijo) implements NodoExpr {}

    record LiteralEntero(int linea, int valor) implements NodoExpr {}
    record LiteralDecimal(int linea, double valor) implements NodoExpr {}
    record LiteralTexto(int linea, String valor) implements NodoExpr {}
    record LiteralCaracter(int linea, char valor) implements NodoExpr {}
    record LiteralBooleano(int linea, boolean valor) implements NodoExpr {}

    record Identificador(int linea, String nombre) implements NodoExpr {}

    record AccesoArray(int linea, NodoExpr arreglo, NodoExpr indice) implements NodoExpr {}
    record AccesoAtributo(int linea, NodoExpr objeto, String atributo) implements NodoExpr {}

    record LlamadaFuncion(int linea, String nombre, List<NodoExpr> argumentos) implements NodoExpr {}

    // Literal anonimo de struct: { campo: expr, campo: expr, ... }
    record LiteralStruct(int linea, Map<String, NodoExpr> campos) implements NodoExpr {}
}