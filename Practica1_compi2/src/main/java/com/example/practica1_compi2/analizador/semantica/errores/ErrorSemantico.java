package com.example.practica1_compi2.analizador.semantica.errores;

public record ErrorSemantico(int linea, String mensaje) {
    @Override
    public String toString() {
        return "Error semantico en la linea " + linea + ": " + mensaje;
    }
}
