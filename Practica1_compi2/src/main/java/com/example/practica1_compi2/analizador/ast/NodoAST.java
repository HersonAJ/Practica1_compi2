package com.example.practica1_compi2.analizador.ast;

public sealed interface NodoAST permits NodoExpr, NodoSentencia, NodoFuncion, NodoPrograma {
    int linea();
}
