package com.example.practica1_compi2.analizador.semantica.validadores;

import com.example.practica1_compi2.analizador.ast.NodoExpr;
import com.example.practica1_compi2.analizador.semantica.TablaSimbolos;
import com.example.practica1_compi2.analizador.semantica.errores.ErrorSemantico;

import java.util.List;
import java.util.Map;

public class ValidadorTipos {

    private static final Map<String, Integer> JERARQUIA = Map.of(
            "textum", 5,
            "decimalis", 4,
            "numerus", 3,
            "littera", 2,
            "booleano", 1
    );

    private final TablaSimbolos tabla;
    private final List<ErrorSemantico> errores;

    public ValidadorTipos(TablaSimbolos tabla, List<ErrorSemantico> errores) {
        this.tabla = tabla;
        this.errores = errores;
    }

    public String inferirTipo(NodoExpr expr) {
        if (expr instanceof NodoExpr.LiteralEntero) {
            return "numerus";
        } else if (expr instanceof NodoExpr.LiteralDecimal) {
            return "decimalis";
        } else if (expr instanceof NodoExpr.LiteralTexto) {
            return "textum";
        } else if (expr instanceof NodoExpr.LiteralCaracter) {
            return "littera";
        } else if (expr instanceof NodoExpr.LiteralBooleano) {
            return "booleano";
        } else if (expr instanceof NodoExpr.Identificador id) {
            var opt = tabla.buscarVariable(id.nombre());
            if (opt.isEmpty()) {
                return "desconocido";
            }
            return opt.get().tipo();
        } else if (expr instanceof NodoExpr.Binaria bin) {
            return inferirBinaria(bin);
        } else if (expr instanceof NodoExpr.Unaria unaria) {
            return inferirUnaria(unaria);
        } else if (expr instanceof NodoExpr.AccesoArray acceso) {
            return inferirAccesoArray(acceso);
        } else if (expr instanceof NodoExpr.AccesoAtributo acceso) {
            return inferirAccesoAtributo(acceso);
        } else if (expr instanceof NodoExpr.LlamadaFuncion llamada) {
            return inferirLlamadaFuncion(llamada);
        } else if (expr instanceof NodoExpr.LiteralStruct) {
            return "struct";
        } else {
            return "desconocido";
        }
    }

    private String inferirBinaria(NodoExpr.Binaria bin) {
        String tipoIzq = inferirTipo(bin.izquierda());
        String tipoDer = inferirTipo(bin.derecha());
        String op = bin.operador();

        // Textum solo se concatena con +
        if ("textum".equals(tipoIzq) || "textum".equals(tipoDer)) {
            if (!"+".equals(op)) {
                errores.add(new ErrorSemantico(bin.linea(),
                        "Textum solo se puede concatenar con +"));
                return "textum";
            }
            return "textum";
        }

        // Jerarquía de tipos para operaciones aritméticas
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) {
            int nivelIzq = JERARQUIA.getOrDefault(tipoIzq, 0);
            int nivelDer = JERARQUIA.getOrDefault(tipoDer, 0);
            if (nivelIzq == 0 || nivelDer == 0) {
                errores.add(new ErrorSemantico(bin.linea(),
                        "Tipo no soportado en operación aritmética: " + tipoIzq + " " + op + " " + tipoDer));
                return "desconocido";
            }
            // Retorna el tipo de mayor jerarquía
            return nivelIzq >= nivelDer ? tipoIzq : tipoDer;
        }

        // Operaciones relacionales y lógicas retornan booleano
        return "booleano";
    }

    private String inferirUnaria(NodoExpr.Unaria unaria) {
        String tipo = inferirTipo(unaria.operando());
        if ("non".equals(unaria.operador())) {
            if (!"booleano".equals(tipo)) {
                errores.add(new ErrorSemantico(unaria.linea(),
                        "non solo aplica a booleanos, pero es: " + tipo));
            }
            return "booleano";
        }
        // ++ y -- solo aplican a numerus
        if (!"numerus".equals(tipo)) {
            errores.add(new ErrorSemantico(unaria.linea(),
                    "Incremento/decremento solo aplica a numerus, pero es: " + tipo));
        }
        return "numerus";
    }

    private String inferirAccesoArray(NodoExpr.AccesoArray acceso) {
        String tipoArreglo = inferirTipo(acceso.arreglo());
        String tipoIndice = inferirTipo(acceso.indice());
        if (!"numerus".equals(tipoIndice) && !"desconocido".equals(tipoIndice)) {
            errores.add(new ErrorSemantico(acceso.linea(),
                    "Índice de arreglo debe ser numerus, pero es: " + tipoIndice));
        }
        return tipoArreglo; // El tipo del arreglo es el tipo de sus elementos
    }

    private String inferirAccesoAtributo(NodoExpr.AccesoAtributo acceso) {
        // Validación de structs se hará en ValidadorEstructuras
        return "desconocido";
    }

    private String inferirLlamadaFuncion(NodoExpr.LlamadaFuncion llamada) {
        var opt = tabla.buscarFuncion(llamada.nombre());
        if (opt.isEmpty()) {
            errores.add(new ErrorSemantico(llamada.linea(),
                    "Función no declarada: " + llamada.nombre()));
            return "desconocido";
        }
        var func = opt.get();
        var params = func.tipoParametros();
        var args = llamada.argumentos();

        if (params.size() != args.size()) {
            errores.add(new ErrorSemantico(llamada.linea(),
                    "Número incorrecto de argumentos para " + llamada.nombre() +
                            ". Esperaba " + params.size() + ", recibió " + args.size()));
        }

        for (int i = 0; i < Math.min(params.size(), args.size()); i++) {
            String tipoArg = inferirTipo(args.get(i));
            String tipoParam = params.get(i);
            if (!sonCompatibles(tipoArg, tipoParam)) {
                errores.add(new ErrorSemantico(args.get(i).linea(),
                        "Tipo de argumento incompatible. Esperaba " + tipoParam +
                                ", pero es " + tipoArg));
            }
        }

        return func.tipoRetorno();
    }

    private boolean sonCompatibles(String tipo1, String tipo2) {
        if (tipo1.equals(tipo2)) return true;
        // Regla de jerarquía para asignación: tipo1 debe ser subtipo de tipo2
        int nivel1 = JERARQUIA.getOrDefault(tipo1, 0);
        int nivel2 = JERARQUIA.getOrDefault(tipo2, 0);
        return nivel1 <= nivel2; // El tipo de menor jerarquía puede asignarse al de mayor
    }
}