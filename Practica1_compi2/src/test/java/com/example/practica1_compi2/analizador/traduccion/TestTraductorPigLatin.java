package com.example.practica1_compi2.analizador.traduccion;

import com.example.practica1_compi2.analizador.builder.ASTBuilder;
import com.example.practica1_compi2.analizador.gramatica.LatinusLexer;
import com.example.practica1_compi2.analizador.gramatica.LatinusParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class TestTraductorPigLatin {
    public static void main(String[] args) {
        String codigo = """
                VARIABILES>
                esto edad : numerus 20;
                esto cifrado : falsus;
                esto comandante : textum "Estudiante X";
                esto fuerza : numerus 10;
                esto poder : numerus 0;

                MUNERA>
                ratio numerus calcularPoder(esto fuerza : numerus) {
                    VARIABILES[
                        esto total : numerus fuerza * 2;
                    ]
                    reddere total;
                } finis;

                MAIOR>
                >> "Hola comandante!" ;
                >> "Ingresa tu nombre por favor" ;
                comandante <<
                >> "Bienvenido" >> comandante ;
                >> "Ingresa tu edad" ;
                edad <<

                si (edad >= 18) {
                    cifrado = verum;
                    fuerza = 12;
                } finis ;

                >> "Tu poder es: " >> calcularPoder(fuerza);
                >> "La puerta esta cifrada?" >> cifrado ;

                FINIS;
                """;

        System.out.println("=== CÓDIGO ORIGINAL ===\n");
        System.out.println(codigo);

        // Parsear y construir AST
        LatinusLexer lexer = new LatinusLexer(CharStreams.fromString(codigo));
        LatinusParser parser = new LatinusParser(new CommonTokenStream(lexer));
        ASTBuilder builder = new ASTBuilder();
        var programa = (com.example.practica1_compi2.analizador.ast.NodoPrograma) builder.visit(parser.programa());

        // Traducir a PigLatin
        TraductorPigLatin traductor = new TraductorPigLatin();
        String traducido = traductor.traducir(programa);

        System.out.println("\n=== CÓDIGO TRADUCIDO A PIGLATIN ===\n");
        System.out.println(traducido);
    }
}