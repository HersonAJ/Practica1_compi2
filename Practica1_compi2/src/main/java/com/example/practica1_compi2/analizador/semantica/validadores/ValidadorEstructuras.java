package com.example.practica1_compi2.analizador.semantica.validadores;

import com.example.practica1_compi2.analizador.ast.NodoExpr;
import com.example.practica1_compi2.analizador.ast.NodoSentencia;
import com.example.practica1_compi2.analizador.semantica.TablaSimbolos;
import com.example.practica1_compi2.analizador.semantica.errores.ErrorSemantico;

import java.util.List;
import java.util.Map;

public class ValidadorEstructuras {

    private final TablaSimbolos tabla;
    private final List<ErrorSemantico> errores;
    private final ValidadorTipos validadorTipos;

    public ValidadorEstructuras(TablaSimbolos tabla, List<ErrorSemantico> errores, ValidadorTipos validadorTipos) {
        this.tabla = tabla;
        this.errores = errores;
        this.validadorTipos = validadorTipos;
    }

    public void validar(NodoSentencia sentencia) {
        if (sentencia instanceof NodoSentencia.InstanciaStruct instancia) {
            validarInstanciaStruct(instancia);
        }
    }

    public void validarAccesoAtributo(NodoExpr.AccesoAtributo acceso) {
        String tipoObjeto = validadorTipos.inferirTipo(acceso.objeto());
        var optStruct = tabla.buscarStruct(tipoObjeto);

        if (optStruct.isEmpty()) {
            errores.add(new ErrorSemantico(acceso.linea(),
                    "Tipo no es una estructura: " + tipoObjeto));
            return;
        }

        TablaSimbolos.DefinicionStruct struct = optStruct.get();
        if (!struct.campos().containsKey(acceso.atributo())) {
            errores.add(new ErrorSemantico(acceso.linea(),
                    "La estructura " + tipoObjeto + " no tiene el campo: " + acceso.atributo()));
        }
    }

    private void validarInstanciaStruct(NodoSentencia.InstanciaStruct instancia) {
        var optStruct = tabla.buscarStruct(instancia.tipoStruct());

        if (optStruct.isEmpty()) {
            errores.add(new ErrorSemantico(instancia.linea(),
                    "Struct no declarado: " + instancia.tipoStruct()));
            return;
        }

        TablaSimbolos.DefinicionStruct struct = optStruct.get();
        Map<String, String> camposEsperados = struct.campos();
        Map<String, NodoExpr> valores = instancia.valores();

        // Validar que todos los campos estén presentes
        for (String campo : camposEsperados.keySet()) {
            if (!valores.containsKey(campo)) {
                errores.add(new ErrorSemantico(instancia.linea(),
                        "Falta el campo '" + campo + "' en la instancia de " + instancia.tipoStruct()));
            }
        }

        // Validar que no haya campos extra
        for (String campo : valores.keySet()) {
            if (!camposEsperados.containsKey(campo)) {
                errores.add(new ErrorSemantico(instancia.linea(),
                        "Campo desconocido '" + campo + "' en la instancia de " + instancia.tipoStruct()));
            }
        }

        // Validar tipos de cada campo
        for (Map.Entry<String, NodoExpr> entry : valores.entrySet()) {
            String campo = entry.getKey();
            NodoExpr valor = entry.getValue();
            String tipoEsperado = camposEsperados.get(campo);
            String tipoReal = validadorTipos.inferirTipo(valor);

            if (!tipoReal.equals(tipoEsperado) && !"desconocido".equals(tipoReal)) {
                errores.add(new ErrorSemantico(valor.linea(),
                        "Tipo incorrecto para el campo '" + campo + "'. Esperaba " +
                                tipoEsperado + ", pero es " + tipoReal));
            }
        }

        // Declarar la variable con el tipo struct
        boolean ok = tabla.declararVariable(instancia.nombre(), instancia.tipoStruct());
        if (!ok) {
            errores.add(new ErrorSemantico(instancia.linea(),
                    "Variable ya declarada: " + instancia.nombre()));
        }
    }
}