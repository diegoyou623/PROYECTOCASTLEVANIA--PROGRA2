package Piezas;

public class Vampiro extends Pieza {



    public Vampiro(String color, int fila, int columna) {
        super(4, 5, 3, color, "Vampiro", fila, columna);
    }
    

    @Override
    public void atacar(Pieza enemigo) {
        
    }



    @Override
    public void ataqueEspecial(Pieza enemigo) {
        enemigo.recibirDaño(1);

        if (vida < 4) {
        vida++;
    }
    }
}
   
