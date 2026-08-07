package com.example.practica1_compi2.analizador.ast;

import java.util.List;

public record NodoFuncion(
        int linea,
        String nombre,
        List<Parametro> parametros,
        String tipoRetorno,
        List<NodoSentencia> variablesLocales,
        List<NodoSentencia> cuerpo
) implements NodoAST {

    public record Parametro(String nombre, String tipo) {}
}