package com.example.practica1_compi2.analizador.builder;

import com.example.practica1_compi2.analizador.ast.NodoPrograma;
import com.example.practica1_compi2.analizador.ast.NodoSentencia;
import com.example.practica1_compi2.analizador.gramatica.LatinusLexer;
import com.example.practica1_compi2.analizador.gramatica.LatinusParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ASTBuilderTest {

    private NodoPrograma parsear(String codigo) {
        LatinusLexer lexer = new LatinusLexer(CharStreams.fromString(codigo));
        LatinusParser parser = new LatinusParser(new CommonTokenStream(lexer));
        NodoPrograma programa = (NodoPrograma) new ASTBuilder().visit(parser.programa());

        // Si hubo errores de sintaxis, ANTLR no lanza excepcion por defecto:
        // solo los imprime en consola y sigue. Esto detecta ese caso en el test.
        assertEquals(0, parser.getNumberOfSyntaxErrors(), "La gramatica reporto errores de sintaxis");
        return programa;
    }

    @Test
    void declaracionDeVariableSimple() {
        String codigo = """
                VARIABILES>
                esto edad : numerus 20;

                MAIOR>
                >> edad;
                FINIS;
                """;

        NodoPrograma programa = parsear(codigo);

        assertEquals(1, programa.variablesGlobales().size());
        assertEquals(1, programa.main().size());
        System.out.println(programa);
    }

    @Test
    void funcionConRetorno() {
        String codigo = """
                MUNERA>
                ratio numerus calcularPoder(esto fuerza : numerus) {
                    VARIABILES[
                        esto total : numerus fuerza * 2;
                    ]
                    reddere total;
                } finis;

                MAIOR>
                esto resultado : numerus calcularPoder(10);
                FINIS;
                """;

        NodoPrograma programa = parsear(codigo);

        assertEquals(1, programa.funciones().size());
        assertEquals("numerus", programa.funciones().get(0).tipoRetorno());
        assertEquals(1, programa.funciones().get(0).variablesLocales().size());
        System.out.println(programa);
    }

    @Test
    void condicionalConAliterEncadenado() {
        // si (...) -> ramaAliter(x>10) -> ramaElse. Verifica que la separacion
        // de bloques por subregla (bloqueSentencias) no mezcla las sentencias.
        String codigo = """
                MAIOR>
                si (x > 10 && y < 5) {
                    cifrado = verum;
                } aliter (x > 10) {
                    cifrado = falsus;
                } aliter {
                    cifrado = falsus;
                } finis;
                FINIS;
                """;

        NodoPrograma programa = parsear(codigo);
        var condicional = (NodoSentencia.Condicional) programa.main().get(0);

        assertEquals(2, condicional.ramas().size());   // si + un aliter con condicion
        assertNotNull(condicional.elseCuerpo());        // el aliter final, sin condicion
        assertEquals(1, condicional.elseCuerpo().size());
    }

    @Test
    void condicionalSinElse() {
        String codigo = """
                MAIOR>
                si (x > 10) {
                    cifrado = verum;
                } finis;
                FINIS;
                """;

        NodoPrograma programa = parsear(codigo);
        var condicional = (NodoSentencia.Condicional) programa.main().get(0);

        assertEquals(1, condicional.ramas().size());
        assertNull(condicional.elseCuerpo());
    }

    @Test
    void arregloBooleanoSinPalabraDeTipo() {
        String codigo = """
                VARIABILES>
                series valores[2] : {verum, falsus};

                MAIOR>
                FINIS;
                """;

        NodoPrograma programa = parsear(codigo);
        var arreglo = (NodoSentencia.DeclaracionArreglo) programa.variablesGlobales().get(0);

        assertEquals("booleano", arreglo.tipo());
        assertEquals(2, arreglo.valoresIniciales().size());
    }

    @Test
    void incrementoEnCicloPerSeDesazucaraAAsignacion() {
        String codigo = """
                MAIOR>
                per (esto i : numerus 0; i < 10; i++) {
                    >> i;
                }
                FINIS;
                """;

        NodoPrograma programa = parsear(codigo);
        var ciclo = (NodoSentencia.CicloPer) programa.main().get(0);

        // i++ debe llegar como Asignacion(i, Binaria("+", i, 1)), no como un nodo especial
        assertEquals(NodoSentencia.Asignacion.class, ciclo.incremento().getClass());
    }
}