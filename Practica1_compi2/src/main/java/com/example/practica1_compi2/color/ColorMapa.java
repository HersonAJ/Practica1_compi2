/*package com.example.practica1_compi2.color;

import java.util.HashMap;
import java.util.Map;

public class ColorMapa {

    private static final Map<String, String> MAPA = new HashMap<>();
    private static final String DEFAULT_COLOR = "#D4D4D4";

    static {
        agregar("#569DC6","VARIABLES", "MUNERA", "MAIOR", "FIN_PROGRAMA", "ESTO", "SERIES", "STRUCTURA",
                "FINIS", "ACTIO", "RATIO", "REDDERE", "NON", "SI", "ALITER", "DIUM", "FACERE", "PER", "PERGE", "INTERRUMPE");

        agregar("#4EC9B0", "NUMERUS", "TEXTUM", "DECIMALIS", "LITTERA");

        agregar("#CE9178", "INT", "FLOAT", "STRING", "CHAR", "VERUM", "FALSUS");

        agregar("#D16969", "ASIGNAR", "MAS", "MENOS", "MULT", "DIV", "IGUAL", "DISTINTO", "MENOR", "MAYOR",
                "MENIG", "MAYIG", "AND", "OR", "INC", "DEC", "LEER", "ESCRIBIR");
        agregar("#D4D4D4", "PUNTOCOMA", "COMA", "PUNTO", "DOSPUNTOS", "LLAVE_A", "LLAVE_C", "CORCH_A", "CORCH_C" ,
                "PAR_A", "PAR_C", "ID", "WS");
        agregar("#6A9955", "LINEA_COMENTARIO", "BLOQUE_COMENTARIO");
    }

    private static void agregar(String color, String... tokens){
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
*/

package com.example.practica1_compi2.color;

import java.util.HashMap;
import java.util.Map;

public class ColorMapa {

    private static final Map<String, String> MAPA = new HashMap<>();
    private static final String DEFAULT_COLOR = "#D4D4D4";

    static {
        // ===== KEYWORDS - #569DC6 =====
        agregar("#569DC6","VARIABILES", "MUNERA", "MAIOR", "FIN_PROGRAMA", "ESTO", "SERIES", "STRUCTURA",
                "FINIS", "ACTIO", "RATIO", "REDDERE", "NON", "SI", "ALITER", "DUM", "FACERE", "PER", "PERGE", "INTERRUMPE");

        // ===== TYPES - #4EC9B0 =====
        // Mayúsculas (para tokens del parser)
        agregar("#4EC9B0", "NUMERUS", "TEXTUM", "DECIMALIS", "LITTERA");
        // Minúsculas (para cuando se usa ctx.getText())
        agregar("#4EC9B0", "numerus", "textum", "decimalis", "littera");

        // ===== LITERALS - #CE9178 =====
        agregar("#CE9178", "INT", "FLOAT", "STRING", "CHAR", "VERUM", "FALSUS");
        // Minúsculas para literales
        agregar("#CE9178", "verum", "falsus");

        // ===== OPERATORS - #D16969 =====
        // Tokens del parser
        agregar("#D16969", "ASIGNAR", "MAS", "MENOS", "MULT", "DIV", "IGUAL", "DISTINTO", "MENOR", "MAYOR",
                "MENIG", "MAYIG", "AND", "OR", "INC", "DEC", "LEER", "ESCRIBIR");
        // Operadores literales (para ctx.getText())
        agregar("#D16969", "=", "+", "-", "*", "/", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "++", "--", "<<", ">>");

        // ===== PUNCTUATION - #D4D4D4 =====
        agregar("#D4D4D4", "PUNTOCOMA", "COMA", "PUNTO", "DOSPUNTOS", "LLAVE_A", "LLAVE_C", "CORCH_A", "CORCH_C",
                "PAR_A", "PAR_C", "ID", "WS");
        // Puntuación literal
        agregar("#D4D4D4", ";", ",", ".", ":", "{", "}", "[", "]", "(", ")");

        // ===== COMMENTS - #6A9955 =====
        agregar("#6A9955", "LINEA_COMENTARIO", "BLOQUE_COMENTARIO");
    }

    private static void agregar(String color, String... tokens) {
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