


package Piezas;

public class Zombie extends Pieza {

    public Zombie(String color, int fila, int columna) {
        super(1, 1, 1, color, "Zombie", fila, columna);
    }

    @Override
    public boolean mover(int nuevaFila, int nuevaColumna) {
        return false;
    }

    @Override
    public void ataqueEspecial(Pieza enemigo) {
    }
}