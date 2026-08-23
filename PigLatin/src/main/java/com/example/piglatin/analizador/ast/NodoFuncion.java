package com.example.piglatin.analizador.ast;

import com.example.piglatin.analizador.traduccion.PigLatinUtil;
import java.util.List;

public record NodoFuncion(
        int linea,
        String nombre,
        List<Parametro> parametros,
        String tipoRetorno,
        List<NodoSentencia> variablesLocales,
        List<NodoSentencia> cuerpo
) implements NodoAST {

    public record Parametro(String nombre, String tipo) {}

    @Override
    public void toPigLatin(StringBuilder sb) {
        String nombreTraducido = PigLatinUtil.traducir(nombre);

        if (tipoRetorno == null) {
            sb.append(PigLatinUtil.traducir("actio")).append(" ").append(nombreTraducido).append("(");
        } else {
            sb.append(PigLatinUtil.traducir("ratio")).append(" ")
                    .append(PigLatinUtil.traducir(tipoRetorno)).append(" ")
                    .append(nombreTraducido).append("(");
        }

        for (int i = 0; i < parametros.size(); i++) {
            Parametro p = parametros.get(i);
            if (i > 0) sb.append(", ");
            sb.append(PigLatinUtil.traducir("esto")).append(" ")
                    .append(PigLatinUtil.traducir(p.nombre())).append(" : ")
                    .append(PigLatinUtil.traducir(p.tipo()));
        }
        sb.append(") {\n");

        if (!variablesLocales.isEmpty()) {
            sb.append("  ").append(PigLatinUtil.traducir("VARIABILES")).append("[\n");
            for (NodoSentencia sentencia : variablesLocales) {
                sb.append("    ");
                sentencia.toPigLatin(sb);
                sb.append("\n");
            }
            sb.append("  ]\n");
        }

        for (NodoSentencia sentencia : cuerpo) {
            sb.append("  ");
            sentencia.toPigLatin(sb);
            sb.append("\n");
        }
        sb.append("} ").append(PigLatinUtil.traducir("finis")).append(";");
    }
}