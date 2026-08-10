package com.example.practica1_compi2.analizador.semantica.validadores;

import com.example.practica1_compi2.analizador.ast.NodoSentencia;
import com.example.practica1_compi2.analizador.semantica.errores.ErrorSemantico;

import java.util.List;

public class ValidadorFlujo {

    private final List<ErrorSemantico> errores;
    private boolean dentroDeCiclo = false;
    private boolean dentroDeFuncion = false;
    private String tipoRetornoEsperado = null;

    public ValidadorFlujo(List<ErrorSemantico> errores) {
        this.errores = errores;
    }

    public void entrarCiclo() {
        dentroDeCiclo = true;
    }

    public void salirCiclo(){
        dentroDeCiclo = false;
    }

    public void entrarFuncion(String tipoRetorno) {
        dentroDeFuncion = true;
        this.tipoRetornoEsperado = tipoRetorno;
    }

    public void salirFuncion(){
        dentroDeFuncion = false;
        this.tipoRetornoEsperado = null;
    }

    public void validar(NodoSentencia sentencia) {
        if (sentencia instanceof NodoSentencia.InterrupcionCiclo interrupcion) {
            validarInterrupcion(interrupcion);
        } else if (sentencia instanceof NodoSentencia.Retorno retorno) {
            validarRetorno(retorno);
        }
    }

    private void validarInterrupcion(NodoSentencia.InterrupcionCiclo interrupcion) {
        if (!dentroDeCiclo) {
            errores.add(new ErrorSemantico(interrupcion.linea(), interrupcion.tipo() + " solo puede usarse dentro de un ciclo"));
        }
    }

    private void validarRetorno(NodoSentencia.Retorno retorno) {
        if (!dentroDeFuncion) {
            errores.add(new ErrorSemantico(retorno.linea(), "retorno solo puede usarse dentro de una funcion"));
            return;
        }

        //si el tipoRetornoEsperado es null, la funcion no retorna valir
        if (tipoRetornoEsperado == null) {
            errores.add(new ErrorSemantico(retorno.linea(), "Esta funcion no retorna valor (actio)"));
        }
    }
}
