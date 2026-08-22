package com.example.piglatin.analizador.semantica;

import java.util.*;

public class TablaSimbolos {

    public enum Categoria {
        VARIABLE, ARREGLO, PARAMETRO, STRUCT_INSTANCIA, STRUCT_DEF, FUNCION
    }

    public record Parametro(String nombre, String tipo) {}

    public record SimboloVariable(String nombre, String tipo, boolean esArreglo, Integer tamanoArreglo) {}

    public record DefinicionStruct(String nombre, Map<String, String> campos) {}

    public record DefinicionFuncion(String nombre, List<Parametro> parametros, String tipoRetorno) {
        public List<String> tipoParametros() {
            return parametros.stream().map(Parametro::tipo).toList();
        }
    }

    public record EntradaSimbolo(
            int id,
            String nombre,
            Categoria categoria,
            String tipo,
            int numParametros,
            List<Parametro> parametros,
            String ambito,
            int alcance
    ) {}

    private static class Scope {
        final String etiqueta;
        final Map<String, SimboloVariable> variable = new LinkedHashMap<>();
        final Map<String, DefinicionStruct> struct = new LinkedHashMap<>();

        Scope(String etiqueta) {
            this.etiqueta = etiqueta;
        }
    }

    private final Deque<Scope> pila = new ArrayDeque<>();
    private final Map<String, DefinicionFuncion> funciones = new LinkedHashMap<>();

    private final List<EntradaSimbolo> registro = new ArrayList<>();
    private int siguienteId = 1;

    public TablaSimbolos() {
        pila.push(new Scope("global"));
    }

    // ===== manejo de scope =====

    public void entrarScope(String etiqueta) {
        pila.push(new Scope(etiqueta));
    }

    public void entrarScope() {
        entrarScope("bloque");
    }

    public void salirScope() {
        if (pila.size() == 1) {
            throw new IllegalStateException("No se puede salir del scope global");
        }
        pila.pop();
    }

    private String ambitoActual() {
        return pila.peek().etiqueta;
    }

    private int alcanceActual() {
        return pila.size();
    }

    // ===== variables y arreglos =====

    public boolean declararVariable(String nombre, String tipo) {
        return declararVariable(nombre, tipo, false, null, Categoria.VARIABLE);
    }

    public boolean declararVariable(String nombre, String tipo, boolean esArreglo, Integer tamanoArreglo) {
        return declararVariable(nombre, tipo, esArreglo, tamanoArreglo, Categoria.VARIABLE);
    }

    public boolean declararVariable(String nombre, String tipo, boolean esArreglo,
                                    Integer tamanoArreglo, Categoria categoria) {
        Scope actual = pila.peek();
        if (actual.variable.containsKey(nombre)) {
            return false;
        }
        actual.variable.put(nombre, new SimboloVariable(nombre, tipo, esArreglo, tamanoArreglo));

        Categoria categoriaFinal = esArreglo ? Categoria.ARREGLO : categoria;
        registro.add(new EntradaSimbolo(
                siguienteId++, nombre, categoriaFinal, tipo, 0, List.of(),
                ambitoActual(), alcanceActual()));
        return true;
    }

    public Optional<SimboloVariable> buscarVariable(String nombre) {
        for (Scope s : pila) {
            SimboloVariable v = s.variable.get(nombre);
            if (v != null) return Optional.of(v);
        }
        return Optional.empty();
    }

    // ===== structs =====

    public boolean declararStruct(String nombre, Map<String, String> campos) {
        Scope actual = pila.peek();
        if (actual.struct.containsKey(nombre)) {
            return false;
        }
        actual.struct.put(nombre, new DefinicionStruct(nombre, campos));

        // Los campos del struct se registran como si fueran parámetros
        List<Parametro> camposComoParametros = campos.entrySet().stream()
                .map(e -> new Parametro(e.getKey(), e.getValue()))
                .toList();

        registro.add(new EntradaSimbolo(
                siguienteId++, nombre, Categoria.STRUCT_DEF, "struct",
                camposComoParametros.size(), camposComoParametros,
                ambitoActual(), alcanceActual()));
        return true;
    }

    public Optional<DefinicionStruct> buscarStruct(String nombre) {
        for (Scope s : pila) {
            DefinicionStruct d = s.struct.get(nombre);
            if (d != null) return Optional.of(d);
        }
        return Optional.empty();
    }

    // ===== funciones =====

    public boolean declararFunciones(String nombre, List<Parametro> parametros, String tipoRetorno) {
        if (funciones.containsKey(nombre)) {
            return false;
        }
        funciones.put(nombre, new DefinicionFuncion(nombre, parametros, tipoRetorno));

        registro.add(new EntradaSimbolo(
                siguienteId++, nombre, Categoria.FUNCION, tipoRetorno,
                parametros.size(), parametros, ambitoActual(), alcanceActual()));
        return true;
    }

    public Optional<DefinicionFuncion> buscarFuncion(String nombre) {
        return Optional.ofNullable(funciones.get(nombre));
    }

    // ===== lectura para la UI =====

    public Map<String, DefinicionFuncion> getFunciones() {
        return Collections.unmodifiableMap(funciones);
    }

    public List<EntradaSimbolo> getRegistroCompleto() {
        return Collections.unmodifiableList(registro);
    }
}