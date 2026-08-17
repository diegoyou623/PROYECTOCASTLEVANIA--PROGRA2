



package Modelo;

public class Sistema {

    private Jugador[] jugadores;
    private int cantidadJugadores;
    private Jugador jugadorActivo;

   
    private RegistroPartida[] historialPartidas;
    private int cantidadPartidasRegistradas;

    public Sistema() {
        jugadores = new Jugador[50];
        cantidadJugadores = 0;
        jugadorActivo = null;
        historialPartidas = new RegistroPartida[50];
        cantidadPartidasRegistradas = 0;
    }

    public Jugador[] getJugadores() {
        return jugadores;
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    public Jugador getJugadorActivo() {
        return jugadorActivo;
    }

    public boolean registrarJugador(String usuario,
                                    String contraseña,
                                    String fechaIngreso)
            throws UsuarioDupException,
            ContraseñaException {

        if (usuario == null || usuario.isEmpty()) {
            return false;
        }

        if (contraseña == null || contraseña.length() != 5) {
            throw new ContraseñaException(
                    "La contraseña debe tener exactamente 5 caracteres."
            );
        }

        for (int i = 0; i < cantidadJugadores; i++) {
            if (jugadores[i].getUsuario().equals(usuario)) {
                throw new UsuarioDupException("El usuario ya existe.");
            }
        }

        if (cantidadJugadores >= jugadores.length) {
            return false;
        }

        Jugador nuevoJugador = new Jugador(usuario, contraseña, fechaIngreso);
        jugadores[cantidadJugadores] = nuevoJugador;
        cantidadJugadores++;
        jugadorActivo = nuevoJugador;
        return true;
    }

    public boolean iniciarSesion(String usuario, String contraseña) {
        for (int i = 0; i < cantidadJugadores; i++) {
            if (jugadores[i].getUsuario().equals(usuario)
                    && jugadores[i].getContraseña().equals(contraseña)
                    && jugadores[i].isActivo()) {
                jugadorActivo = jugadores[i];
                return true;
            }
        }
        return false;
    }

    public void cerrarSesion() {
        jugadorActivo = null;
    }

    public boolean cambiarContraseña(String nuevaContraseña)
            throws ContraseñaException {
        if (jugadorActivo == null) {
            return false;
        }

        if (nuevaContraseña == null || nuevaContraseña.length() != 5) {
            throw new ContraseñaException(
                    "La contraseña debe tener exactamente 5 caracteres."
            );
        }

        jugadorActivo.setContraseña(nuevaContraseña);
        return true;
    }

    public void cerrarCuenta() {
        if (jugadorActivo != null) {
            jugadorActivo.cerrarCuenta();
            jugadorActivo = null;
        }
    }

    public Jugador buscarJugador(String usuario) {
        for (int i = 0; i < cantidadJugadores; i++) {
            if (jugadores[i].getUsuario().equals(usuario)) {
                return jugadores[i];
            }
        }
        return null;
    }

    public boolean hayOponenteDisponible() {
        if (jugadorActivo == null) {
            return false;
        }

        return UtilidadesRecursivas.existeOponenteActivo(
                jugadores,
                cantidadJugadores,
                jugadorActivo,
                0
        );
    }

    public Jugador[] obtenerOponentes() {
        Jugador[] oponentes = new Jugador[cantidadJugadores];
        int posicion = 0;

        for (int i = 0; i < cantidadJugadores; i++) {
            if (jugadores[i] != jugadorActivo && jugadores[i].isActivo()) {
                oponentes[posicion] = jugadores[i];
                posicion++;
            }
        }
        return oponentes;
    }

    public Partida crearPartida(Jugador oponente) {
        if (jugadorActivo == null || oponente == null || jugadorActivo == oponente) {
            return null;
        }
        return new Partida(jugadorActivo, oponente);
    }

   
    public void registrarResultadoPartida(Partida partida, Jugador ganador, Jugador retirado) {
        if (partida == null || ganador == null) {
            return;
        }

        Jugador perdedor = ganador == partida.getJugador1()
                ? partida.getJugador2() : partida.getJugador1();

        ganador.sumarPuntos(3);

        String mensaje;
        if (retirado != null) {
            mensaje = retirado.getUsuario() + " se ha retirado. ¡Felicidades, "
                    + ganador.getUsuario() + ", has ganado 3 puntos!";
        } else {
            mensaje = ganador.getUsuario() + " venció a " + perdedor.getUsuario()
                    + ". ¡Felicidades, has ganado 3 puntos!";
        }

        guardarRegistro(new RegistroPartida(
                partida.getJugador1().getUsuario(),
                partida.getJugador2().getUsuario(),
                mensaje
        ));
    }

    public String[] obtenerUltimosJuegosJugadorActivo() {
        if (jugadorActivo == null) {
            return new String[0];
        }
        return obtenerUltimosJuegos(jugadorActivo.getUsuario());
    }

   
    public String[] obtenerUltimosJuegos(String usuario) {
        String[] resultadoTemporal = new String[5];
        int cantidad = 0;

        for (int i = cantidadPartidasRegistradas - 1; i >= 0 && cantidad < 5; i--) {
            RegistroPartida registro = historialPartidas[i];
            if (registro != null && registro.participa(usuario)) {
                resultadoTemporal[cantidad] = registro.getMensaje();
                cantidad++;
            }
        }

        String[] resultado = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            resultado[i] = resultadoTemporal[i];
        }
        return resultado;
    }

    private void guardarRegistro(RegistroPartida registro) {
        if (cantidadPartidasRegistradas == historialPartidas.length) {
            for (int i = 1; i < historialPartidas.length; i++) {
                historialPartidas[i - 1] = historialPartidas[i];
            }
            cantidadPartidasRegistradas--;
        }

        historialPartidas[cantidadPartidasRegistradas] = registro;
        cantidadPartidasRegistradas++;
    }

    private static class RegistroPartida {
        private final String jugador1;
        private final String jugador2;
        private final String mensaje;

        RegistroPartida(String jugador1, String jugador2, String mensaje) {
            this.jugador1 = jugador1;
            this.jugador2 = jugador2;
            this.mensaje = mensaje;
        }

        boolean participa(String usuario) {
            return jugador1.equals(usuario) || jugador2.equals(usuario);
        }

        String getMensaje() {
            return mensaje;
        }
    }
}
