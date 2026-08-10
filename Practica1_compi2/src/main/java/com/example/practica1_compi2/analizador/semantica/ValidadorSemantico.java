package com.example.practica1_compi2.analizador.semantica;

import com.example.practica1_compi2.analizador.ast.NodoExpr;
import com.example.practica1_compi2.analizador.ast.NodoFuncion;
import com.example.practica1_compi2.analizador.ast.NodoPrograma;
import com.example.practica1_compi2.analizador.ast.NodoSentencia;
import com.example.practica1_compi2.analizador.semantica.errores.ErrorSemantico;
import com.example.practica1_compi2.analizador.semantica.validadores.*;

import java.util.ArrayList;
import java.util.List;

public class ValidadorSemantico {

    private final TablaSimbolos tabla;
    private final List<ErrorSemantico> errores;
    private final ValidadorDeclaraciones validadorDeclaraciones;
    private final ValidadorAlcance validadorAlcance;
    private final ValidadorTipos validadorTipos;
    private final ValidadorEstructuras validadorEstructuras;
    private final ValidadorFlujo validadorFlujo;

    public ValidadorSemantico() {
        this.tabla = new TablaSimbolos();
        this.errores = new ArrayList<>();
        this.validadorDeclaraciones = new ValidadorDeclaraciones(tabla, errores);
        this.validadorAlcance = new ValidadorAlcance(tabla, errores);
        this.validadorTipos = new ValidadorTipos(tabla, errores);
        this.validadorEstructuras = new ValidadorEstructuras(tabla, errores, validadorTipos);
        this.validadorFlujo = new ValidadorFlujo(errores);
    }

    public List<ErrorSemantico> validar(NodoPrograma programa) {
        // 1. Validar variables globales
        for (NodoSentencia sentencia : programa.variablesGlobales()) {
            validarDeclaracion(sentencia);
        }

        // 2. Validar funciones (declaraciones y cuerpos)
        for (NodoFuncion funcion : programa.funciones()) {
            validarFuncion(funcion);
        }

        // 3. Validar main
        validarMain(programa.main());

        return errores;
    }

    private void validarDeclaracion(NodoSentencia sentencia) {
        validadorDeclaraciones.validar(sentencia);
        if (sentencia instanceof NodoSentencia.InstanciaStruct) {
            validadorEstructuras.validar(sentencia);
        }
        // Validar expresión de inicialización en declaraciones de variable
        if (sentencia instanceof NodoSentencia.DeclaracionVariable varDecl) {
            if (varDecl.valorInicial() != null) {
                validarExpresion(varDecl.valorInicial());
            }
        }
    }

    private void validarFuncion(NodoFuncion funcion) {
        // Registrar función en tabla
        List<String> tiposParametros = new ArrayList<>();
        for (NodoFuncion.Parametro p : funcion.parametros()) {
            tiposParametros.add(p.tipo());
        }
        boolean ok = tabla.declararFunciones(funcion.nombre(), tiposParametros, funcion.tipoRetorno());
        if (!ok) {
            errores.add(new ErrorSemantico(funcion.linea(),
                    "Función ya declarada: " + funcion.nombre()));
            return;
        }

        // Entrar a nuevo scope para la función
        tabla.entrarScope();

        // Declarar parámetros como variables locales
        for (NodoFuncion.Parametro p : funcion.parametros()) {
            tabla.declararVariable(p.nombre(), p.tipo());
        }

        // Validar variables locales
        for (NodoSentencia sentencia : funcion.variablesLocales()) {
            validarDeclaracion(sentencia);
        }

        // Validar cuerpo de la función
        validadorFlujo.entrarFuncion(funcion.tipoRetorno());
        validarSentencias(funcion.cuerpo());
        validadorFlujo.salirFuncion();

        // Salir del scope de la función
        tabla.salirScope();
    }

    private void validarMain(List<NodoSentencia> main) {
        tabla.entrarScope();
        validadorFlujo.entrarFuncion(null); // main no retorna valor
        validarSentencias(main);
        validadorFlujo.salirFuncion();
        tabla.salirScope();
    }

    private void validarSentencias(List<NodoSentencia> sentencias) {
        for (NodoSentencia sentencia : sentencias) {
            validarSentencia(sentencia);
        }
    }

    private void validarSentencia(NodoSentencia sentencia) {
        // 1. Validar declaraciones
        if (sentencia instanceof NodoSentencia.DeclaracionVariable ||
                sentencia instanceof NodoSentencia.DeclaracionArreglo ||
                sentencia instanceof NodoSentencia.DefinicionStruct ||
                sentencia instanceof NodoSentencia.InstanciaStruct) {
            validarDeclaracion(sentencia);
            return;
        }

        // 2. Validar flujo (retorno, break, continue)
        validadorFlujo.validar(sentencia);

        // 3. Validar expresiones (alcance y tipos)
        if (sentencia instanceof NodoSentencia.Asignacion asignacion) {
            validarAsignacion(asignacion);
        } else if (sentencia instanceof NodoSentencia.AsignacionStructLiteral asignacionStruct) {
            validarAsignacionStructLiteral(asignacionStruct);
        } else if (sentencia instanceof NodoSentencia.Escritura escritura) {
            validarEscritura(escritura);
        } else if (sentencia instanceof NodoSentencia.Lectura lectura) {
            validarLectura(lectura);
        } else if (sentencia instanceof NodoSentencia.Retorno retorno) {
            validarRetorno(retorno);
        } else if (sentencia instanceof NodoSentencia.CicloDum ciclo) {
            validarCicloDum(ciclo);
        } else if (sentencia instanceof NodoSentencia.CicloFacere ciclo) {
            validarCicloFacere(ciclo);
        } else if (sentencia instanceof NodoSentencia.CicloPer ciclo) {
            validarCicloPer(ciclo);
        } else if (sentencia instanceof NodoSentencia.Condicional condicional) {
            validarCondicional(condicional);
        } else if (sentencia instanceof NodoSentencia.LlamadaFuncionSentencia llamada) {
            validarLlamadaFuncion(llamada.llamada());
        }
    }

    private void validarAsignacion(NodoSentencia.Asignacion asignacion) {
        validarExpresion(asignacion.referencia());
        validarExpresion(asignacion.valor());
        String tipoRef = validadorTipos.inferirTipo(asignacion.referencia());
        String tipoVal = validadorTipos.inferirTipo(asignacion.valor());

        // Verificar compatibilidad
        if (!"desconocido".equals(tipoRef) && !"desconocido".equals(tipoVal)
                && !"struct".equals(tipoRef) && !"struct".equals(tipoVal)) {
            if (!validadorTipos.sonCompatibles(tipoVal, tipoRef)) {
                errores.add(new ErrorSemantico(asignacion.linea(),
                        "Tipo incompatible en asignación: " + tipoVal + " no es compatible con " + tipoRef));
            }
        }
    }

    private void validarAsignacionStructLiteral(NodoSentencia.AsignacionStructLiteral asignacion) {
        validarExpresion(asignacion.referencia());
        // El literal struct se valida en ValidadorEstructuras
    }

    private void validarEscritura(NodoSentencia.Escritura escritura) {
        for (NodoExpr expr : escritura.valores()) {
            validarExpresion(expr);
        }
    }

    private void validarLectura(NodoSentencia.Lectura lectura) {
        if (lectura.variable() != null) {
            var opt = tabla.buscarVariable(lectura.variable());
            if (opt.isEmpty()) {
                errores.add(new ErrorSemantico(lectura.linea(),
                        "Variable no declarada: " + lectura.variable()));
            }
        }
    }

    private void validarRetorno(NodoSentencia.Retorno retorno) {
        validarExpresion(retorno.valor());
        // El tipo de retorno se valida en ValidadorFlujo
    }

    private void validarCicloDum(NodoSentencia.CicloDum ciclo) {
        validarExpresion(ciclo.condicion());
        validadorFlujo.entrarCiclo();
        tabla.entrarScope();
        validarSentencias(ciclo.cuerpo());
        tabla.salirScope();
        validadorFlujo.salirCiclo();
    }

    private void validarCicloFacere(NodoSentencia.CicloFacere ciclo) {
        validadorFlujo.entrarCiclo();
        tabla.entrarScope();
        validarSentencias(ciclo.cuerpo());
        tabla.salirScope();
        validarExpresion(ciclo.condicion());
        validadorFlujo.salirCiclo();
    }

    private void validarCicloPer(NodoSentencia.CicloPer ciclo) {
        // Validar inicialización
        validarDeclaracion(ciclo.inicializacion());
        validarExpresion(ciclo.condicion());
        validarSentencia(ciclo.incremento());
        validadorFlujo.entrarCiclo();
        tabla.entrarScope();
        validarSentencias(ciclo.cuerpo());
        tabla.salirScope();
        validadorFlujo.salirCiclo();
    }

    private void validarCondicional(NodoSentencia.Condicional condicional) {
        for (NodoSentencia.Rama rama : condicional.ramas()) {
            validarExpresion(rama.condicion());
            tabla.entrarScope();
            validarSentencias(rama.cuerpo());
            tabla.salirScope();
        }
        if (condicional.elseCuerpo() != null) {
            tabla.entrarScope();
            validarSentencias(condicional.elseCuerpo());
            tabla.salirScope();
        }
    }

    private void validarLlamadaFuncion(NodoExpr.LlamadaFuncion llamada) {
        for (NodoExpr arg : llamada.argumentos()) {
            validarExpresion(arg);
        }
        // La validación de tipos de la función se hace en ValidadorTipos
        validadorTipos.inferirTipo(llamada);
    }

    private void validarExpresion(NodoExpr expr) {
        System.out.println("🔍 validarExpresion: " + expr.getClass().getSimpleName());
        // Validar alcance
        validadorAlcance.validar(expr);
        // Validar tipos
        validadorTipos.inferirTipo(expr);
        // Validar acceso a structs
        if (expr instanceof NodoExpr.AccesoAtributo acceso) {
            validadorEstructuras.validarAccesoAtributo(acceso);
        }
        // Recursivamente validar subexpresiones
        if (expr instanceof NodoExpr.Binaria bin) {
            validarExpresion(bin.izquierda());
            validarExpresion(bin.derecha());
        } else if (expr instanceof NodoExpr.Unaria unaria) {
            validarExpresion(unaria.operando());
        } else if (expr instanceof NodoExpr.AccesoArray acceso) {
            validarExpresion(acceso.arreglo());
            validarExpresion(acceso.indice());
        } else if (expr instanceof NodoExpr.AccesoAtributo acceso) {
            validarExpresion(acceso.objeto());
        } else if (expr instanceof NodoExpr.LlamadaFuncion llamada) {
            for (NodoExpr arg : llamada.argumentos()) {
                validarExpresion(arg);
            }
        }
    }

    public boolean hayErrores() {
        return !errores.isEmpty();
    }

    public List<ErrorSemantico> getErrores() {
        return errores;
    }

    public TablaSimbolos getTabla() {
        return tabla;
    }
}