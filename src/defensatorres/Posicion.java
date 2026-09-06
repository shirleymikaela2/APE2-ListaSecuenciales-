package defensatorres;

import java.util.Objects;

/** Representa una celda inmutable del mapa. */
public final class Posicion {
    private final int fila;
    private final int columna;

    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public double distanciaA(Posicion otra) {
        int diferenciaFilas = fila - otra.fila;
        int diferenciaColumnas = columna - otra.columna;
        return Math.hypot(diferenciaFilas, diferenciaColumnas);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Posicion)) {
            return false;
        }
        Posicion otra = (Posicion) objeto;
        return fila == otra.fila && columna == otra.columna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }

    @Override
    public String toString() {
        return "(" + fila + ", " + columna + ")";
    }
}
