package com.example.practica1_compi2.analizador.traduccion.reglas;

import java.util.Map;

public class TraductorReservadas implements ReglaTraduccion {

    private static final Map<String, String> RESERVADAS = Map.ofEntries(
            Map.entry("esto", "estoway"),
            Map.entry("series", "eriesstay"),
            Map.entry("structura", "ucturastray"),
            Map.entry("finis", "inis fay"),
            Map.entry("si", "i say"),
            Map.entry("aliter", "aliterway"),
            Map.entry("dum", "umday"),
            Map.entry("facere", "acerefay"),
            Map.entry("per", "erpay"),
            Map.entry("perge", "ergepay"),
            Map.entry("interrumpe", "interrumpeway"),
            Map.entry("actio", "actioway"),
            Map.entry("ratio", "atioway"),
            Map.entry("reddere", "eddereray"),
            Map.entry("non", "on nay"),
            Map.entry("numerus", "umerusnay"),
            Map.entry("textum", "extumtay"),
            Map.entry("decimalis", "ecimalisday"),
            Map.entry("littera", "itteralay"),
            Map.entry("verum", "erumvay"),
            Map.entry("falsus", "alsusfay"),
            Map.entry("VARIABILES", "ARIABILESVAY"),
            Map.entry("MUNERA", "UNERAMAY"),
            Map.entry("MAIOR", "AIORMAY"),
            Map.entry("FINIS", "INISFAY")
    );

    @Override
    public boolean aplica(String texto) {
        return texto != null && RESERVADAS.containsKey(texto);
    }

    @Override
    public String traducir(String texto) {
        return RESERVADAS.getOrDefault(texto, texto);
    }
}