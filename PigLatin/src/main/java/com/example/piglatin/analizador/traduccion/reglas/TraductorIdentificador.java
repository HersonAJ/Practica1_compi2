package com.example.piglatin.analizador.traduccion.reglas;

public class TraductorIdentificador implements ReglaTraduccion {

    private static final String VOCALES = "aeiouAEIOU";

    @Override
    public boolean aplica(String texto) {
        //aplica a cualquier identificador que no sea palabra reservada
        return texto != null && !texto.isEmpty() && Character.isLetter(texto.charAt(0));
    }

    @Override
    public String traducir(String texto) {
        if (texto == null || texto.isEmpty()){
            return texto;
        }

        //verificar si comienza con vocal
        if (esVocal(texto.charAt(0))) {
            return texto + "way";
        }

        //comienza con consonante
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

    private boolean esVocal(char c) {
        return VOCALES.indexOf(c) >= 0;
    }
}
