package com.example.piglatin.analizador.ast;


import com.example.piglatin.analizador.ast.nodo.TipoNodoSentencia;

import java.util.List;
import java.util.Map;


public sealed interface NodoSentencia extends NodoAST {

    TipoNodoSentencia tipoNodo();

    // tipo: "numerus" | "textum" | "decimalis" | "littera" | "booleano"
    record DeclaracionVariable(int linea, String nombre, String tipo, NodoExpr valorInicial) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.DECLARACION_VARIABLE;
        }
    }

    record DeclaracionArreglo(int linea, String nombre, int tamano, String tipo, List<NodoExpr> valoresIniciales) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.DECLARACION_ARREGLO;
        }
    }

    record DefinicionStruct(int linea, String nombre, List<CampoStruct> campos) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.DEFINICION_STRUCT;
        }
    }

    // esto <nombre> : <tipoStruct> { campo: expr, ... }
    record InstanciaStruct(int linea, String nombre, String tipoStruct, Map<String, NodoExpr> valores) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.INSTANCIA_STRUCT;
        }
    }

    record Asignacion(int linea, NodoExpr referencia, NodoExpr valor) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.ASIGNACION;
        }
    }

    // referencia = { campo: expr, ... }  (sin ';', confirmado con el auxiliar)
    record AsignacionStructLiteral(int linea, NodoExpr referencia, NodoExpr.LiteralStruct valor) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.ASIGNACION_STRUCT_LITERAL;
        }
    }

    record Condicional(int linea, List<Rama> ramas, List<NodoSentencia> elseCuerpo) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.CONDICIONAL;
        }
    }

    record CicloDum(int linea, NodoExpr condicion, List<NodoSentencia> cuerpo) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.CICLO_DUM;
        }
    }
    record CicloFacere(int linea, List<NodoSentencia> cuerpo, NodoExpr condicion) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.CICLO_FACERE;
        }
    }

    // incremento desazucarado siempre a un NodoSentencia.Asignacion (ver ASTBuilder)
    record CicloPer(int linea, DeclaracionVariable inicializacion, NodoExpr condicion,
                    NodoSentencia incremento, List<NodoSentencia> cuerpo) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.CICLO_PER;
        }
    }

    record Retorno(int linea, NodoExpr valor) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.RETORNO;
        }
    }

    // variable == null  =>  '<<' sin capturar valor (solo lee y descarta)
    record Lectura(int linea, String variable) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.LECTURA;
        }
    }

    record Escritura(int linea, List<NodoExpr> valores) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.ESCRITURA;
        }
    }

    // tipo: "perge" | "interrumpe"
    record InterrupcionCiclo(int linea, String tipo) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.INTERRUPCION_CICLO;
        }
    }

    record LlamadaFuncionSentencia(int linea, NodoExpr.LlamadaFuncion llamada) implements NodoSentencia {
        @Override
        public TipoNodoSentencia tipoNodo(){
            return TipoNodoSentencia.LLAMADA_FUNCION_SENTENCIA;
        }
    }

    // ---- Tipos auxiliares (no son NodoAST, no tienen numero de linea propio) ----

    record CampoStruct(String nombre, String tipo) {}

    record Rama(NodoExpr condicion, List<NodoSentencia> cuerpo) {}
}