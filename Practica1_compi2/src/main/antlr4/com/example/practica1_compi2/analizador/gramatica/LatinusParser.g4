parser grammar LatinusParser;

options { tokenVocab = LatinusLexer; }

// ===== Programa =====

programa
    : seccionVariables? seccionFunciones? seccionMain FIN_PROGRAMA PUNTOCOMA EOF
    ;

seccionVariables
    : VARIABILES MAYOR declaracionVar*
    ;

seccionFunciones
    : MUNERA MAYOR funcion*
    ;

seccionMain
    : MAIOR MAYOR sentencia*
    ;

// ===== Declaraciones (variables, arreglos, structs) =====

declaracionVar
    : variable
    | arreglo
    | structDef
    | structInstancia
    ;

variable
    : ESTO ID DOSPUNTOS tipoPrimitivo expr? PUNTOCOMA      # variablePrimitiva
    | ESTO ID DOSPUNTOS (VERUM | FALSUS) PUNTOCOMA          # variableBooleana
    ;

tipoPrimitivo
    : NUMERUS | TEXTUM | DECIMALIS | LITTERA
    ;

tipo
    : tipoPrimitivo
    | ID   // nombre de una estructura definida por el usuario
    ;

arreglo
    : SERIES ID CORCH_A INT CORCH_C DOSPUNTOS tipo (LLAVE_A listaExpr LLAVE_C)? PUNTOCOMA  # arregloTipado
    | SERIES ID CORCH_A INT CORCH_C DOSPUNTOS LLAVE_A listaExpr LLAVE_C PUNTOCOMA          # arregloBooleano
    ;

listaExpr
    : expr (COMA expr)*
    ;

structDef
    : STRUCTURA ID LLAVE_A campoStruct* LLAVE_C FINIS PUNTOCOMA
    ;

campoStruct
    : ESTO ID DOSPUNTOS tipo PUNTOCOMA
    ;

structInstancia
    : ESTO ID DOSPUNTOS ID literalStruct
    ;

literalStruct
    : LLAVE_A asignacionCampo (COMA asignacionCampo)* LLAVE_C
    ;

asignacionCampo
    : ID DOSPUNTOS expr
    ;

// ===== Funciones =====

funcion
    : ACTIO ID PAR_A listaParametros? PAR_C
      LLAVE_A bloqueVariables? sentencia* LLAVE_C FINIS PUNTOCOMA           # funcionSinRetorno
    | RATIO tipo ID PAR_A listaParametros? PAR_C
      LLAVE_A bloqueVariables? sentencia* LLAVE_C FINIS PUNTOCOMA           # funcionConRetorno
    ;

listaParametros
    : parametro (COMA parametro)*
    ;

parametro
    : ESTO ID DOSPUNTOS tipo
    ;

bloqueVariables
    : VARIABILES CORCH_A declaracionVar* CORCH_C
    ;

// ===== Sentencias =====

sentencia
    : declaracionVar
    | asignacion
    | asignacionStructLiteral
    | condicional
    | cicloDum
    | cicloFacere
    | cicloPer
    | retorno
    | lectura
    | escritura
    | interrupcionCiclo
    | llamadaFuncion PUNTOCOMA
    ;

asignacion
    : referencia ASIGNAR expr PUNTOCOMA
    ;

// Caso especial: asignar un literal de struct anónimo a un atributo/elemento
// existente (ej. mi_selva.animales[1] = { nombre: "Perro", apodo: "Canis" })
// termina en '}', no lleva ';'
asignacionStructLiteral
    : referencia ASIGNAR literalStruct
    ;

referencia
    : ID (PUNTO ID)* (CORCH_A expr CORCH_C)?
    ;

condicional
    : SI PAR_A expr PAR_C bloqueSentencias
      ramaAliter*
      ramaElse?
      FINIS PUNTOCOMA
    ;

bloqueSentencias
    : LLAVE_A sentencia* LLAVE_C
    ;

ramaAliter
    : ALITER PAR_A expr PAR_C bloqueSentencias
    ;

ramaElse
    : ALITER bloqueSentencias
    ;

cicloDum
    : DUM PAR_A expr PAR_C LLAVE_A sentencia* LLAVE_C FINIS PUNTOCOMA
    ;

cicloFacere
    : FACERE LLAVE_A sentencia* LLAVE_C DUM PAR_A expr PAR_C PUNTOCOMA
    ;

cicloPer
    : PER PAR_A variable expr PUNTOCOMA incremento PAR_C LLAVE_A sentencia* LLAVE_C
    ;

incremento
    : ID (INC | DEC)
    | referencia ASIGNAR expr
    ;

interrupcionCiclo
    : (PERGE | INTERRUMPE) PUNTOCOMA
    ;

retorno
    : REDDERE expr PUNTOCOMA
    ;

lectura
    : ID? LEER
    ;

escritura
    : ESCRIBIR expr (ESCRIBIR expr)* PUNTOCOMA
    ;

llamadaFuncion
    : ID PAR_A listaArgumentos? PAR_C
    ;

listaArgumentos
    : expr (COMA expr)*
    ;

// ===== Expresiones =====
// Orden de alternativas = precedencia (primero = más fuerte).
// ANTLR4 resuelve la recursión izquierda automáticamente con este orden.

expr
    : PAR_A expr PAR_C                                # exprParentesis
    | (INC | DEC) ID                                  # exprIncDecPrefijo
    | ID (INC | DEC)                                  # exprIncDecPostfijo
    | NON expr                                        # exprNegacion
    | expr op=(MULT | DIV) expr                       # exprMulDiv
    | expr op=(MAS | MENOS) expr                      # exprSumaResta
    | expr op=(MENOR | MAYOR | MENIG | MAYIG) expr    # exprRelacional
    | expr op=(IGUAL | DISTINTO) expr                 # exprIgualdad
    | expr AND expr                                   # exprAnd
    | expr OR expr                                    # exprOr
    | llamadaFuncion                                  # exprLlamada
    | referencia                                      # exprReferencia
    | INT                                             # exprEntero
    | FLOAT                                           # exprDecimal
    | STRING                                          # exprTexto
    | CHAR                                            # exprCaracter
    | VERUM                                           # exprVerum
    | FALSUS                                          # exprFalsus
    ;