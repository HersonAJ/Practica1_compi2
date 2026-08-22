package com.example.piglatin.analizador.pila;

import java.util.List;

public record PasoPila(
        int numero,
        TipoOperacion operacion,
        String simbolo,
        List<String> simbolosReducidos,
        List<String> pila,
        String descripcion
) {
}
