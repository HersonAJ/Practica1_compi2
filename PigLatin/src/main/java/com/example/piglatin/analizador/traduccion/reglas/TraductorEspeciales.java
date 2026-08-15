package com.example.piglatin.analizador.traduccion.reglas;

import java.util.Map;

public class TraductorEspeciales implements ReglaTraduccion {

    private static final Map<String, String> ESPECIALES = Map.ofEntries(
            Map.entry("<<", "%OINK_OINK"),
            Map.entry(">>", "%OINK")
    );

    @Override
    public boolean aplica(String texto) {
        return texto != null && ESPECIALES.containsKey(texto);
    }

    @Override
    public String traducir(String texto) {
        return ESPECIALES.getOrDefault(texto, texto);
    }
}