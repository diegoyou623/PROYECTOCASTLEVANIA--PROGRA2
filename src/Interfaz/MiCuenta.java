

package Interfaz;

import Modelo.ContraseñaException;
import Modelo.Sistema;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class MiCuenta extends JFrame {

    private Sistema sistema;

    private JButton btnInformacion;
    private JButton btnCambiarContraseña;
    private JButton btnCerrarCuenta;
    private JButton btnRegresar;

    private BufferedImage fondo;
    private BufferedImage marco;
    private Font fuente;

    private final int ANCHO = 1920;
    private final int ALTO = 1080;

    public MiCuenta(Sistema sistema) {

        this.sistema = sistema;

        setTitle("Mi Cuenta");
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
                            "/Recursos/sleepingKnight.jpg"
                    )
            );

            marco = ImageIO.read(
                    getClass().getResource(
                            "/Recursos/usuarioYcontraseña.png"
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

        BufferedImage marcoEscalado = escalar(marco, 0.30);

        int xMarco = 60;

        int yInformacion = 160;
        int yContraseña = 335;
        int yCerrarCuenta = 510;
        int yRegresar = 685;

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

                g.drawImage(marcoEscalado, xMarco, yInformacion, null);
                g.drawImage(marcoEscalado, xMarco, yContraseña, null);
                g.drawImage(marcoEscalado, xMarco, yCerrarCuenta, null);
                g.drawImage(marcoEscalado, xMarco, yRegresar, null);
            }
        };

        setContentPane(panel);

        Color colorTexto = Color.decode("#FFFEAE");

        JLabel lblTitulo = crearEtiqueta(
                "Mi Cuenta",
                60,
                colorTexto
        );

        lblTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        lblTitulo.setBounds(90, 65, 500, 90);

        panel.add(lblTitulo);

        btnInformacion = agregarBoton(
                panel,
                marcoEscalado,
                xMarco,
                yInformacion,
                "Ver mi información",
                e -> mostrarInformacion(),
                colorTexto
        );

        btnCambiarContraseña = agregarBoton(
                panel,
                marcoEscalado,
                xMarco,
                yContraseña,
                "Cambiar contraseña",
                e -> cambiarContraseña(),
                colorTexto
        );

        btnCerrarCuenta = agregarBoton(
                panel,
                marcoEscalado,
                xMarco,
                yCerrarCuenta,
                "Cerrar mi cuenta",
                e -> cerrarCuenta(),
                colorTexto
        );

        btnRegresar = agregarBoton(
                panel,
                marcoEscalado,
                xMarco,
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
                x + 150,
                y + 75,
                imagen.getWidth() - 185,
                70
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

    private void mostrarInformacion() {

        if (sistema.getJugadorActivo() == null) {
            return;
        }

        String informacion =
                "Usuario: "
                + sistema.getJugadorActivo().getUsuario()
                + "\nPuntos: "
                + sistema.getJugadorActivo().getPuntos()
                + "\nFecha de ingreso: "
                + sistema.getJugadorActivo().getFechaIngreso()
                + "\nActivo: "
                + sistema.getJugadorActivo().isActivo();

        JOptionPane.showMessageDialog(
                this,
                informacion,
                "Información de cuenta",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void cambiarContraseña() {

        String nuevaContraseña =
                JOptionPane.showInputDialog(
                        this,
                        "Ingrese la nueva contraseña:"
                );

        if (nuevaContraseña == null) {
            return;
        }

        try {

            sistema.cambiarContraseña(nuevaContraseña);

            JOptionPane.showMessageDialog(
                    this,
                    "Contraseña cambiada correctamente."
            );

        } catch (ContraseñaException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cerrarCuenta() {

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de que desea cerrar su cuenta?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION
                );

        if (respuesta == JOptionPane.YES_OPTION) {

            sistema.cerrarCuenta();

            JOptionPane.showMessageDialog(
                    this,
                    "La cuenta ha sido cerrada."
            );

            dispose();

            Login login = new Login(sistema);
            login.setVisible(true);
        }
    }

    private void regresar() {

        dispose();

        MenuPrincipal menu =
                new MenuPrincipal(sistema);

        menu.setVisible(true);
    }
}