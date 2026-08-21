lexer grammar LatinusLexer;

// ===== Comentarios y espacios en blanco regresar a la version anterio +  \u200B =====
LINEA_COMENTARIO   : '//' ~[\r\n]* -> channel(HIDDEN) ;
BLOQUE_COMENTARIO  : '##' .*? '##' -> channel(HIDDEN) ;
WS                 : [ \t\r\n\u200B]+ -> channel(HIDDEN) ;

// ===== Secciones del programa (deben ir ANTES de ID) =====
VARIABILES   : 'VARIABILES' ;
MUNERA       : 'MUNERA' ;
MAIOR        : 'MAIOR' ;
FIN_PROGRAMA : 'FINIS' ;   // 'FINIS' mayuscula -> cierra TODO el programa

// ===== Palabras reservadas (deben ir ANTES de ID) =====
ESTO         : 'esto' ;
SERIES       : 'series' ;
STRUCTURA    : 'structura' ;
FINIS        : 'finis' ;   // 'finis' minuscula -> cierra un bloque
SI           : 'si' ;
ALITER       : 'aliter' ;
DUM          : 'dum' ;
FACERE       : 'facere' ;
PER          : 'per' ;
PERGE        : 'perge' ;
INTERRUMPE   : 'interrumpe' ;
ACTIO        : 'actio' ;
RATIO        : 'ratio' ;
REDDERE      : 'reddere' ;
NON          : 'non' ;

// ===== Tipos primitivos =====
NUMERUS   : 'numerus' ;
TEXTUM    : 'textum' ;
DECIMALIS : 'decimalis' ;
LITTERA   : 'littera' ;
VERUM     : 'verum' ;
FALSUS    : 'falsus' ;

// ===== Operadores de dos caracteres (deben ir ANTES que los de uno) =====
LEER      : '<<' ;
ESCRIBIR  : '>>' ;
INC       : '++' ;
DEC       : '--' ;
IGUAL     : '==' ;
DISTINTO  : '!=' ;
MENIG     : '<=' ;
MAYIG     : '>=' ;
AND       : '&&' ;
OR        : '||' ;

// ===== Operadores y simbolos de un caracter =====
ASIGNAR    : '=' ;
MAS        : '+' ;
MENOS      : '-' ;
MULT       : '*' ;
DIV        : '/' ;
MENOR      : '<' ;
MAYOR      : '>' ;
DOSPUNTOS  : ':' ;
PUNTOCOMA  : ';' ;
COMA       : ',' ;
PUNTO      : '.' ;
LLAVE_A    : '{' ;
LLAVE_C    : '}' ;
CORCH_A    : '[' ;
CORCH_C    : ']' ;
PAR_A      : '(' ;
PAR_C      : ')' ;

// ===== Literales =====
FLOAT   : [0-9]+ '.' [0-9]+ ;
INT     : [0-9]+ ;
STRING  : '"' (~["\r\n])* '"' ;
CHAR    : '\'' . '\'' ;

// ID va de ultimo: si el texto coincide con una palabra reservada de arriba,
ID : [a-zA-Z_][a-zA-Z0-9_]* ;