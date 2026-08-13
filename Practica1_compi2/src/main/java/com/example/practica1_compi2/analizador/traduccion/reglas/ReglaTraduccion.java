package com.example.practica1_compi2.analizador.traduccion.reglas;

public interface ReglaTraduccion {
    String traducir(String texto);
    boolean aplica(String texto);
}
