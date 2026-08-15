package com.example.piglatin.analizador.semantica.validadores;

import com.example.piglatin.analizador.ast.NodoSentencia;
import com.example.piglatin.analizador.ast.nodo.TipoNodoSentencia;
import com.example.piglatin.analizador.semantica.TablaSimbolos;
import com.example.piglatin.analizador.semantica.errores.ErrorSemantico;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValidadorDeclaraciones {

    private final TablaSimbolos tabla;
    private final List<ErrorSemantico> errores;

    public ValidadorDeclaraciones(TablaSimbolos tabla, List<ErrorSemantico> errores) {
        this.tabla = tabla;
        this.errores = errores;
    }

    public void validar(NodoSentencia sentencia) {
        if (sentencia.tipoNodo() == TipoNodoSentencia.DECLARACION_VARIABLE) {
            validarDeclaracionVariable((NodoSentencia.DeclaracionVariable) sentencia);
        } else if (sentencia.tipoNodo() == TipoNodoSentencia.DECLARACION_ARREGLO) {
            validarDeclaracionArreglo((NodoSentencia.DeclaracionArreglo) sentencia);
        } else if (sentencia.tipoNodo() == TipoNodoSentencia.DEFINICION_STRUCT) {
            validarDefinicionStruct((NodoSentencia.DefinicionStruct) sentencia);
        }
    }

    private void validarDeclaracionVariable(NodoSentencia.DeclaracionVariable var) {
        boolean ok = tabla.declararVariable(var.nombre(), var.tipo());
        if (!ok) {
            errores.add(new ErrorSemantico(var.linea(), "Variable ya declarada: " + var.nombre()));
        }
    }

    private void validarDeclaracionArreglo(NodoSentencia.DeclaracionArreglo arr) {
        boolean ok = tabla.declararVariable(arr.nombre(), arr.tipo(), true, arr.tamano());
        if (!ok) {
            errores.add(new ErrorSemantico(arr.linea(), "Arreglo ya declarado: " + arr.nombre()));
        }
    }

    private void validarDefinicionStruct(NodoSentencia.DefinicionStruct struct) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (NodoSentencia.CampoStruct campo : struct.campos()) {
            if (campos.containsKey(campo.nombre())) {
                errores.add(new ErrorSemantico(struct.linea(), "Campo duplicado en struct: " + campo.nombre()));
            }
            campos.put(campo.nombre(), campo.tipo());
        }
        boolean ok = tabla.declararStruct(struct.nombre(), campos);
        if (!ok) {
            errores.add(new ErrorSemantico(struct.linea(), "Struct ya declarado: " + struct.nombre()));
        }
    }
}
