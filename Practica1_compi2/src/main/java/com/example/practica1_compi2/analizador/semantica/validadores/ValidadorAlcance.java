package com.example.practica1_compi2.analizador.semantica.validadores;

import com.example.practica1_compi2.analizador.ast.NodoExpr;
import com.example.practica1_compi2.analizador.semantica.TablaSimbolos;
import com.example.practica1_compi2.analizador.semantica.errores.ErrorSemantico;

import java.util.List;

public class ValidadorAlcance {

    private final TablaSimbolos tabla;
    private final List<ErrorSemantico> errores;

    public ValidadorAlcance(TablaSimbolos tabla, List<ErrorSemantico> errores) {
        this.tabla = tabla;
        this.errores = errores;
    }

    public void validar(NodoExpr expresion) {
        if (expresion instanceof NodoExpr.Identificador id) {
            validarIdentificador(id);
        } else if (expresion instanceof NodoExpr.AccesoArray acceso) {
            validar(acceso.arreglo());
            validar(acceso.indice());
        } else if (expresion instanceof NodoExpr.AccesoAtributo acceso) {
            validar(acceso.objeto());
        } else if (expresion instanceof NodoExpr.Binaria bin) {
            validar(bin.izquierda());
            validar(bin.derecha());
        } else if (expresion instanceof NodoExpr.Unaria unaria) {
            validar(unaria.operando());
        } else if (expresion instanceof NodoExpr.LlamadaFuncion llamada) {
            for (NodoExpr arg : llamada.argumentos()) {
                validar(arg);
            }
        }
    }

    private void validarIdentificador(NodoExpr.Identificador id) {
        if (tabla.buscarVariable(id.nombre()).isEmpty()) {
            errores.add(new ErrorSemantico(id.linea(), "Variable no declarada: " + id.nombre()));
        }
    }
}
