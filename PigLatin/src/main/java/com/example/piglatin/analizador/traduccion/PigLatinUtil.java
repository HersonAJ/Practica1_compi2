package com.example.piglatin.analizador.traduccion;

public class PigLatinUtil {

    private static final String VOCALES = "aeiouAEIOU";

    public static String traducir(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        // 1. Regla Especial Porcina
        if (texto.equals("<<")) return "%OINK_OINK";
        if (texto.equals(">>")) return "%OINK";

        // Si no empieza con letra (ej. números, símbolos), no se altera
        if (!Character.isLetter(texto.charAt(0))) {
            return texto;
        }

        // 2. Comienza con vocal -> agregar "way"
        if (esVocal(texto.charAt(0))) {
            return texto + "way";
        }

        // 3. Comienza con consonante -> mover consonantes iniciales y agregar "ay"
        int indice = 0;
        while (indice < texto.length() && !esVocal(texto.charAt(indice))) {
            indice++;
        }

        if (indice == 0) {
            return texto + "way";
        }

        String inicio = texto.substring(0, indice);
        String resto = texto.substring(indice);
        return resto + inicio + "ay";
    }

    private static boolean esVocal(char c) {
        return VOCALES.indexOf(c) >= 0;
    }
}