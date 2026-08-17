

package Modelo;

import Piezas.HombreLobo;
import Piezas.Necromante;
import Piezas.Pieza;
import Piezas.Vampiro;
import Piezas.Zombie;

public class Tablero {

    private Casilla[][] tablero;

    public Tablero() {
        tablero = new Casilla[6][6];

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                tablero[i][j] = new Casilla(i, j);
            }
        }
    }

    public Casilla getCasilla(int fila, int columna) {
        return tablero[fila][columna];
    }

    public boolean dentroTablero(int fila, int columna) {
        return fila >= 0 && fila < 6
                && columna >= 0 && columna < 6;
    }

    public boolean casillaVacia(int fila, int columna) {

        if (!dentroTablero(fila, columna)) {
            return false;
        }

        return tablero[fila][columna].estaVacia();
    }

    public Pieza obtenerPieza(int fila, int columna) {

        if (!dentroTablero(fila, columna)) {
            return null;
        }

        return tablero[fila][columna].getPieza();
    }

    public boolean colocarPieza(Pieza pieza, int fila, int columna) {

        if (!dentroTablero(fila, columna)) {
            return false;
        }

        if (!tablero[fila][columna].estaVacia()) {
            return false;
        }

        tablero[fila][columna].setPieza(pieza);

        pieza.setFila(fila);
        pieza.setColumna(columna);

        return true;
    }

    public boolean moverPieza(int filaOrigen, int columnaOrigen,
                              int filaDestino, int columnaDestino) {

        if (!dentroTablero(filaOrigen, columnaOrigen)
                || !dentroTablero(filaDestino, columnaDestino)) {
            return false;
        }

        Casilla origen = tablero[filaOrigen][columnaOrigen];
        Casilla destino = tablero[filaDestino][columnaDestino];

        if (origen.estaVacia()) {
            return false;
        }

        if (!destino.estaVacia()) {
            return false;
        }

        Pieza pieza = origen.getPieza();

        int diferenciaFila =
                Math.abs(filaDestino - filaOrigen);

        int diferenciaColumna =
                Math.abs(columnaDestino - columnaOrigen);

        if (diferenciaFila == 0 && diferenciaColumna == 0) {
            return false;
        }

        if (pieza instanceof Zombie) {
            return false;
        }

        if (pieza instanceof HombreLobo) {

            if (diferenciaFila > 2 || diferenciaColumna > 2) {
                return false;
            }

            if (diferenciaFila == 2 || diferenciaColumna == 2) {

                int pasoFila = 0;
                int pasoColumna = 0;

                if (filaDestino > filaOrigen) {
                    pasoFila = 1;
                } else if (filaDestino < filaOrigen) {
                    pasoFila = -1;
                }

                if (columnaDestino > columnaOrigen) {
                    pasoColumna = 1;
                } else if (columnaDestino < columnaOrigen) {
                    pasoColumna = -1;
                }

                int filaIntermedia = filaOrigen + pasoFila;
                int columnaIntermedia = columnaOrigen + pasoColumna;

                if (!casillaVacia(filaIntermedia, columnaIntermedia)) {
                    return false;
                }
            }

        } else {

            if (diferenciaFila > 1 || diferenciaColumna > 1) {
                return false;
            }
        }

        if (!pieza.mover(filaDestino, columnaDestino)) {
            return false;
        }

        destino.setPieza(pieza);
        origen.setPieza(null);

        return true;
    }

    public boolean atacar(int filaOrigen, int columnaOrigen,
                          int filaDestino, int columnaDestino) {

        if (!dentroTablero(filaOrigen, columnaOrigen)
                || !dentroTablero(filaDestino, columnaDestino)) {
            return false;
        }

        Pieza atacante = obtenerPieza(filaOrigen, columnaOrigen);
        Pieza enemigo = obtenerPieza(filaDestino, columnaDestino);

        if (atacante == null || enemigo == null) {
            return false;
        }

        if (atacante.getColor().equals(enemigo.getColor())) {
            return false;
        }

        int diferenciaFila =
                Math.abs(filaDestino - filaOrigen);

        int diferenciaColumna =
                Math.abs(columnaDestino - columnaOrigen);

        if (diferenciaFila > 1 || diferenciaColumna > 1) {
            return false;
        }

        if (diferenciaFila == 0 && diferenciaColumna == 0) {
            return false;
        }

        boolean murio =
                enemigo.recibirDaño(atacante.getAtaque());

        if (murio) {
            tablero[filaDestino][columnaDestino].setPieza(null);
        }

        return true;
    }

    public boolean ataqueEspecialVampiro(int filaOrigen,
                                         int columnaOrigen,
                                         int filaDestino,
                                         int columnaDestino) {

        Pieza atacante = obtenerPieza(filaOrigen, columnaOrigen);
        Pieza enemigo = obtenerPieza(filaDestino, columnaDestino);

        if (!(atacante instanceof Vampiro)) {
            return false;
        }

        if (enemigo == null) {
            return false;
        }

        if (atacante.getColor().equals(enemigo.getColor())) {
            return false;
        }

        int diferenciaFila =
                Math.abs(filaDestino - filaOrigen);

        int diferenciaColumna =
                Math.abs(columnaDestino - columnaOrigen);

        if (diferenciaFila > 1 || diferenciaColumna > 1) {
            return false;
        }

        if (diferenciaFila == 0 && diferenciaColumna == 0) {
            return false;
        }

        atacante.ataqueEspecial(enemigo);

        if (enemigo.getVida() <= 0) {
            tablero[filaDestino][columnaDestino].setPieza(null);
        }

        return true;
    }

    public boolean ataqueLanza(int filaOrigen,
                               int columnaOrigen,
                               int filaDestino,
                               int columnaDestino) {

        Pieza atacante = obtenerPieza(filaOrigen, columnaOrigen);
        Pieza enemigo = obtenerPieza(filaDestino, columnaDestino);

        if (!(atacante instanceof Necromante)) {
            return false;
        }

        if (enemigo == null) {
            return false;
        }

        if (atacante.getColor().equals(enemigo.getColor())) {
            return false;
        }

        int diferenciaFila =
                Math.abs(filaDestino - filaOrigen);

        int diferenciaColumna =
                Math.abs(columnaDestino - columnaOrigen);

        if (!((diferenciaFila == 2 && diferenciaColumna == 0)
                || (diferenciaFila == 0 && diferenciaColumna == 2))) {
            return false;
        }

        int pasoFila = 0;
        int pasoColumna = 0;

        if (filaDestino > filaOrigen) {
            pasoFila = 1;
        } else if (filaDestino < filaOrigen) {
            pasoFila = -1;
        }

        if (columnaDestino > columnaOrigen) {
            pasoColumna = 1;
        } else if (columnaDestino < columnaOrigen) {
            pasoColumna = -1;
        }

        int filaIntermedia = filaOrigen + pasoFila;
        int columnaIntermedia = columnaOrigen + pasoColumna;

        if (!casillaVacia(filaIntermedia, columnaIntermedia)) {
            return false;
        }

        atacante.ataqueEspecial(enemigo);

        if (enemigo.getVida() <= 0) {
            tablero[filaDestino][columnaDestino].setPieza(null);
        }

        return true;
    }

    public boolean invocarZombie(Necromante necromante,
                                 int fila,
                                 int columna) {

        if (!dentroTablero(fila, columna)) {
            return false;
        }

        if (!casillaVacia(fila, columna)) {
            return false;
        }

        Zombie zombie = new Zombie(
                necromante.getColor(),
                fila,
                columna
        );

        return colocarPieza(zombie, fila, columna);
    }

    public boolean ataqueConZombie(int filaZombie,
                                   int columnaZombie,
                                   int filaEnemigo,
                                   int columnaEnemigo) {

        Pieza zombie = obtenerPieza(filaZombie, columnaZombie);
        Pieza enemigo = obtenerPieza(filaEnemigo, columnaEnemigo);

        if (!(zombie instanceof Zombie)) {
            return false;
        }

        if (enemigo == null) {
            return false;
        }

        if (zombie.getColor().equals(enemigo.getColor())) {
            return false;
        }

        int diferenciaFila =
                Math.abs(filaEnemigo - filaZombie);

        int diferenciaColumna =
                Math.abs(columnaEnemigo - columnaZombie);

        if (diferenciaFila > 1 || diferenciaColumna > 1) {
            return false;
        }

        if (diferenciaFila == 0 && diferenciaColumna == 0) {
            return false;
        }

        boolean murio = enemigo.recibirDaño(1);

        if (murio) {
            tablero[filaEnemigo][columnaEnemigo].setPieza(null);
        }

        return true;
    }

    public void colocarPiezasIniciales() {

        colocarPieza(
                new HombreLobo("Blanco", 0, 0),
                0, 0
        );

        colocarPieza(
                new Vampiro("Blanco", 0, 1),
                0, 1
        );

        colocarPieza(
                new Necromante("Blanco", 0, 2),
                0, 2
        );

        colocarPieza(
                new Necromante("Blanco", 0, 3),
                0, 3
        );

        colocarPieza(
                new Vampiro("Blanco", 0, 4),
                0, 4
        );

        colocarPieza(
                new HombreLobo("Blanco", 0, 5),
                0, 5
        );

        colocarPieza(
                new HombreLobo("Negro", 5, 0),
                5, 0
        );

        colocarPieza(
                new Vampiro("Negro", 5, 1),
                5, 1
        );

        colocarPieza(
                new Necromante("Negro", 5, 2),
                5, 2
        );

        colocarPieza(
                new Necromante("Negro", 5, 3),
                5, 3
        );

        colocarPieza(
                new Vampiro("Negro", 5, 4),
                5, 4
        );

        colocarPieza(
                new HombreLobo("Negro", 5, 5),
                5, 5
        );
    }
}