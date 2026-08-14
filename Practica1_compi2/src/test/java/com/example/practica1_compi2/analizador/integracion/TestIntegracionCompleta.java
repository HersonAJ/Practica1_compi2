package com.example.practica1_compi2.analizador.integracion;

import com.example.practica1_compi2.analizador.ast.NodoPrograma;
import com.example.practica1_compi2.analizador.builder.ASTBuilder;
import com.example.practica1_compi2.analizador.gramatica.LatinusLexer;
import com.example.practica1_compi2.analizador.gramatica.LatinusParser;
import com.example.practica1_compi2.analizador.semantica.TablaSimbolos;
import com.example.practica1_compi2.analizador.semantica.ValidadorSemantico;
import com.example.practica1_compi2.analizador.semantica.errores.ErrorSemantico;
import com.example.practica1_compi2.color.ASTColor;
import com.example.practica1_compi2.color.ColorMapa;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;

public class TestIntegracionCompleta {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("      PRUEBA DE INTEGRACIÓN COMPLETA");
        System.out.println("=".repeat(70));

        // ===== CASO 1: CÓDIGO VÁLIDO =====
        System.out.println("\n📌 CASO 1: CÓDIGO VÁLIDO");
        System.out.println("-".repeat(70));
        String codigoValido = """
                VARIABILES>
                esto edad : numerus 20;
                esto nombre : textum "Juan";
                esto activo : verum;
                
                MUNERA>
                ratio numerus sumar(esto a : numerus, esto b : numerus) {
                    reddere a + b;
                } finis;
                
                actio saludar(esto msg : textum) {
                    >> "Hola " >> msg;
                } finis;
                
                MAIOR>
                >> "Bienvenido" >> nombre;
                esto resultado : numerus sumar(10, 20);
                >> "Resultado: " >> resultado;
                saludar("Mundo");
                FINIS;
                """;  // ← SIN punto y coma

        ejecutarPrueba(codigoValido);

        // ===== CASO 2: CÓDIGO CON ERRORES =====
        System.out.println("\n📌 CASO 2: CÓDIGO CON ERRORES");
        System.out.println("-".repeat(70));
        String codigoErrores = """
                VARIABILES>
                esto edad : numerus 20;
                esto edad : textum "Juan";
                
                MUNERA>
                ratio numerus sumar(esto a : numerus, esto b : numerus) {
                    reddere a + c;
                } finis;
                
                MAIOR>
                >> x;
                resultado = 10;
                FINIS;
                """;  // ← SIN punto y coma

        ejecutarPrueba(codigoErrores);
    }
    private static void ejecutarPrueba(String codigo) {
        System.out.println("\n📝 CÓDIGO FUENTE:");
        System.out.println("›".repeat(50));
        System.out.println(codigo);
        System.out.println("‹".repeat(50));

        try {
            // ===== 1. PARSER =====
            LatinusLexer lexer = new LatinusLexer(CharStreams.fromString(codigo));
            LatinusParser parser = new LatinusParser(new CommonTokenStream(lexer));
            var tree = parser.programa();

            // ===== 2. AST BUILDER =====
            ASTBuilder builder = new ASTBuilder();
            NodoPrograma programa = (NodoPrograma) builder.visit(tree);

            System.out.println("\n✅ AST construido correctamente:");
            System.out.println("   • Variables globales: " + programa.variablesGlobales().size());
            System.out.println("   • Funciones: " + programa.funciones().size());
            System.out.println("   • Main: " + programa.main().size() + " sentencias");

            // ===== 3. VALIDADOR SEMÁNTICO =====
            ValidadorSemantico validador = new ValidadorSemantico();
            List<ErrorSemantico> errores = validador.validar(programa);

            System.out.println("\n🔍 VALIDACIÓN SEMÁNTICA:");
            if (errores.isEmpty()) {
                System.out.println("   ✅ SIN errores");
            } else {
                System.out.println("   ❌ " + errores.size() + " errores encontrados:");
                for (int i = 0; i < errores.size(); i++) {
                    System.out.println("      " + (i + 1) + ". " + errores.get(i));
                }
            }

            // ===== 4. COLOREADO =====
            System.out.println("\n🎨 CÓDIGO COLOREADO (texto|color):");
            System.out.println("›".repeat(50));

            LatinusLexer lexerColor = new LatinusLexer(CharStreams.fromString(codigo));
            LatinusParser parserColor = new LatinusParser(new CommonTokenStream(lexerColor));
            var treeColor = parserColor.programa();

            ASTColor colorVisitor = new ASTColor();
            List<ColorMapa.TextoColoreado> coloreado = colorVisitor.visit(treeColor);

            StringBuilder lineaActual = new StringBuilder();
            for (ColorMapa.TextoColoreado tc : coloreado) {
                if (tc.color() == null) {
                    lineaActual.append(tc.texto());
                } else {
                    lineaActual.append("[").append(tc.texto()).append("|").append(tc.color()).append("]");
                }
            }
            System.out.println(lineaActual.toString());
            System.out.println("‹".repeat(50));

            // ===== 5. TABLA DE SÍMBOLOS (solo si no hay errores) =====
            if (errores.isEmpty()) {
                System.out.println("\n📚 TABLA DE SÍMBOLOS:");
                TablaSimbolos tabla = validador.getTabla();
                System.out.println("   (No se puede imprimir directamente, pero existe)");
            }

            System.out.println("\n✅ PRUEBA COMPLETADA");

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(70));
    }
}