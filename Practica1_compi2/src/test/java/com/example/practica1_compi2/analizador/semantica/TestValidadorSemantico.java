package com.example.practica1_compi2.analizador.semantica;

import com.example.practica1_compi2.analizador.builder.ASTBuilder;
import com.example.practica1_compi2.analizador.gramatica.LatinusLexer;
import com.example.practica1_compi2.analizador.gramatica.LatinusParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class TestValidadorSemantico {
    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE ERRORES SEMÁNTICOS ===\n");

        testVariableNoDeclarada();
        testVariableDuplicada();
        testTipoIncompatibleAsignacion();
        testTipoIncompatibleOperacion();
        testStructNoDeclarado();
        testCampoStructInexistente();
        testCampoStructDuplicado();
        testFuncionNoDeclarada();
        testArgumentosIncorrectos();
        testBreakFueraDeCiclo();
        testContinueFueraDeCiclo();
        testRetornoFueraDeFuncion();
        testFuncionSinRetornoConReturn();
        testIndiceArregloNoNumerico();
        testArregloYaDeclarado();
    }

    private static void testVariableNoDeclarada() {
        System.out.println("=== 1. Variable no declarada ===");
        String codigo = """
                MAIOR>
                >> edadNoDeclarada;
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testVariableDuplicada() {
        System.out.println("\n=== 2. Variable duplicada ===");
        String codigo = """
                VARIABILES>
                esto edad : numerus 20;
                esto edad : textum "veinte";
                MAIOR>
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testTipoIncompatibleAsignacion() {
        System.out.println("\n=== 3. Tipo incompatible en asignación ===");
        String codigo = """
                VARIABILES>
                esto texto : textum "Hola";
                esto numero : numerus 10;
                MAIOR>
                texto = numero;
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testTipoIncompatibleOperacion() {
        System.out.println("\n=== 4. Tipo incompatible en operación ===");
        String codigo = """
                VARIABILES>
                esto texto : textum "Hola";
                esto numero : numerus 10;
                MAIOR>
                esto resultado : textum texto + numero;
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testStructNoDeclarado() {
        System.out.println("\n=== 5. Struct no declarado ===");
        String codigo = """
                MAIOR>
                esto persona : Persona { nombre: "Juan", edad: 25 };
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testCampoStructInexistente() {
        System.out.println("\n=== 6. Campo de struct inexistente ===");
        String codigo = """
                VARIABILES>
                structura Persona {
                    esto nombre : textum;
                    esto edad : numerus;
                } finis;
                MAIOR>
                esto p : Persona { nombre: "Juan", edad: 25, peso: 70 };
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testCampoStructDuplicado() {
        System.out.println("\n=== 7. Campo duplicado en struct ===");
        String codigo = """
                VARIABILES>
                structura Persona {
                    esto nombre : textum;
                    esto nombre : textum;
                } finis;
                MAIOR>
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testFuncionNoDeclarada() {
        System.out.println("\n=== 8. Función no declarada ===");
        String codigo = """
                MAIOR>
                esto resultado : numerus calcularPoder(10);
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testArgumentosIncorrectos() {
        System.out.println("\n=== 9. Número de argumentos incorrecto ===");
        String codigo = """
                MUNERA>
                ratio numerus sumar(esto a : numerus, esto b : numerus) {
                    reddere a + b;
                } finis;
                MAIOR>
                esto resultado : numerus sumar(5);
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testBreakFueraDeCiclo() {
        System.out.println("\n=== 10. break fuera de ciclo ===");
        String codigo = """
                MAIOR>
                interrumpe;
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testContinueFueraDeCiclo() {
        System.out.println("\n=== 11. continue fuera de ciclo ===");
        String codigo = """
                MAIOR>
                perge;
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testRetornoFueraDeFuncion() {
        System.out.println("\n=== 12. return fuera de función ===");
        String codigo = """
                MAIOR>
                reddere 10;
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testFuncionSinRetornoConReturn() {
        System.out.println("\n=== 13. Función sin retorno (actio) con return ===");
        String codigo = """
                MUNERA>
                actio imprimir(esto msg : textum) {
                    >> msg;
                    reddere 10;
                } finis;
                MAIOR>
                imprimir("Hola");
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testIndiceArregloNoNumerico() {
        System.out.println("\n=== 14. Índice de arreglo no numérico ===");
        String codigo = """
                VARIABILES>
                series numeros[5] : numerus {1,2,3,4,5};
                MAIOR>
                numeros["tres"] = 10;
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void testArregloYaDeclarado() {
        System.out.println("\n=== 15. Arreglo ya declarado ===");
        String codigo = """
                VARIABILES>
                series numeros[5] : numerus;
                series numeros[3] : numerus;
                MAIOR>
                FINIS;
                """;
        ejecutarPrueba(codigo);
    }

    private static void ejecutarPrueba(String codigo) {
        try {
            LatinusLexer lexer = new LatinusLexer(CharStreams.fromString(codigo));
            LatinusParser parser = new LatinusParser(new CommonTokenStream(lexer));
            ASTBuilder builder = new ASTBuilder();
            var programa = (com.example.practica1_compi2.analizador.ast.NodoPrograma) builder.visit(parser.programa());

            ValidadorSemantico validador = new ValidadorSemantico();
            var errores = validador.validar(programa);

            if (errores.isEmpty()) {
                System.out.println("  ⚠️ NO se detectaron errores (se esperaban errores)");
            } else {
                System.out.println("  ✅ Errores detectados (" + errores.size() + "):");
                for (var error : errores) {
                    System.out.println("    " + error);
                }
            }
        } catch (Exception e) {
            System.out.println("  ⚠️ Error al parsear: " + e.getMessage());
        }
        System.out.println();
    }
}