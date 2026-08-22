package com.example.piglatin.service;

import com.example.piglatin.analizador.ast.NodoPrograma;
import com.example.piglatin.analizador.builder.ASTBuilder;
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

    public CompileService() {
    }

    public ResultadoCompilacion analizar(String codigo) {

        if (codigo != null && codigo.trim().isEmpty()) {
            return new ResultadoCompilacion(
                    false,
                    null,
                    null,
                    null,
                    null,
                    List.of(),
                    List.of(),
                    List.of("El codigo está vacio"),
                    List.of()
            );
        }

        try {
            LatinusLexer lexer = new LatinusLexer(CharStreams.fromString(codigo));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LatinusParser parser = new LatinusParser(tokens);

            // Registrar listener de la pila
            PilaListener pilaListener = new PilaListener();
            parser.addParseListener(pilaListener);

            // Manejo de errores sintácticos
            List<String> erroresSintacticos = new ArrayList<>();
            parser.removeErrorListeners();
            parser.addErrorListener(new ConsoleErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol,
                                        int line,
                                        int charPositionInLine,
                                        String msg,
                                        RecognitionException e) {
                    erroresSintacticos.add("Error sintáctico en línea " + line + ":" + charPositionInLine + " - " + msg);
                }
            });

            var tree = parser.programa();
            List<PasoPila> pasosPila = pilaListener.getPasos();

            if (!erroresSintacticos.isEmpty()) {
                return new ResultadoCompilacion(
                        false,
                        null,
                        null,
                        null,
                        null,
                        erroresSintacticos,
                        List.of(),
                        List.of(),
                        pasosPila
                );
            }

            // Builder AST
            ASTBuilder builder = new ASTBuilder();
            NodoPrograma programa = (NodoPrograma) builder.visit(tree);

            // Validador semántico
            ValidadorSemantico validador = new ValidadorSemantico();
            List<ErrorSemantico> erroresSemanticos = validador.validar(programa);

            // Coloreado
            LatinusLexer lexerColor = new LatinusLexer(CharStreams.fromString(codigo));
            CommonTokenStream tokensColor = new CommonTokenStream(lexerColor);
            LatinusParser parserColor = new LatinusParser(tokensColor);
            var treeColor = parserColor.programa();

            ASTColor colorVisitor = new ASTColor(tokensColor);
            List<ColorMapa.TextoColoreado> coloreado = colorVisitor.visit(treeColor);

            if (!erroresSemanticos.isEmpty()) {
                return new ResultadoCompilacion(
                        false,
                        programa,
                        validador.getTabla(),
                        null,
                        coloreado,
                        List.of(),
                        erroresSemanticos,
                        List.of(),
                        pasosPila
                );
            }

            TraductorPigLatin traductor = new TraductorPigLatin();
            String traduccion = traductor.traducir(programa);

            return new ResultadoCompilacion(
                    true,
                    programa,
                    validador.getTabla(),
                    traduccion,
                    coloreado,
                    List.of(),
                    List.of(),
                    List.of(),
                    pasosPila
            );

        } catch (Exception e) {
            return new ResultadoCompilacion(
                    false,
                    null,
                    null,
                    null,
                    null,
                    List.of(),
                    List.of(),
                    List.of("Error interno: " + e.getMessage()),
                    List.of()
            );
        }
    }
}