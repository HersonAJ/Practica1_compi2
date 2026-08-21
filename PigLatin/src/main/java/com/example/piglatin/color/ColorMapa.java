package com.example.piglatin.color;

import java.util.HashMap;
import java.util.Map;

public class ColorMapa {

    private static final Map<String, String> MAPA = new HashMap<>();
    private static final String DEFAULT_COLOR = "#FFFFFF";

    static {
        // Identificadores - #DCDCAA (Amarillo)
        agregar("#DCDCAA", "ID");

        // Palabras reservadas - #569CD6 (Azul)
        agregar("#569CD6",
                "VARIABILES", "MUNERA", "MAIOR", "FIN_PROGRAMA", "ESTO", "SERIES", "STRUCTURA",
                "FINIS", "SI", "ALITER", "DUM", "FACERE", "PER", "PERGE", "INTERRUMPE",
                "ACTIO", "RATIO", "REDDERE", "NON"
        );

        // Tipos - #4EC9B0 (Verde Agua)
        agregar("#4EC9B0",
                "NUMERUS", "TEXTUM", "DECIMALIS", "LITTERA"
        );

        // NUMBERS y BOOLEANS - #B5CEA8 (Verde Claro)
        agregar("#B5CEA8",
                "INT", "FLOAT", "VERUM", "FALSUS"
        );

        //STRINGS y CHARS - #CE9178 (Naranja)
        agregar("#CE9178",
                "STRING", "CHAR"
        );

        // Operadores - #D16969 (Rojo/Rosa)
        agregar("#D16969",
                "ASIGNAR", "MAS", "MENOS", "MULT", "DIV", "IGUAL", "DISTINTO",
                "MENOR", "MAYOR", "MENIG", "MAYIG", "AND", "OR", "INC", "DEC",
                "LEER", "ESCRIBIR",
                "=", "+", "-", "*", "/", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "++", "--", "<<", ">>"
        );

        // Puntuacion y limitadores - #808080 (Gris Medio)
        agregar("#808080",
                "PUNTOCOMA", "COMA", "PUNTO", "DOSPUNTOS", "LLAVE_A", "LLAVE_C",
                "CORCH_A", "CORCH_C", "PAR_A", "PAR_C",
                ";", ",", ".", ":", "{", "}", "[", "]", "(", ")"
        );
    }

    public static void agregar(String color, String... tokens) {
        for (String token : tokens) {
            MAPA.put(token, color);
        }
    }

    public static String getColor(String tokenType) {
        return MAPA.getOrDefault(tokenType, DEFAULT_COLOR);
    }

    public static TextoColoreado colorear(String texto, String tokenType) {
        return new TextoColoreado(texto, getColor(tokenType));
    }

    public record TextoColoreado(String texto, String color) {}
}