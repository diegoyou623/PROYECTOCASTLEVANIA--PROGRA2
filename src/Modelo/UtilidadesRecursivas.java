

package Modelo;

import Piezas.Pieza;

/**
 * Utilidades pequeñas para recorrer los arreglos del proyecto sin alterar
 * las reglas del juego. La clase y sus funciones finales cubren los
 * requisitos técnicos de la especificación.
 */
public final class UtilidadesRecursivas {

    private static final int TOTAL_CASILLAS = 36;

    private UtilidadesRecursivas() {
    }


    public static final boolean existeOponenteActivo(
            Jugador[] jugadores,
            int cantidadJugadores,
            Jugador jugadorActivo,
            int indice) {

        if (jugadores == null || indice >= cantidadJugadores) {
            return false;
        }

        Jugador candidato = jugadores[indice];

        if (candidato != null
                && candidato != jugadorActivo
                && candidato.isActivo()) {
            return true;
        }

        return existeOponenteActivo(
                jugadores,
                cantidadJugadores,
                jugadorActivo,
                indice + 1
        );
    }

  
    public static final boolean existePiezaDelColor(
            Tablero tablero,
            String color,
            int indice) {

        if (tablero == null || indice >= TOTAL_CASILLAS) {
            return false;
        }

        int fila = indice / 6;
        int columna = indice % 6;
        Pieza pieza = tablero.obtenerPieza(fila, columna);

        if (pieza != null && color.equals(pieza.getColor())) {
            return true;
        }

        return existePiezaDelColor(tablero, color, indice + 1);
    }
}

