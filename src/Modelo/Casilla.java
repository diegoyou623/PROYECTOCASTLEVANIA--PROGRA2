
package Modelo;

import Piezas.Pieza;

public class Casilla {

    private int fila;
    private int columna;
    private Pieza pieza;

    public Casilla(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.pieza = null;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public Pieza getPieza() {
        return pieza;
    }

    public void setPieza(Pieza pieza) {
        this.pieza = pieza;
    }

    public boolean estaVacia() {
        return pieza == null;
    }
}