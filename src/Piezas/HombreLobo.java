


package Piezas;

import Modelo.Tablero;

public class HombreLobo extends Pieza {

    public HombreLobo(String color, int fila, int columna) {
        super(6, 4, 5, color, "Hombre Lobo", fila, columna);
    }

    @Override
    public boolean mover(int nuevaFila, int nuevaColumna) {

        int diferenciaFila = Math.abs(nuevaFila - fila);
        int diferenciaColumna = Math.abs(nuevaColumna - columna);

        if (diferenciaFila <= 2
                && diferenciaColumna <= 2
                && (diferenciaFila != 0 || diferenciaColumna != 0)) {

            fila = nuevaFila;
            columna = nuevaColumna;
            return true;
        }

        return false;
    }

    @Override
    public void ataqueEspecial(Pieza enemigo) {
    }

    public boolean moverEspecial(Tablero tablero,
                                 int nuevaFila,
                                 int nuevaColumna) {

        int diferenciaFila = Math.abs(nuevaFila - fila);
        int diferenciaColumna = Math.abs(nuevaColumna - columna);

        if (!tablero.dentroTablero(nuevaFila, nuevaColumna)) {
            return false;
        }

        if (!tablero.casillaVacia(nuevaFila, nuevaColumna)) {
            return false;
        }

        if (diferenciaFila > 2 || diferenciaColumna > 2) {
            return false;
        }

        if (diferenciaFila == 0 && diferenciaColumna == 0) {
            return false;
        }

        fila = nuevaFila;
        columna = nuevaColumna;

        return true;
    }
}