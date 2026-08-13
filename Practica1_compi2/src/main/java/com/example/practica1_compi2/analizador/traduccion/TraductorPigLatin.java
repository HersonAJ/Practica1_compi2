package com.example.practica1_compi2.analizador.traduccion;

import com.example.practica1_compi2.analizador.ast.NodoPrograma;
import com.example.practica1_compi2.analizador.traduccion.visitantes.VisitanteTraduccion;

public class TraductorPigLatin {

    private final VisitanteTraduccion visitante;

    public TraductorPigLatin() {
        this.visitante = new VisitanteTraduccion();
    }

    public String traducir(NodoPrograma programa) {
        if (programa == null) {
            return "";
        }
        return visitante.traducirPrograma(programa);
    }
}