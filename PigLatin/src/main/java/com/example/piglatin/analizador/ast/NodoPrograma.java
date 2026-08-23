package com.example.piglatin.analizador.ast;

import com.example.piglatin.analizador.traduccion.PigLatinUtil;
import java.util.List;

public record NodoPrograma(
        List<NodoSentencia> variablesGlobales,
        List<NodoFuncion> funciones,
        List<NodoSentencia> main
) implements NodoAST {

    @Override
    public int linea() {
        return 1;
    }

    @Override
    public void toPigLatin(StringBuilder sb) {
        // Variables globales
        for (NodoSentencia sentencia : variablesGlobales) {
            sentencia.toPigLatin(sb);
            sb.append("\n");
        }

        // Funciones
        for (NodoFuncion funcion : funciones) {
            funcion.toPigLatin(sb);
            sb.append("\n");
        }

        // Bloque main (MAIOR>)
        sb.append(PigLatinUtil.traducir("MAIOR")).append(">\n");
        for (NodoSentencia sentencia : main) {
            sb.append("  ");
            sentencia.toPigLatin(sb);
            sb.append("\n");
        }
        sb.append(PigLatinUtil.traducir("FINIS")).append(";");
    }
}