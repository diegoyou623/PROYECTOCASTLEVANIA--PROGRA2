
package Modelo;

import java.util.Random;

public class Ruleta {

    private Random random;

    public Ruleta() {
        random = new Random();
    }

    public String girar() {

        int resultado = random.nextInt(3);

        if (resultado == 0) {
            return "Vampiro";
        } else if (resultado == 1) {
            return "Hombre Lobo";
        } else {
            return "Necromante";
        }
    }
}
