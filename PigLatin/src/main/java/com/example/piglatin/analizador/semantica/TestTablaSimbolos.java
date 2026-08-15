package com.example.piglatin.analizador.semantica;

import java.util.List;
import java.util.Map;

public class TestTablaSimbolos {
    public static void main(String[] args) {
        TablaSimbolos tabla = new TablaSimbolos();

        // 1. Variables globales
        System.out.println("=== Variables Globales ===");
        tabla.declararVariable("edad", "numerus");
        tabla.declararVariable("nombre", "textum", false, null);
        tabla.declararVariable("valores", "numerus", true, 5);

        System.out.println("Buscar 'edad': " + tabla.buscarVariable("edad").get());
        System.out.println("Buscar 'nombre': " + tabla.buscarVariable("nombre").get());
        System.out.println("Buscar 'valores': " + tabla.buscarVariable("valores").get());

        // 2. Struct
        System.out.println("\n=== Struct ===");
        tabla.declararStruct("Persona", Map.of("nombre", "textum", "edad", "numerus"));
        System.out.println("Buscar struct 'Persona': " + tabla.buscarStruct("Persona").get());

        // 3. Funcion
        System.out.println("\n=== Funcion ===");
        tabla.declararFunciones("calcularPoder", List.of("numerus"), "numerus");
        System.out.println("Buscar funcion 'calcularPoder': " + tabla.buscarFuncion("calcularPoder").get());

        // 4. Scope anidado (dentro de una funcion)
        System.out.println("\n=== Scope Anidado ===");
        tabla.entrarScope();
        tabla.declararVariable("total", "numerus");
        System.out.println("Buscar 'total' (local): " + tabla.buscarVariable("total").get());
        System.out.println("Buscar 'edad' (global desde local): " + tabla.buscarVariable("edad").get());
        tabla.salirScope();

        // 5. Buscar algo que no existe
        System.out.println("\n=== Buscar inexistente ===");
        System.out.println("Buscar 'xyz': " + tabla.buscarVariable("xyz"));
    }
}