package com.example.piglatin.service;

import com.example.piglatin.analizador.ast.NodoPrograma;
import com.example.piglatin.analizador.errores.ErrorPosicional;
import com.example.piglatin.analizador.pila.PasoPila;
import com.example.piglatin.analizador.semantica.TablaSimbolos;
import com.example.piglatin.analizador.semantica.errores.ErrorSemantico;
import com.example.piglatin.color.ColorMapa;

import java.util.List;

public record ResultadoCompilacion(
        boolean exito,
        NodoPrograma ast,
        TablaSimbolos tablaSimbolos,
        String traduccion,
        List<ColorMapa.TextoColoreado> coloreado,
        List<ErrorPosicional> erroresLexicos,
        List<ErrorPosicional> erroresSintacticos,
        List<ErrorSemantico> erroresSemanticos,
        List<String> errores,
        List<PasoPila> pasosPila
) {
}