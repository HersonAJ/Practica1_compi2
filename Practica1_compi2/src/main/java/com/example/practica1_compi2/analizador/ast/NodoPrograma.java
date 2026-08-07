package com.example.practica1_compi2.analizador.ast;

import java.util.List;

public record NodoPrograma(
        List<NodoSentencia> variablesGlobales,
        List<NodoFuncion> funciones,
        List<NodoSentencia> main
) implements NodoAST {

    @Override
    public int linea() {
        return 1;
    }
}