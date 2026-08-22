package com.example.piglatin.analizador.errores;

/**
 * Representa un error posicional (léxico o sintáctico) capturado durante
 * el análisis, con la línea y columna ya separadas del texto del mensaje
 * para poder mostrarlas correctamente en la UI (por ejemplo, en la
 * columna "Línea" de la tabla de errores) sin tener que reparsear un
 * String como "Error en línea 1:0 - ...".
 */
public record ErrorPosicional(int linea, int columna, String mensaje) {

    /**
     * Texto combinado, útil para logs en consola o si en algún punto
     * todavía se necesita un solo String.
     */
    public String descripcionCompleta() {
        return "Línea " + linea + ":" + columna + " - " + mensaje;
    }
}