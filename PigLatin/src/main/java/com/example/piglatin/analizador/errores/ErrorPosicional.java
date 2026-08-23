package com.example.piglatin.analizador.errores;

public record ErrorPosicional(int linea, int columna, String mensaje) {

    public String descripcionCompleta() {
        return "Línea " + linea + ":" + columna + " - " + mensaje;
    }
}