package com.example.piglatin.analizador.pila;

import com.example.piglatin.analizador.gramatica.LatinusParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class PilaListener extends LatinusParserBaseListener {

    private final Deque<String> pila = new ArrayDeque<>();
    private final List<PasoPila> pasos = new ArrayList<>();
    private int contador = 0;

    @Override
    public void visitTerminal(TerminalNode node) {
        String simobolo = node.getText();
        pila.push(simobolo);
        contador++;
        pasos.add(new PasoPila(
                contador,
                TipoOperacion.SHIFT,
                simobolo,
                null,
                snapshot(),
                "shift" + simobolo
        ));
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        int n = ctx.getChildCount();
        if (n == 0) {
            return;
        }

        List<String> reducidos = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            reducidos.add(0,pila.pop());
        }

        String noTerminal = nombreRegla(ctx);
        pila.push(noTerminal);
        contador++;

        //la regla raiz programa es la unica sin padre: su reduce final es el acept
        boolean esRaiz = ctx.getParent() == null;
        TipoOperacion operacion = esRaiz ? TipoOperacion.ACCEPT : TipoOperacion.REDUCE;
        String verbo = esRaiz ? "accept" : "replace";
        String descripcion = verbo + " " + noTerminal + " -> " + String.join(" ", reducidos);

        pasos.add(new PasoPila(contador,operacion,noTerminal,reducidos,snapshot(), descripcion));
    }

    private String nombreRegla(ParserRuleContext ctx) {
        String nombreClase = ctx.getClass().getSimpleName();
        return nombreClase.endsWith("Context") ? nombreClase.substring(0, nombreClase.length() - "Context".length())
        : nombreClase;
    }

    private List<String> snapshot() {
        List<String> copia = new ArrayList<>(pila);
        Collections.reverse(copia);
        return copia;
    }

    public List<PasoPila> getPasos() {
        return pasos;
    }

}
