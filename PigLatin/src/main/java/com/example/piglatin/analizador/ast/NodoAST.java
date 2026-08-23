package com.example.piglatin.analizador.ast;

public sealed interface NodoAST permits NodoExpr, NodoSentencia, NodoFuncion, NodoPrograma,  NodoSentencia.CampoStruct {
    int linea();
    void toPigLatin(StringBuilder sb);
}