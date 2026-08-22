package com.example.piglatin.service;

import com.example.piglatin.analizador.ast.NodoPrograma;
import com.example.piglatin.analizador.builder.ASTBuilder;
import com.example.piglatin.analizador.errores.ErrorPosicional;
import com.example.piglatin.analizador.gramatica.LatinusLexer;
import com.example.piglatin.analizador.gramatica.LatinusParser;
import com.example.piglatin.analizador.pila.PasoPila;
import com.example.piglatin.analizador.pila.PilaListener;
import com.example.piglatin.analizador.semantica.ValidadorSemantico;
import com.example.piglatin.analizador.semantica.errores.ErrorSemantico;
import com.example.piglatin.analizador.traduccion.TraductorPigLatin;
import com.example.piglatin.color.ASTColor;
import com.example.piglatin.color.ColorMapa;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class CompileService {

    private static final boolean DEBUG = true;

    public CompileService() {
    }

    public ResultadoCompilacion analizar(String codigo) {

        if (DEBUG) {
            System.out.println("=== INICIO ANÁLISIS ===");
            System.out.println("Longitud del código: " + (codigo != null ? codigo.length() : 0));
        }

        if (codigo == null || codigo.trim().isEmpty()) {
            return new ResultadoCompilacion(
                    false, null, null, null, null,
                    List.of(), List.of(), List.of(), List.of("El código está vacío"), List.of()
            );
        }

        try {
            return analizarInterno(codigo);
        } catch (StackOverflowError soe) {
            if (DEBUG) System.err.println("❌ StackOverflowError durante el análisis");
            return new ResultadoCompilacion(
                    false, null, null, null, null,
                    List.of(), List.of(), List.of(),
                    List.of("El código produjo una estructura demasiado profunda o inválida para analizarse (revisa símbolos sin cerrar: (), {}, [])."),
                    List.of()
            );
        } catch (Exception e) {
            String errorMsg = "Error interno inesperado: " + e.getMessage();
            if (DEBUG) {
                System.err.println("❌ " + errorMsg);
                e.printStackTrace();
            }
            return new ResultadoCompilacion(
                    false, null, null, null, null,
                    List.of(), List.of(), List.of(), List.of(errorMsg), List.of()
            );
        }
    }


    public String traducir(NodoPrograma programa) {
        if (programa == null) {
            return "// No hay árbol de sintaxis abstracta (AST) disponible para traducir.";
        }

        if (DEBUG) System.out.println("=== INICIO TRADUCCIÓN A PIGLATIN ===");

        try {
            TraductorPigLatin traductor = new TraductorPigLatin();
            String traduccion = traductor.traducir(programa);
            if (DEBUG) {
                System.out.println("   Traducción generada: " + (traduccion != null ? traduccion.length() + " caracteres" : "NULL"));
                System.out.println("=== FIN TRADUCCIÓN ===");
            }
            return traduccion;
        } catch (Exception e) {
            if (DEBUG) {
                System.err.println("❌ Error durante la traducción: " + e.getMessage());
                e.printStackTrace();
            }
            return "// Error durante la traducción: " + e.getMessage();
        }
    }

    private ResultadoCompilacion analizarInterno(String codigo) {

        // 1 LEXER
        if (DEBUG) System.out.println("1. Creando lexer...");

        LatinusLexer lexer = new LatinusLexer(CharStreams.fromString(codigo));

        List<ErrorPosicional> erroresLexer = new ArrayList<>();
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                erroresLexer.add(new ErrorPosicional(line, charPositionInLine, msg));
                if (DEBUG) System.err.println("❌ Error léxico en línea " + line + ":" + charPositionInLine + " - " + msg);
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();

        if (!erroresLexer.isEmpty()) {
            if (DEBUG) System.out.println("❌ Errores léxicos encontrados: " + erroresLexer.size());
            return new ResultadoCompilacion(
                    false, null, null, null, null,
                    erroresLexer, List.of(), List.of(), List.of(), List.of()
            );
        }

        // 2 PARSER
        if (DEBUG) System.out.println("2. Creando parser...");

        LatinusParser parser = new LatinusParser(tokens);

        PilaListener pilaListener = new PilaListener();
        parser.addParseListener(pilaListener);

        List<ErrorPosicional> erroresSintacticos = new ArrayList<>();
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                erroresSintacticos.add(new ErrorPosicional(line, charPositionInLine, msg));
                if (DEBUG) System.err.println("❌ Error sintáctico en línea " + line + ":" + charPositionInLine + " - " + msg);
            }
        });

        parser.setErrorHandler(new DefaultErrorStrategy());

        // 3 PARSEAR
        if (DEBUG) System.out.println("3. Parseando...");

        ParserRuleContext tree;
        try {
            tree = parser.programa();
        } catch (RecognitionException re) {
            erroresSintacticos.add(new ErrorPosicional(-1, -1, "Error sintáctico no recuperable: " + re.getMessage()));
            tree = null;
        }

        List<PasoPila> pasosPila = pilaListener.getPasos();

        if (DEBUG) {
            System.out.println("   Árbol generado: " + (tree != null ? "OK" : "NULL"));
            System.out.println("   Pasos de pila: " + pasosPila.size());
        }

        if (!erroresSintacticos.isEmpty()) {
            if (DEBUG) System.out.println("❌ Errores sintácticos encontrados: " + erroresSintacticos.size());
            return new ResultadoCompilacion(
                    false, null, null, null, null,
                    List.of(), erroresSintacticos, List.of(), List.of(), pasosPila
            );
        }

        // 4 CONSTRUIR AST
        if (DEBUG) System.out.println("4. Construyendo AST...");

        NodoPrograma programa;
        try {
            ASTBuilder builder = new ASTBuilder();
            programa = (NodoPrograma) builder.visit(tree);
            if (DEBUG) System.out.println("   AST construido: OK");
        } catch (Exception e) {
            String errorMsg = "Error al construir el AST: " + e.getMessage();
            if (DEBUG) {
                System.err.println("❌ " + errorMsg);
                e.printStackTrace();
            }
            return new ResultadoCompilacion(
                    false, null, null, null, null,
                    List.of(), List.of(), List.of(), List.of(errorMsg), pasosPila
            );
        }

        if (programa == null) {
            String errorMsg = "El AST resultante es nulo";
            if (DEBUG) System.err.println("❌ " + errorMsg);
            return new ResultadoCompilacion(
                    false, null, null, null, null,
                    List.of(), List.of(), List.of(), List.of(errorMsg), pasosPila
            );
        }

        // 5 VALIDACION SEMANTICA
        if (DEBUG) System.out.println("5. Validando semánticamente...");

        ValidadorSemantico validador = new ValidadorSemantico();
        List<ErrorSemantico> erroresSemanticos;
        try {
            erroresSemanticos = validador.validar(programa);
            if (DEBUG) {
                System.out.println("   Validación semántica completada");
                System.out.println("   Errores semánticos: " + erroresSemanticos.size());
            }
        } catch (Exception e) {
            String errorMsg = "Error durante la validación semántica: " + e.getMessage();
            if (DEBUG) {
                System.err.println("❌ " + errorMsg);
                e.printStackTrace();
            }
            return new ResultadoCompilacion(
                    false, programa, validador.getTabla(), null, null,
                    List.of(), List.of(), List.of(), List.of(errorMsg), pasosPila
            );
        }

        // 6 COLOREADO
        if (DEBUG) System.out.println("6. Generando coloreado...");

        List<ColorMapa.TextoColoreado> coloreado;
        try {
            LatinusLexer lexerColor = new LatinusLexer(CharStreams.fromString(codigo));
            CommonTokenStream tokensColor = new CommonTokenStream(lexerColor);
            LatinusParser parserColor = new LatinusParser(tokensColor);
            parserColor.removeErrorListeners();
            parserColor.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                        int line, int charPositionInLine, String msg,
                                        RecognitionException e) {
                    if (DEBUG) System.out.println("   (Advertencia de coloreado: " + msg + ")");
                }
            });

            var treeColor = parserColor.programa();
            ASTColor colorVisitor = new ASTColor(tokensColor);
            coloreado = colorVisitor.visit(treeColor);

            if (DEBUG) System.out.println("   Coloreado generado: " + (coloreado != null ? coloreado.size() + " elementos" : "NULL"));
        } catch (Exception e) {
            if (DEBUG) {
                System.err.println("⚠️ Advertencia: Error en el coloreado: " + e.getMessage());
            }
            coloreado = null;
        }

        if (!erroresSemanticos.isEmpty()) {
            if (DEBUG) System.out.println("❌ Errores semánticos encontrados: " + erroresSemanticos.size());
            return new ResultadoCompilacion(
                    false, programa, validador.getTabla(), null, coloreado,
                    List.of(), List.of(), erroresSemanticos, List.of(), pasosPila
            );
        }

        // 7 EXITO DE ANALISIS
        if (DEBUG) {
            System.out.println("✅ ANÁLISIS COMPLETADO CON ÉXITO (Listo para traducir)");
            System.out.println("=== FIN ANÁLISIS ===");
        }

        return new ResultadoCompilacion(
                true, programa, validador.getTabla(), null, coloreado,
                List.of(), List.of(), List.of(), List.of(), pasosPila
        );
    }
}