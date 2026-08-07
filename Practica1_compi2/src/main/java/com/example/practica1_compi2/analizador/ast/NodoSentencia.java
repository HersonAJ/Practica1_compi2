package com.example.practica1_compi2.analizador.ast;

import java.util.List;
import java.util.Map;


public sealed interface NodoSentencia extends NodoAST {

    // tipo: "numerus" | "textum" | "decimalis" | "littera" | "booleano"
    record DeclaracionVariable(int linea, String nombre, String tipo, NodoExpr valorInicial) implements NodoSentencia {}

    record DeclaracionArreglo(int linea, String nombre, int tamano, String tipo, List<NodoExpr> valoresIniciales) implements NodoSentencia {}

    record DefinicionStruct(int linea, String nombre, List<CampoStruct> campos) implements NodoSentencia {}

    // esto <nombre> : <tipoStruct> { campo: expr, ... }
    record InstanciaStruct(int linea, String nombre, String tipoStruct, Map<String, NodoExpr> valores) implements NodoSentencia {}

    record Asignacion(int linea, NodoExpr referencia, NodoExpr valor) implements NodoSentencia {}

    // referencia = { campo: expr, ... }  (sin ';', confirmado con el auxiliar)
    record AsignacionStructLiteral(int linea, NodoExpr referencia, NodoExpr.LiteralStruct valor) implements NodoSentencia {}

    record Condicional(int linea, List<Rama> ramas, List<NodoSentencia> elseCuerpo) implements NodoSentencia {}

    record CicloDum(int linea, NodoExpr condicion, List<NodoSentencia> cuerpo) implements NodoSentencia {}
    record CicloFacere(int linea, List<NodoSentencia> cuerpo, NodoExpr condicion) implements NodoSentencia {}

    // incremento desazucarado siempre a un NodoSentencia.Asignacion (ver ASTBuilder)
    record CicloPer(int linea, DeclaracionVariable inicializacion, NodoExpr condicion,
                    NodoSentencia incremento, List<NodoSentencia> cuerpo) implements NodoSentencia {}

    record Retorno(int linea, NodoExpr valor) implements NodoSentencia {}

    // variable == null  =>  '<<' sin capturar valor (solo lee y descarta)
    record Lectura(int linea, String variable) implements NodoSentencia {}

    record Escritura(int linea, List<NodoExpr> valores) implements NodoSentencia {}

    // tipo: "perge" | "interrumpe"
    record InterrupcionCiclo(int linea, String tipo) implements NodoSentencia {}

    record LlamadaFuncionSentencia(int linea, NodoExpr.LlamadaFuncion llamada) implements NodoSentencia {}

    // ---- Tipos auxiliares (no son NodoAST, no tienen numero de linea propio) ----

    record CampoStruct(String nombre, String tipo) {}

    record Rama(NodoExpr condicion, List<NodoSentencia> cuerpo) {}
}