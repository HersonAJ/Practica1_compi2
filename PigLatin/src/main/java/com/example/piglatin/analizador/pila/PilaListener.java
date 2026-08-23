package com.example.piglatin.analizador.pila;

import com.example.piglatin.analizador.gramatica.LatinusParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class PilaListener extends LatinusParserBaseListener {

    private final Deque<ElementoPila> pila = new ArrayDeque<>();
    private final List<PasoPila> pasos = new ArrayList<>();
    private int contador = 0;

    @Override
    public void visitTerminal(TerminalNode node) {
        String simbolo = node.getText();
        pila.push(new ElementoPila(simbolo, false));
        contador++;
        pasos.add(new PasoPila(
                contador,
                TipoOperacion.SHIFT,
                simbolo,
                null,
                snapshot(),
                "shift " + simbolo
        ));
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        int n = ctx.getChildCount();
        if (n == 0 || pila.size() < n) return;

        List<String> reducidos = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            reducidos.add(0, pila.pop().simbolo());
        }

        String noTerminal = nombreRegla(ctx);
        pila.push(new ElementoPila(noTerminal, true));
        contador++;

        boolean esRaiz = ctx.getParent() == null;
        TipoOperacion operacion = esRaiz ? TipoOperacion.ACCEPT : TipoOperacion.REDUCE;
        String verbo = esRaiz ? "accept" : "replace";
        String descripcion = verbo + " " + noTerminal + " -> " + String.join(" ", reducidos);

        pasos.add(new PasoPila(contador, operacion, noTerminal, reducidos, snapshot(), descripcion));
    }

    private String nombreRegla(ParserRuleContext ctx) {
        String nombreClase = ctx.getClass().getSimpleName();
        return nombreClase.endsWith("Context")
                ? nombreClase.substring(0, nombreClase.length() - "Context".length())
                : nombreClase;
    }

    private List<ElementoPila> snapshot() {
        List<ElementoPila> copia = new ArrayList<>(pila);
        Collections.reverse(copia);
        return copia;
    }

    public List<PasoPila> getPasos() {
        return pasos;
    }

}