package Piezas;

public abstract class Pieza {

   
    protected int vida;
    protected int escudo;
    protected int ataque;
    protected String color;
    protected String tipo;
    protected int fila;
    protected int columna;

   
    public Pieza(int vida, int escudo, int ataque,String color, String tipo, int fila, int columna) {
        
    this.vida = vida;
    this.escudo = escudo;
    this.ataque = ataque;
    this.color = color;
    this.tipo = tipo;
    this.fila = fila;
    this.columna = columna;
    }

 
    public int getVida() {
        return vida;
    }

    public int getEscudo() {
        return escudo;
    }

    public int getAtaque() {
        return ataque;
    }

    public String getColor() {
        return color;
    }

    public String getTipo() {
        return tipo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }


    public void setFila(int fila) {
        this.fila = fila;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }


    public boolean mover(int nuevaFila, int nuevaColumna) {
        this.fila = nuevaFila;
        this.columna = nuevaColumna;
        
        return true;
    }

    public void atacar(Pieza enemigo) {
            enemigo.recibirDaño(ataque);
    }

    public boolean recibirDaño(int daño) {
        if (escudo >= daño) {
        escudo = escudo - daño;
         } 
        
        else {
        int dañoRestante = daño - escudo;
        escudo = 0;
        vida = vida - dañoRestante;
    }

        return vida <= 0;
    }
    
    public void recibirDañoDirecto(int daño) {
    vida = vida - daño;
}


    public abstract void ataqueEspecial(Pieza enemigo);
}


  