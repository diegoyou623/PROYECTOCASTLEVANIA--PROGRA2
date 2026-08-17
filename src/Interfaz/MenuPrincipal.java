

package Interfaz;

import Modelo.Sistema;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MenuPrincipal extends JFrame {

    private final Sistema sistema;

    private JButton btnJugar;
    private JButton btnMiCuenta;
    private JButton btnReportes;
    private JButton btnCerrarSesion;

    private Font fuente;
    private BufferedImage fondo;
    private BufferedImage assetTitulo;
    private BufferedImage assetBoton;

    private static final int ANCHO = 1920;
    private static final int ALTO = 1080;

    public MenuPrincipal(Sistema sistema) {

        this.sistema = sistema;

        setTitle("Vampire Wargame");
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

           
            fondo = cargarImagen("/Recursos/fondoMenuPrincipal.png", "/Recursos/darkCastle.jpg");
            assetTitulo = cargarImagen("/Recursos/textBoxDeUsuarioYcontraseña.png");
            assetBoton = cargarImagen("/Recursos/IngresarCrearJugadorYregresar.png");

            if (fondo == null || assetTitulo == null || assetBoton == null) {
                throw new IOException("Falta uno de los recursos del menú en la carpeta src/Recursos.");
            }

            InputStream archivoFuente =
                    getClass().getResourceAsStream(
                            "/Recursos/AppleGaramond-Bold.ttf"
                    );

            if (archivoFuente == null) {
                fuente = new Font("Serif", Font.BOLD, 36);
            } else {
                Font fuenteBase = Font.createFont(
                        Font.TRUETYPE_FONT,
                        archivoFuente
                );
                fuente = fuenteBase.deriveFont(36f);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error cargando recursos:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            fuente = new Font("Serif", Font.BOLD, 36);
        }
    }

    private BufferedImage cargarImagen(String... rutas) throws IOException {
        for (String ruta : rutas) {
            URL recurso = getClass().getResource(ruta);
            if (recurso != null) {
                BufferedImage imagen = ImageIO.read(recurso);
                if (imagen != null) return imagen;
            }
        }
        return null;
    }

    private BufferedImage escalar(
            BufferedImage imagen,
            double escala) {

        int ancho = (int) Math.round(imagen.getWidth() * escala);
        int alto = (int) Math.round(imagen.getHeight() * escala);

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

        g.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        g.drawImage(imagen, 0, 0, ancho, alto, null);
        g.dispose();

        return resultado;
    }

    private void crearInterfaz() {

        BufferedImage titulo = escalar(assetTitulo, 0.30);
        BufferedImage boton = escalar(assetBoton, 0.24);

        Point posTitulo = new Point(
                (ANCHO - titulo.getWidth()) / 2,
                70
        );

        int xBoton = (ANCHO - boton.getWidth()) / 2;
        Point posJugar = new Point(xBoton, 250);
        Point posMiCuenta = new Point(xBoton, 385);
        Point posReportes = new Point(xBoton, 520);
        Point posCerrarSesion = new Point(xBoton, 655);

        JPanel panel = new JPanel(null) {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY
                );

                g2.drawImage(fondo, 0, 0, ANCHO, ALTO, null);
                g2.drawImage(titulo, posTitulo.x, posTitulo.y, null);
                g2.drawImage(boton, posJugar.x, posJugar.y, null);
                g2.drawImage(boton, posMiCuenta.x, posMiCuenta.y, null);
                g2.drawImage(boton, posReportes.x, posReportes.y, null);
                g2.drawImage(
                        boton,
                        posCerrarSesion.x,
                        posCerrarSesion.y,
                        null
                );

                g2.dispose();
            }
        };

        setContentPane(panel);

        Color colorTexto = Color.decode("#FFFEAE");

        JLabel lblTitulo = crearEtiqueta(
                "Vampire Wargame",
                52,
                colorTexto
        );

        lblTitulo.setBounds(
                posTitulo.x + 110,
                posTitulo.y + 50,
                titulo.getWidth() - 145,
                82
        );

        panel.add(lblTitulo);

        agregarBoton(
                panel,
                boton,
                posJugar,
                "Jugar",
                e -> abrirJuego(),
                colorTexto
        );

        agregarBoton(
                panel,
                boton,
                posMiCuenta,
                "Mi cuenta",
                e -> abrirMiCuenta(),
                colorTexto
        );

        agregarBoton(
                panel,
                boton,
                posReportes,
                "Reportes",
                e -> abrirReportes(),
                colorTexto
        );

        agregarBoton(
                panel,
                boton,
                posCerrarSesion,
                "Agregar Cuenta",
                e -> cerrarSesion(),
                colorTexto
        );
    }

    private void agregarBoton(
            JPanel panel,
            BufferedImage imagenBoton,
            Point posicion,
            String texto,
            java.awt.event.ActionListener accion,
            Color colorTexto) {

        JLabel etiqueta = crearEtiqueta(texto, 38, colorTexto);

        etiqueta.setBounds(
                posicion.x,
                posicion.y,
                imagenBoton.getWidth(),
                imagenBoton.getHeight()
        );

        panel.add(etiqueta);

        JButton boton = crearBotonInvisible();

        boton.setBounds(
                posicion.x,
                posicion.y,
                imagenBoton.getWidth(),
                imagenBoton.getHeight()
        );

        boton.addActionListener(accion);
        panel.add(boton);

        if ("Jugar".equals(texto)) {
            btnJugar = boton;
        } else if ("Mi cuenta".equals(texto)) {
            btnMiCuenta = boton;
        } else if ("Reportes".equals(texto)) {
            btnReportes = boton;
        } else {
            btnCerrarSesion = boton;
        }
    }

    private JLabel crearEtiqueta(
            String texto,
            int tamaño,
            Color color) {

        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);

        etiqueta.setForeground(color);
        etiqueta.setFont(fuente.deriveFont(Font.BOLD, (float) tamaño));

        return etiqueta;
    }

    private JButton crearBotonInvisible() {

        JButton boton = new JButton();

        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(null);
        boton.setFocusable(false);

        return boton;
    }

    private void abrirJuego() {

        if (!sistema.hayOponenteDisponible()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No hay otro jugador disponible para iniciar una partida.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        Juego juego = new Juego(sistema);

      
        if (!juego.tienePartidaIniciada()) {
            return;
        }

        juego.setVisible(true);
        dispose();
    }

    private void abrirMiCuenta() {

        MiCuenta cuenta = new MiCuenta(sistema);

        cuenta.setVisible(true);

        dispose();
    }

    private void abrirReportes() {

        Reportes reportes = new Reportes(sistema);

        reportes.setVisible(true);

        dispose();
    }

    private void cerrarSesion() {

        sistema.cerrarSesion();

        dispose();

        Login login = new Login(sistema);

        login.setVisible(true);
    }
}
