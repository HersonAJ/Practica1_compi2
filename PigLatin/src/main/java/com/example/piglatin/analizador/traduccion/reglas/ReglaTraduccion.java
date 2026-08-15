package com.example.piglatin.analizador.traduccion.reglas;

public interface ReglaTraduccion {
    String traducir(String texto);
    boolean aplica(String texto);
}
