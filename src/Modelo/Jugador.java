

package Modelo;

import Piezas.Pieza;

public class Jugador {

    private String usuario;
    private String contraseña;
    private int puntos;
    private String fechaIngreso;
    private boolean activo;
    private Pieza[] piezas;

    public Jugador(String usuario, String contraseña, String fechaIngreso) {
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.fechaIngreso = fechaIngreso;
        this.puntos = 0;
        this.activo = true;
        this.piezas = new Pieza[10];
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public int getPuntos() {
        return puntos;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public boolean isActivo() {
        return activo;
    }

    public Pieza[] getPiezas() {
        return piezas;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void sumarPuntos(int puntos) {
        this.puntos += puntos;
    }

    public void cerrarCuenta() {
        activo = false;
    }

    public boolean agregarPieza(Pieza pieza) {

        for (int i = 0; i < piezas.length; i++) {

            if (piezas[i] == null) {
                piezas[i] = pieza;
                return true;
            }
        }

        return false;
    }
}