package com.example.piglatin.analizador.semantica.validadores;

import com.example.piglatin.analizador.ast.NodoExpr;
import com.example.piglatin.analizador.ast.nodo.TipoNodoExpr;
import com.example.piglatin.analizador.semantica.TablaSimbolos;
import com.example.piglatin.analizador.semantica.errores.ErrorSemantico;

import java.util.List;

public class ValidadorAlcance {

    private final TablaSimbolos tabla;
    private final List<ErrorSemantico> errores;

    public ValidadorAlcance(TablaSimbolos tabla, List<ErrorSemantico> errores) {
        this.tabla = tabla;
        this.errores = errores;
    }

    public void validar(NodoExpr expresion) {

        if (expresion.tipoNodo() == TipoNodoExpr.IDENTIFICADOR) {

            NodoExpr.Identificador id =
                    (NodoExpr.Identificador) expresion;

            validarIdentificador(id);

        } else if (expresion.tipoNodo() == TipoNodoExpr.ACCESO_ARRAY) {

            NodoExpr.AccesoArray acceso =
                    (NodoExpr.AccesoArray) expresion;

            validar(acceso.arreglo());
            validar(acceso.indice());

        } else if (expresion.tipoNodo() == TipoNodoExpr.ACCESO_ATRIBUTO) {

            NodoExpr.AccesoAtributo acceso =
                    (NodoExpr.AccesoAtributo) expresion;

            validar(acceso.objeto());

        } else if (expresion.tipoNodo() == TipoNodoExpr.BINARIA) {

            NodoExpr.Binaria bin =
                    (NodoExpr.Binaria) expresion;

            validar(bin.izquierda());
            validar(bin.derecha());

        } else if (expresion.tipoNodo() == TipoNodoExpr.UNARIA) {

            NodoExpr.Unaria unaria =
                    (NodoExpr.Unaria) expresion;

            validar(unaria.operando());

        } else if (expresion.tipoNodo() == TipoNodoExpr.LLAMADA_FUNCION) {

            NodoExpr.LlamadaFuncion llamada =
                    (NodoExpr.LlamadaFuncion) expresion;

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
