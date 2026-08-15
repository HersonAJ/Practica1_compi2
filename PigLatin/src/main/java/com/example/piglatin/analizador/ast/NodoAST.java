package com.example.piglatin.analizador.ast;

public sealed interface NodoAST permits NodoExpr, NodoSentencia, NodoFuncion, NodoPrograma {
    int linea();
}
