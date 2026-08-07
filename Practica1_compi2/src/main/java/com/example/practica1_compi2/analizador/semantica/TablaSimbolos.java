package com.example.practica1_compi2.analizador.semantica;

import java.util.*;

public class TablaSimbolos {

    public record SimboloVariable(String nombre, String tipo, boolean esArreglo, Integer tamanoArreglo) {}
    public record DefinicionStruct(String nombre, Map<String, String> campos) {}
    public record DefinicionFuncion(String nombre, List<String> tipoParametros, String tipoRetorno) {}

    private static class Scope {
        final Map<String, SimboloVariable> variable = new LinkedHashMap<>();
        final Map<String, DefinicionStruct> struct = new LinkedHashMap<>();
    }

    private final Deque<Scope> pila = new ArrayDeque<>();
    private final Map<String, DefinicionFuncion> funciones = new LinkedHashMap<>();

    public TablaSimbolos() {
        pila.push(new Scope()); //scope global, se mantiene en toda la ejecucion
    }

    //manejo de scope
    public void entrarScope() {
        pila.push(new Scope());
    }

    public void salirScope() {
        if (pila.size() == 1) {
            throw  new IllegalStateException("No se puede salir del scope global");
        }
        pila.pop();
    }

    //variables y arreglos
    //declara en el scope actual que esta en la cima de la pila
    public boolean declararVariable(String nombre, String tipo) {
        return declararVariable(nombre, tipo, false, null);
    }

    public boolean declararVariable(String nombre, String tipo, boolean esArreglo, Integer tamanoArreglo) {
        Scope actual = pila.peek();
        if (actual.variable.containsKey(nombre)) {
            return false; //ya existe en este scope
        }
        actual.variable.put(nombre, new SimboloVariable(nombre, tipo, esArreglo, tamanoArreglo));
        return true;
    }

    //busca de adentro hacia afuera: scope actual primero, liuego los externos hasta el global
    public Optional<SimboloVariable> buscarVariable(String nombre) {
        for (Scope s : pila) {
            SimboloVariable v = s.variable.get(nombre);
            if (v != null) return Optional.of(v);
        }
        return Optional.empty();
    }

    //structs
    //tambien se declara en el scope actual
    public boolean declararStruct(String nombre, Map<String, String> campos) {
        Scope actual = pila.peek();
        if (actual.struct.containsKey(nombre)) {
            return false;
        }
        actual.struct.put(nombre, new DefinicionStruct(nombre, campos));
        return true;
    }

    public Optional<DefinicionStruct> buscarStruct(String nombre) {
        for (Scope s : pila) {
            DefinicionStruct d = s.struct.get(nombre);
            if (d != null) return Optional.of(d);
        }
        return Optional.empty();
    }

    //funciones, simpre globales, mapa plano aparte
    public boolean declararFunciones(String nombre, List<String> tipoParametros, String tipoRetorno) {
        if (funciones.containsKey(nombre)) {
            return false;
        }
        funciones.put(nombre, new DefinicionFuncion(nombre, tipoParametros, tipoRetorno));
        return true;
    }

    public Optional<DefinicionFuncion> buscarFuncion(String nombre) {
        return Optional.ofNullable(funciones.get(nombre));
    }
}