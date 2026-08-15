package com.example.piglatin.analizador.traduccion;

import com.example.piglatin.analizador.ast.NodoPrograma;
import com.example.piglatin.analizador.traduccion.visitantes.VisitanteTraduccion;

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