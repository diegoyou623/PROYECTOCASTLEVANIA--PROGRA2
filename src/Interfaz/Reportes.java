

package Interfaz;

import Modelo.Jugador;
import Modelo.Sistema;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class Reportes extends JFrame {

    private Sistema sistema;

    private JButton btnRanking;
    private JButton btnHistorial;
    private JButton btnRegresar;

    private BufferedImage fondo;
    private BufferedImage marcoTitulo;
    private BufferedImage marcoBoton;
    private Font fuente;

    private final int ANCHO = 1920;
    private final int ALTO = 1080;

    public Reportes(Sistema sistema) {

        this.sistema = sistema;

        setTitle("Reportes");
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        cargarRecursos();
        crearInterfaz();
    }

    private void cargarRecursos() {

        try {

            fondo = ImageIO.read(
                    getClass().getResource(
                            "/Recursos/fondoReportes.jpg"
                    )
            );

            marcoTitulo = ImageIO.read(
                    getClass().getResource(
                            "/Recursos/textBoxDeUsuarioYcontraseña.png"
                    )
            );

            marcoBoton = ImageIO.read(
                    getClass().getResource(
                            "/Recursos/IngresarCrearJugadorYregresar.png"
                    )
            );

            InputStream archivoFuente =
                    getClass().getResourceAsStream(
                            "/Recursos/AppleGaramond-Bold.ttf"
                    );

            fuente = Font.createFont(
                    Font.TRUETYPE_FONT,
                    archivoFuente
            ).deriveFont(36f);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudieron cargar los recursos:\n"
                    + e.getMessage()
            );

            throw new RuntimeException(e);
        }
    }

    private BufferedImage escalar(
            BufferedImage imagen,
            double escala) {

        int ancho = (int) (imagen.getWidth() * escala);
        int alto = (int) (imagen.getHeight() * escala);

        BufferedImage resultado = new BufferedImage(
                ancho,
                alto,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = resultado.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        g.drawImage(imagen, 0, 0, ancho, alto, null);
        g.dispose();

        return resultado;
    }

    private void crearInterfaz() {

        BufferedImage titulo = escalar(marcoTitulo, 0.30);
        BufferedImage boton = escalar(marcoBoton, 0.24);

        int xTitulo = (ANCHO - titulo.getWidth()) / 2;
        int xBoton = (ANCHO - boton.getWidth()) / 2;

        int yTitulo = 50;
        int yRanking = 250;
        int yHistorial = 435;
        int yRegresar = 620;

        JPanel panel = new JPanel(null) {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                g.drawImage(
                        fondo,
                        0,
                        0,
                        ANCHO,
                        ALTO,
                        null
                );

                g.drawImage(titulo, xTitulo, yTitulo, null);
                g.drawImage(boton, xBoton, yRanking, null);
                g.drawImage(boton, xBoton, yHistorial, null);
                g.drawImage(boton, xBoton, yRegresar, null);
            }
        };

        setContentPane(panel);

        Color colorTexto = Color.decode("#FFFEAE");

        JLabel lblTitulo = crearEtiqueta(
                "REPORTES",
                52,
                colorTexto
        );

        lblTitulo.setBounds(
                xTitulo + 75,
                yTitulo + 75,
                titulo.getWidth() - 145,
                82
        );

        panel.add(lblTitulo);

        btnRanking = agregarBoton(
                panel,
                boton,
                xBoton,
                yRanking,
                "Ranking de jugadores",
                e -> mostrarRanking(),
                colorTexto
        );

        btnHistorial = agregarBoton(
                panel,
                boton,
                xBoton,
                yHistorial,
                "Historial",
                e -> mostrarHistorial(),
                colorTexto
        );

        btnRegresar = agregarBoton(
                panel,
                boton,
                xBoton,
                yRegresar,
                "Regresar",
                e -> regresar(),
                colorTexto
        );
    }

    private JButton agregarBoton(
            JPanel panel,
            BufferedImage imagen,
            int x,
            int y,
            String texto,
            ActionListener accion,
            Color colorTexto) {

        JLabel etiqueta = crearEtiqueta(
                texto,
                38,
                colorTexto
        );

        etiqueta.setBounds(
                x,
                y,
                imagen.getWidth(),
                imagen.getHeight()
        );

        panel.add(etiqueta);

        JButton boton = new JButton();

        boton.setBounds(
                x,
                y,
                imagen.getWidth(),
                imagen.getHeight()
        );

        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setFocusable(false);

        boton.addActionListener(accion);

        panel.add(boton);

        return boton;
    }

    private JLabel crearEtiqueta(
            String texto,
            int tamaño,
            Color color) {

        JLabel etiqueta = new JLabel(
                texto,
                SwingConstants.CENTER
        );

        etiqueta.setForeground(color);

        etiqueta.setFont(
                fuente.deriveFont(
                        Font.BOLD,
                        (float) tamaño
                )
        );

        return etiqueta;
    }

    private void mostrarRanking() {

        Jugador[] jugadores = sistema.getJugadores();

        int cantidad = sistema.getCantidadJugadores();

        for (int i = 0; i < cantidad - 1; i++) {

            for (int j = 0; j < cantidad - 1 - i; j++) {

                if (jugadores[j].getPuntos()
                        < jugadores[j + 1].getPuntos()) {

                    Jugador temporal = jugadores[j];

                    jugadores[j] = jugadores[j + 1];

                    jugadores[j + 1] = temporal;
                }
            }
        }

        String resultado = "RANKING DE JUGADORES\n\n";

        int posicion = 1;

        for (int i = 0; i < cantidad; i++) {

            if (jugadores[i] != null
                    && jugadores[i].isActivo()) {

                resultado += posicion
                        + ". "
                        + jugadores[i].getUsuario()
                        + " - "
                        + jugadores[i].getPuntos()
                        + " puntos\n";

                posicion++;
            }
        }

        mostrarTexto(resultado, "Ranking");
    }

    private void mostrarHistorial() {
        String[] historial = sistema.obtenerUltimosJuegosJugadorActivo();

        if (historial.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay partidas finalizadas para este jugador.",
                    "Historial",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String resultado = "MIS ÚLTIMOS 5 JUEGOS\n\n";
        for (int i = 0; i < historial.length; i++) {
            resultado += (i + 1) + ". " + historial[i] + "\n\n";
        }

        mostrarTexto(resultado, "Historial");
    }

    private void mostrarTexto(String texto, String titulo) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(fuente.deriveFont(20f));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(560, 330));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                titulo,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void regresar() {

        dispose();

        MenuPrincipal menu = new MenuPrincipal(sistema);

        menu.setVisible(true);
    }
}
