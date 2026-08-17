


package Piezas;

import Modelo.Tablero;

public class Necromante extends Pieza {

    public Necromante(String color, int fila, int columna) {
        super(5, 3, 4, color, "Necromante", fila, columna);
    }

    @Override
    public void ataqueEspecial(Pieza enemigo) {
        enemigo.recibirDañoDirecto(2);
    }

    public boolean invocarZombie(Tablero tablero,
                                 int fila,
                                 int columna) {

        return tablero.invocarZombie(this, fila, columna);
    }

    public boolean atacarConZombie(Tablero tablero,
                                   int filaZombie,
                                   int columnaZombie,
                                   int filaEnemigo,
                                   int columnaEnemigo) {

        Pieza zombie =
                tablero.obtenerPieza(filaZombie, columnaZombie);

        Pieza enemigo =
                tablero.obtenerPieza(filaEnemigo, columnaEnemigo);

        if (zombie == null || enemigo == null) {
            return false;
        }

        if (!zombie.getTipo().equals("Zombie")) {
            return false;
        }

        if (!zombie.getColor().equals(color)) {
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

        enemigo.recibirDaño(1);

        if (enemigo.getVida() <= 0) {
            tablero.getCasilla(
                    filaEnemigo,
                    columnaEnemigo
            ).setPieza(null);
        }

        return true;
    }
}