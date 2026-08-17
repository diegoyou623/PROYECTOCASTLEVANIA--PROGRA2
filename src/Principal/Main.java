

package Principal;

import Interfaz.Login;
import Modelo.Sistema;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Main {

  
    private static Clip musicaFondo;

    public static void main(String[] args) {
        reproducirMusicaFondo();

        Sistema sistema = new Sistema();
        Login login = new Login(sistema);
        login.setVisible(true);
    }

    private static void reproducirMusicaFondo() {
        URL recurso = Main.class.getResource("/Recursos/musica.wav");
        if (recurso == null) return;

        try (AudioInputStream audio = AudioSystem.getAudioInputStream(recurso)) {
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audio);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
            musicaFondo.start();
        } catch (Exception e) {
            musicaFondo = null;
        }
    }
}
