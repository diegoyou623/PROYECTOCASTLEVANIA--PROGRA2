




package Modelo;

import Piezas.Pieza;

public class Partida {

    private Jugador jugador1;
    private Jugador jugador2;
    private Tablero tablero;
    private Ruleta ruleta;
    private Jugador jugadorActual;
    private String tipoSeleccionado;
    private boolean finalizada;

    public Partida(Jugador jugador1, Jugador jugador2) {

        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.tablero = new Tablero();
        this.ruleta = new Ruleta();
        this.jugadorActual = jugador1;
        this.tipoSeleccionado = null;
        this.finalizada = false;

        tablero.colocarPiezasIniciales();
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Ruleta getRuleta() {
        return ruleta;
    }

    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    public String getTipoSeleccionado() {
        return tipoSeleccionado;
    }

    public boolean isFinalizada() {
        return finalizada;
    }
    
    public void finalizarPartida() {
    finalizada = true;
}

    public String girarRuleta() {

        tipoSeleccionado = ruleta.girar();

        return tipoSeleccionado;
    }

    public boolean puedeMoverPieza(Pieza pieza) {

        if (pieza == null) {
            return false;
        }

        if (tipoSeleccionado == null) {
            return false;
        }

        String colorActual;

        if (jugadorActual == jugador1) {
            colorActual = "Blanco";
        } else {
            colorActual = "Negro";
        }

        if (!pieza.getColor().equals(colorActual)) {
            return false;
        }

        if (!pieza.getTipo().equals(tipoSeleccionado)) {
            return false;
        }

        return true;
    }

    public boolean moverPieza(int filaOrigen,
                              int columnaOrigen,
                              int filaDestino,
                              int columnaDestino) {

        if (finalizada) {
            return false;
        }

        Pieza pieza =
                tablero.obtenerPieza(filaOrigen, columnaOrigen);

        if (!puedeMoverPieza(pieza)) {
            return false;
        }

        boolean movimientoRealizado =
                tablero.moverPieza(
                        filaOrigen,
                        columnaOrigen,
                        filaDestino,
                        columnaDestino
                );

        if (movimientoRealizado) {
            cambiarTurno();
        }

        return movimientoRealizado;
    }

    public boolean atacar(int filaOrigen,
                          int columnaOrigen,
                          int filaDestino,
                          int columnaDestino) {

        if (finalizada) {
            return false;
        }

        Pieza pieza =
                tablero.obtenerPieza(filaOrigen, columnaOrigen);

        if (!puedeMoverPieza(pieza)) {
            return false;
        }

        boolean ataqueRealizado =
                tablero.atacar(
                        filaOrigen,
                        columnaOrigen,
                        filaDestino,
                        columnaDestino
                );

        if (ataqueRealizado) {
            cambiarTurno();
        }

        comprobarFinPartida();

        return ataqueRealizado;
    }

    public boolean ataqueEspecialVampiro(
            int filaOrigen,
            int columnaOrigen,
            int filaDestino,
            int columnaDestino) {

        if (finalizada) {
            return false;
        }

        Pieza pieza =
                tablero.obtenerPieza(filaOrigen, columnaOrigen);

        if (!puedeMoverPieza(pieza)) {
            return false;
        }

        boolean realizado =
                tablero.ataqueEspecialVampiro(
                        filaOrigen,
                        columnaOrigen,
                        filaDestino,
                        columnaDestino
                );

        if (realizado) {
            cambiarTurno();
        }

        comprobarFinPartida();

        return realizado;
    }

    public boolean ataqueLanza(int filaOrigen,
                               int columnaOrigen,
                               int filaDestino,
                               int columnaDestino) {

        if (finalizada) {
            return false;
        }

        Pieza pieza =
                tablero.obtenerPieza(filaOrigen, columnaOrigen);

        if (!puedeMoverPieza(pieza)) {
            return false;
        }

        boolean realizado =
                tablero.ataqueLanza(
                        filaOrigen,
                        columnaOrigen,
                        filaDestino,
                        columnaDestino
                );

        if (realizado) {
            cambiarTurno();
        }

        comprobarFinPartida();

        return realizado;
    }

    public boolean invocarZombie(int fila,
                                 int columna) {

        if (finalizada) {
            return false;
        }

        Pieza pieza =
                tablero.obtenerPieza(
                        obtenerFilaNecromante(),
                        obtenerColumnaNecromante()
                );

        if (pieza == null
                || !pieza.getTipo().equals("Necromante")
                || !pieza.getColor().equals(obtenerColorActual())) {

            return false;
        }

        boolean realizado =
                tablero.invocarZombie(
                        (Piezas.Necromante) pieza,
                        fila,
                        columna
                );

        if (realizado) {
            cambiarTurno();
        }

        return realizado;
    }

    public boolean ataqueConZombie(int filaNecromante,
                                   int columnaNecromante,
                                   int filaZombie,
                                   int columnaZombie,
                                   int filaEnemigo,
                                   int columnaEnemigo) {

        if (finalizada) {
            return false;
        }

        Pieza necromante = tablero.obtenerPieza(filaNecromante, columnaNecromante);

        if (!puedeMoverPieza(necromante)
                || !"Necromante".equals(necromante.getTipo())) {
            return false;
        }

        int distanciaFila = Math.abs(filaEnemigo - filaNecromante);
        int distanciaColumna = Math.abs(columnaEnemigo - columnaNecromante);

       
        if (Math.max(distanciaFila, distanciaColumna) <= 2) {
            return false;
        }

        boolean realizado =
                tablero.ataqueConZombie(
                        filaZombie,
                        columnaZombie,
                        filaEnemigo,
                        columnaEnemigo
                );

        if (realizado) {
            cambiarTurno();
        }

        comprobarFinPartida();

        return realizado;
    }

    private String obtenerColorActual() {

        if (jugadorActual == jugador1) {
            return "Blanco";
        }

        return "Negro";
    }

    private int obtenerFilaNecromante() {

        for (int i = 0; i < 6; i++) {

            for (int j = 0; j < 6; j++) {

                Pieza pieza = tablero.obtenerPieza(i, j);

                if (pieza != null
                        && pieza.getTipo().equals("Necromante")
                        && pieza.getColor().equals(obtenerColorActual())) {

                    return i;
                }
            }
        }

        return -1;
    }

    private int obtenerColumnaNecromante() {

        for (int i = 0; i < 6; i++) {

            for (int j = 0; j < 6; j++) {

                Pieza pieza = tablero.obtenerPieza(i, j);

                if (pieza != null
                        && pieza.getTipo().equals("Necromante")
                        && pieza.getColor().equals(obtenerColorActual())) {

                    return j;
                }
            }
        }

        return -1;
    }

    private void cambiarTurno() {

        if (jugadorActual == jugador1) {
            jugadorActual = jugador2;
        } else {
            jugadorActual = jugador1;
        }

        tipoSeleccionado = null;
    }

    private void comprobarFinPartida() {
        boolean blanco = UtilidadesRecursivas.existePiezaDelColor(
                tablero, "Blanco", 0
        );
        boolean negro = UtilidadesRecursivas.existePiezaDelColor(
                tablero, "Negro", 0
        );

        if (!blanco || !negro) {
            finalizada = true;
        }
    }
    
    public Jugador obtenerGanador() {
    boolean blancoVivo = UtilidadesRecursivas.existePiezaDelColor(
            tablero, "Blanco", 0
    );
    boolean negroVivo = UtilidadesRecursivas.existePiezaDelColor(
            tablero, "Negro", 0
    );

    if (blancoVivo && !negroVivo) {
        return jugador1;
    }

    if (negroVivo && !blancoVivo) {
        return jugador2;
    }

    return null;
}
}



