package com.example.practica1_compi2.analizador.traduccion.visitantes;

import com.example.practica1_compi2.analizador.ast.*;
import com.example.practica1_compi2.analizador.traduccion.reglas.ReglaTraduccion;
import com.example.practica1_compi2.analizador.traduccion.reglas.TraductorIdentificador;
import com.example.practica1_compi2.analizador.traduccion.reglas.TraductorReservadas;
import com.example.practica1_compi2.analizador.traduccion.reglas.TraductorEspeciales;

import java.util.ArrayList;
import java.util.List;

public class VisitanteTraduccion {

    private final List<ReglaTraduccion> reglas;

    public VisitanteTraduccion() {
        this.reglas = new ArrayList<>();
        this.reglas.add(new TraductorReservadas());
        this.reglas.add(new TraductorEspeciales());
        this.reglas.add(new TraductorIdentificador());
    }

    public String traducirPrograma(NodoPrograma programa) {
        StringBuilder sb = new StringBuilder();

        // Traducir variables globales
        for (NodoSentencia sentencia : programa.variablesGlobales()) {
            sb.append(traducirSentencia(sentencia)).append("\n");
        }

        // Traducir funciones
        for (NodoFuncion funcion : programa.funciones()) {
            sb.append(traducirFuncion(funcion)).append("\n");
        }

        // Traducir main
        sb.append("MAIOR>\n");
        for (NodoSentencia sentencia : programa.main()) {
            sb.append(traducirSentencia(sentencia)).append("\n");
        }
        sb.append("FINIS;");

        return sb.toString();
    }

    private String traducirFuncion(NodoFuncion funcion) {
        StringBuilder sb = new StringBuilder();
        String nombre = traducirTexto(funcion.nombre());
        String tipoRetorno = funcion.tipoRetorno() != null ? traducirTexto(funcion.tipoRetorno()) : null;

        if (tipoRetorno == null) {
            sb.append("actio ").append(nombre).append("(");
        } else {
            sb.append("ratio ").append(tipoRetorno).append(" ").append(nombre).append("(");
        }

        // Parámetros
        for (int i = 0; i < funcion.parametros().size(); i++) {
            NodoFuncion.Parametro p = funcion.parametros().get(i);
            if (i > 0) sb.append(", ");
            sb.append("esto ").append(traducirTexto(p.nombre())).append(" : ").append(traducirTexto(p.tipo()));
        }
        sb.append(") {\n");

        // Variables locales
        if (!funcion.variablesLocales().isEmpty()) {
            sb.append("VARIABILES[\n");
            for (NodoSentencia sentencia : funcion.variablesLocales()) {
                sb.append(traducirSentencia(sentencia)).append("\n");
            }
            sb.append("]\n");
        }

        // Cuerpo
        for (NodoSentencia sentencia : funcion.cuerpo()) {
            sb.append(traducirSentencia(sentencia)).append("\n");
        }
        sb.append("} finis;");

        return sb.toString();
    }

    private String traducirSentencia(NodoSentencia sentencia) {
        if (sentencia instanceof NodoSentencia.DeclaracionVariable var) {
            return traducirDeclaracionVariable(var);
        } else if (sentencia instanceof NodoSentencia.DeclaracionArreglo arr) {
            return traducirDeclaracionArreglo(arr);
        } else if (sentencia instanceof NodoSentencia.DefinicionStruct struct) {
            return traducirDefinicionStruct(struct);
        } else if (sentencia instanceof NodoSentencia.InstanciaStruct instancia) {
            return traducirInstanciaStruct(instancia);
        } else if (sentencia instanceof NodoSentencia.Asignacion asignacion) {
            return traducirAsignacion(asignacion);
        } else if (sentencia instanceof NodoSentencia.Condicional condicional) {
            return traducirCondicional(condicional);
        } else if (sentencia instanceof NodoSentencia.CicloDum ciclo) {
            return traducirCicloDum(ciclo);
        } else if (sentencia instanceof NodoSentencia.CicloFacere ciclo) {
            return traducirCicloFacere(ciclo);
        } else if (sentencia instanceof NodoSentencia.CicloPer ciclo) {
            return traducirCicloPer(ciclo);
        } else if (sentencia instanceof NodoSentencia.Retorno retorno) {
            return traducirRetorno(retorno);
        } else if (sentencia instanceof NodoSentencia.Lectura lectura) {
            return traducirLectura(lectura);
        } else if (sentencia instanceof NodoSentencia.Escritura escritura) {
            return traducirEscritura(escritura);
        } else if (sentencia instanceof NodoSentencia.InterrupcionCiclo interrupcion) {
            return traducirInterrupcion(interrupcion);
        } else if (sentencia instanceof NodoSentencia.LlamadaFuncionSentencia llamada) {
            return traducirLlamadaFuncion(llamada.llamada()) + ";";
        }
        return "";
    }

    private String traducirDeclaracionVariable(NodoSentencia.DeclaracionVariable var) {
        String nombre = traducirTexto(var.nombre());
        String tipo = traducirTexto(var.tipo());
        String valor = var.valorInicial() != null ? " " + traducirExpr(var.valorInicial()) : "";
        return "esto " + nombre + " : " + tipo + valor + ";";
    }

    private String traducirDeclaracionArreglo(NodoSentencia.DeclaracionArreglo arr) {
        String nombre = traducirTexto(arr.nombre());
        String tipo = traducirTexto(arr.tipo());
        StringBuilder sb = new StringBuilder();
        sb.append("series ").append(nombre).append("[").append(arr.tamano()).append("] : ").append(tipo);
        if (!arr.valoresIniciales().isEmpty()) {
            sb.append(" {");
            for (int i = 0; i < arr.valoresIniciales().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(traducirExpr(arr.valoresIniciales().get(i)));
            }
            sb.append("}");
        }
        sb.append(";");
        return sb.toString();
    }

    private String traducirDefinicionStruct(NodoSentencia.DefinicionStruct struct) {
        StringBuilder sb = new StringBuilder();
        sb.append("structura ").append(traducirTexto(struct.nombre())).append(" {\n");
        for (NodoSentencia.CampoStruct campo : struct.campos()) {
            sb.append("  esto ").append(traducirTexto(campo.nombre())).append(" : ").append(traducirTexto(campo.tipo())).append(";\n");
        }
        sb.append("} finis;");
        return sb.toString();
    }

    private String traducirInstanciaStruct(NodoSentencia.InstanciaStruct instancia) {
        StringBuilder sb = new StringBuilder();
        sb.append("esto ").append(traducirTexto(instancia.nombre())).append(" : ").append(traducirTexto(instancia.tipoStruct())).append(" {\n");
        for (var entry : instancia.valores().entrySet()) {
            sb.append("  ").append(traducirTexto(entry.getKey())).append(": ").append(traducirExpr(entry.getValue())).append(",\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private String traducirAsignacion(NodoSentencia.Asignacion asignacion) {
        return traducirExpr(asignacion.referencia()) + " = " + traducirExpr(asignacion.valor()) + ";";
    }

    private String traducirCondicional(NodoSentencia.Condicional condicional) {
        StringBuilder sb = new StringBuilder();

        // Primera rama (si)
        NodoSentencia.Rama primera = condicional.ramas().get(0);
        //sb.append("si (").append(traducirExpr(primera.condicion())).append(") {\n");
        sb.append(traducirTexto("si")).append(" (").append(traducirExpr(primera.condicion())).append(") {\n");
        for (NodoSentencia s : primera.cuerpo()) {
            sb.append("  ").append(traducirSentencia(s)).append("\n");
        }
        sb.append("}");

        // Ramas aliter con condición
        for (int i = 1; i < condicional.ramas().size(); i++) {
            NodoSentencia.Rama rama = condicional.ramas().get(i);
            sb.append(" aliter (").append(traducirExpr(rama.condicion())).append(") {\n");
            for (NodoSentencia s : rama.cuerpo()) {
                sb.append("  ").append(traducirSentencia(s)).append("\n");
            }
            sb.append("}");
        }

        // Else
        if (condicional.elseCuerpo() != null) {
            sb.append(" aliter {\n");
            for (NodoSentencia s : condicional.elseCuerpo()) {
                sb.append("  ").append(traducirSentencia(s)).append("\n");
            }
            sb.append("}");
        }

        sb.append(" finis;");
        return sb.toString();
    }

    private String traducirCicloDum(NodoSentencia.CicloDum ciclo) {
        StringBuilder sb = new StringBuilder();
        sb.append("dum (").append(traducirExpr(ciclo.condicion())).append(") {\n");
        for (NodoSentencia s : ciclo.cuerpo()) {
            sb.append("  ").append(traducirSentencia(s)).append("\n");
        }
        sb.append("} finis;");
        return sb.toString();
    }

    private String traducirCicloFacere(NodoSentencia.CicloFacere ciclo) {
        StringBuilder sb = new StringBuilder();
        sb.append("facere {\n");
        for (NodoSentencia s : ciclo.cuerpo()) {
            sb.append("  ").append(traducirSentencia(s)).append("\n");
        }
        sb.append("} dum (").append(traducirExpr(ciclo.condicion())).append(");");
        return sb.toString();
    }

    private String traducirCicloPer(NodoSentencia.CicloPer ciclo) {
        StringBuilder sb = new StringBuilder();
        sb.append("per (").append(traducirSentencia(ciclo.inicializacion())).append(" ");
        sb.append(traducirExpr(ciclo.condicion())).append("; ");
        sb.append(traducirSentencia(ciclo.incremento())).append(") {\n");
        for (NodoSentencia s : ciclo.cuerpo()) {
            sb.append("  ").append(traducirSentencia(s)).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private String traducirRetorno(NodoSentencia.Retorno retorno) {
        return "reddere " + traducirExpr(retorno.valor()) + ";";
    }

    private String traducirLectura(NodoSentencia.Lectura lectura) {
        if (lectura.variable() != null) {
            return traducirTexto(lectura.variable()) + " <<";
        }
        return "<<";
    }

    private String traducirEscritura(NodoSentencia.Escritura escritura) {
        StringBuilder sb = new StringBuilder();
        for (NodoExpr expr : escritura.valores()) {
            sb.append(">> ").append(traducirExpr(expr)).append(" ");
        }
        sb.append(";");
        return sb.toString();
    }

    private String traducirInterrupcion(NodoSentencia.InterrupcionCiclo interrupcion) {
        return traducirTexto(interrupcion.tipo()) + ";";
    }

    private String traducirLlamadaFuncion(NodoExpr.LlamadaFuncion llamada) {
        StringBuilder sb = new StringBuilder();
        sb.append(traducirTexto(llamada.nombre())).append("(");
        for (int i = 0; i < llamada.argumentos().size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(traducirExpr(llamada.argumentos().get(i)));
        }
        sb.append(")");
        return sb.toString();
    }

    private String traducirExpr(NodoExpr expr) {
        if (expr instanceof NodoExpr.LiteralEntero lit) {
            return String.valueOf(lit.valor());
        } else if (expr instanceof NodoExpr.LiteralDecimal lit) {
            return String.valueOf(lit.valor());
        } else if (expr instanceof NodoExpr.LiteralTexto lit) {
            return "\"" + lit.valor() + "\"";
        } else if (expr instanceof NodoExpr.LiteralCaracter lit) {
            return "'" + lit.valor() + "'";
        } else if (expr instanceof NodoExpr.LiteralBooleano lit) {
            return lit.valor() ? "verum" : "falsus";
        } else if (expr instanceof NodoExpr.Identificador id) {
            return traducirTexto(id.nombre());
        } else if (expr instanceof NodoExpr.Binaria bin) {
            return "(" + traducirExpr(bin.izquierda()) + " " + bin.operador() + " " + traducirExpr(bin.derecha()) + ")";
        } else if (expr instanceof NodoExpr.Unaria unaria) {
            if (unaria.prefijo()) {
                return unaria.operador() + traducirExpr(unaria.operando());
            } else {
                return traducirExpr(unaria.operando()) + unaria.operador();
            }
        } else if (expr instanceof NodoExpr.AccesoArray acceso) {
            return traducirExpr(acceso.arreglo()) + "[" + traducirExpr(acceso.indice()) + "]";
        } else if (expr instanceof NodoExpr.AccesoAtributo acceso) {
            return traducirExpr(acceso.objeto()) + "." + traducirTexto(acceso.atributo());
        } else if (expr instanceof NodoExpr.LlamadaFuncion llamada) {
            return traducirLlamadaFuncion(llamada);
        } else if (expr instanceof NodoExpr.LiteralStruct lit) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            int i = 0;
            for (var entry : lit.campos().entrySet()) {
                if (i > 0) sb.append(", ");
                sb.append(traducirTexto(entry.getKey())).append(": ").append(traducirExpr(entry.getValue()));
                i++;
            }
            sb.append("}");
            return sb.toString();
        }
        return "";
    }

    private String traducirTexto(String texto) {
        for (ReglaTraduccion regla : reglas) {
            if (regla.aplica(texto)) {
                return regla.traducir(texto);
            }
        }
        return texto;
    }
}