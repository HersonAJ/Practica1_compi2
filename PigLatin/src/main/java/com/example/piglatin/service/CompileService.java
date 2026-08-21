package com.example.piglatin.service;

import com.example.piglatin.analizador.ast.NodoPrograma;
import com.example.piglatin.analizador.builder.ASTBuilder;
import com.example.piglatin.analizador.gramatica.LatinusLexer;
import com.example.piglatin.analizador.gramatica.LatinusParser;
import com.example.piglatin.analizador.semantica.ValidadorSemantico;
import com.example.piglatin.analizador.semantica.errores.ErrorSemantico;
import com.example.piglatin.analizador.traduccion.TraductorPigLatin;
import com.example.piglatin.color.ASTColor;
import com.example.piglatin.color.ColorMapa;
import org.antlr.v4.runtime.*;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.fromString;

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
                    List.of("El código está vacío")
            );
        }
            try {
                LatinusLexer lexer = new LatinusLexer(CharStreams.fromString(codigo));
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                LatinusParser parser = new LatinusParser(tokens);

                //errores sintacticos
                List<String> erroresSintacticos = new ArrayList<>();
                parser.removeErrorListeners();
                parser.addErrorListener(new ConsoleErrorListener() {
                    @Override
                    public void syntaxError(org.antlr.v4.runtime.Recognizer<?, ?> recognizer,
                                            Object offendingSymbol,
                                            int line,
                                            int charPositionInLine,
                                            String msg,
                                            org.antlr.v4.runtime.RecognitionException e) {
                        erroresSintacticos.add("Error sintáctico en línea " + line + ":" + charPositionInLine + " - " + msg);
                    }
                });

                var tree = parser.programa();

                //errores sintacticos
                if (!erroresSintacticos.isEmpty()) {
                    return new ResultadoCompilacion(
                            false,
                            null,
                            null,
                            null,
                            null,
                            erroresSintacticos,
                            List.of(),
                            List.of()
                    );
                }

                //builder
                ASTBuilder builder = new ASTBuilder();
                NodoPrograma programa = (NodoPrograma) builder.visit(tree);

                //validador semantico
                ValidadorSemantico validador = new ValidadorSemantico();
                List<ErrorSemantico> erroresSemanticos = validador.validar(programa);

                //coloreado
                LatinusLexer lexerColor = new LatinusLexer(CharStreams.fromString(codigo));
                CommonTokenStream tokensColor = new CommonTokenStream(lexerColor);
                LatinusParser parserColor = new LatinusParser(tokensColor);
                var treeColor = parserColor.programa();

                ASTColor colorVisitor = new ASTColor(tokensColor);
                List<ColorMapa.TextoColoreado> coloreado = colorVisitor.visit(treeColor);

                // 5. Si hay errores semánticos, retornar
                if (!erroresSemanticos.isEmpty()) {
                    return new ResultadoCompilacion(
                            false,
                            programa,
                            validador.getTabla(),
                            null,
                            coloreado,
                            List.of(),
                            erroresSemanticos,
                            List.of()
                    );
                }

                TraductorPigLatin traductor = new TraductorPigLatin();
                String traduccion = traductor.traducir(programa);

                // 7. Resultado exitoso
                return new ResultadoCompilacion(
                        true,
                        programa,
                        validador.getTabla(),
                        traduccion,
                        coloreado,
                        List.of(),
                        List.of(),
                        List.of()
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
                        List.of("Error interno: " + e.getMessage())
                );
            }


    }
}
