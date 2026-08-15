package com.example.piglatin.analizador.ast.nodo;

public enum TipoNodoExpr {
    BINARIA,
    UNARIA,
    LITERAL_ENTERO,
    LITERAL_DECIMAL,
    LITERAL_TEXTO,
    LITERAL_CARACTER,
    LITERAL_BOOLEANO,
    IDENTIFICADOR,
    ACCESO_ARRAY,
    ACCESO_ATRIBUTO,
    LLAMADA_FUNCION,
    LITERAL_STRUCT
}
