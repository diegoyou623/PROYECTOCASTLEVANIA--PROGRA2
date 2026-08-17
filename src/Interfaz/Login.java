




package Interfaz;

import Modelo.ContraseñaException;
import Modelo.Sistema;
import Modelo.UsuarioDupException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class Login extends JFrame {

    private Sistema sistema;

    private JTextField txtUsuario;
    private JPasswordField txtContraseña;

    private JButton btnIngresar;
    private JButton btnCrear;
    private JButton btnRegresar;

    private Font fuente;

    private BufferedImage fondo;
    private BufferedImage assetUsuario;
    private BufferedImage assetTextbox;
    private BufferedImage assetBoton;

    private final int ANCHO = 1920;
    private final int ALTO = 1080;

    public Login(Sistema sistema) {

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

            fondo = ImageIO.read(
                    getClass().getResource(
                            "/Recursos/fondoDeLogIn.png"
                    )
            );

            assetUsuario = ImageIO.read(
                    getClass().getResource(
                            "/Recursos/usuarioYcontraseña.png"
                    )
            );

            assetTextbox = ImageIO.read(
                    getClass().getResource(
                            "/Recursos/textBoxDeUsuarioYcontraseña.png"
                    )
            );

            assetBoton = ImageIO.read(
                    getClass().getResource(
                            "/Recursos/IngresarCrearJugadorYregresar.png"
                    )
            );

            InputStream archivoFuente =
                    getClass().getResourceAsStream(
                            "/Recursos/AppleGaramond-Bold.ttf"
                    );

            Font fuenteBase =
                    Font.createFont(
                            Font.TRUETYPE_FONT,
                            archivoFuente
                    );

            fuente = fuenteBase.deriveFont(34f);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error cargando recursos:\n"
                    + e.getMessage()
            );

            fuente = new Font(
                    "Serif",
                    Font.BOLD,
                    34
            );
        }
    }

    private BufferedImage escalar(
            BufferedImage imagen,
            double escala) {

        int ancho =
                (int) Math.round(
                        imagen.getWidth() * escala
                );

        int alto =
                (int) Math.round(
                        imagen.getHeight() * escala
                );

        BufferedImage resultado =
                new BufferedImage(
                        ancho,
                        alto,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g =
                resultado.createGraphics();

        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        g.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        g.drawImage(
                imagen,
                0,
                0,
                ancho,
                alto,
                null
        );

        g.dispose();

        return resultado;
    }

    private void crearInterfaz() {

        BufferedImage usuario =
                escalar(
                        assetUsuario,
                        0.31
                );

        BufferedImage textbox =
                escalar(
                        assetTextbox,
                        0.315
                );

        BufferedImage boton =
                escalar(
                        assetBoton,
                        0.22
                );

       
        Point posUsuario = new Point(110, 150);
        Point posContraseña = new Point(110, 490);
        Point posTextboxUsuario = new Point(980, 150);
        Point posTextboxContraseña = new Point(980, 490);

        Point posIngresar = new Point(310, 720);
        Point posCrear = new Point(
                ANCHO - 310 - boton.getWidth(),
                720
        );
        Point posRegresar = new Point(
                (ANCHO - boton.getWidth()) / 2,
                870
        );

        JPanel panel = new JPanel(null) {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY
                );

                g2.drawImage(
                        fondo,
                        0,
                        0,
                        ANCHO,
                        ALTO,
                        null
                );

                g2.drawImage(
                        usuario,
                        posUsuario.x,
                        posUsuario.y,
                        null
                );

                g2.drawImage(
                        usuario,
                        posContraseña.x,
                        posContraseña.y,
                        null
                );

                g2.drawImage(
                        textbox,
                        posTextboxUsuario.x,
                        posTextboxUsuario.y,
                        null
                );

                g2.drawImage(
                        textbox,
                        posTextboxContraseña.x,
                        posTextboxContraseña.y,
                        null
                );

                g2.drawImage(
                        boton,
                        posIngresar.x,
                        posIngresar.y,
                        null
                );

                g2.drawImage(
                        boton,
                        posCrear.x,
                        posCrear.y,
                        null
                );

                g2.drawImage(
                        boton,
                        posRegresar.x,
                        posRegresar.y,
                        null
                );

                g2.dispose();
            }
        };

        setContentPane(panel);

        Color colorTexto =
                Color.decode("#FFFEAE");

        JLabel lblUsuario =
                crearEtiqueta(
                        "USUARIO",
                        34,
                        colorTexto
                );

        lblUsuario.setBounds(
                265,
                230,
                360,
                64
        );

        panel.add(lblUsuario);

        JLabel lblContraseña =
                crearEtiqueta(
                        "CONTRASEÑA",
                        34,
                        colorTexto
                );

        lblContraseña.setBounds(
                265,
                569,
                360,
                64
        );

        panel.add(lblContraseña);

        txtUsuario =
                crearCampo(
                        colorTexto
                );

        txtUsuario.setBounds(
                1125,
                230,
                410,
                62
        );

        panel.add(txtUsuario);

        txtContraseña =
                crearCampoContraseña(
                        colorTexto
                );

        txtContraseña.setBounds(
                1125,
                569,
                410,
                62
        );

        panel.add(txtContraseña);

        JLabel lblIngresar =
                crearEtiqueta(
                        "INGRESAR",
                        29,
                        colorTexto
                );

        lblIngresar.setBounds(
                posIngresar.x,
                posIngresar.y,
                boton.getWidth(),
                boton.getHeight()
        );

        panel.add(lblIngresar);

        JLabel lblCrear =
                crearEtiqueta(
                        "CREAR JUGADOR",
                        29,
                        colorTexto
                );

        lblCrear.setBounds(
                posCrear.x,
                posCrear.y,
                boton.getWidth(),
                boton.getHeight()
        );

        panel.add(lblCrear);

        JLabel lblRegresar =
                crearEtiqueta(
                        "EXIT",
                        29,
                        colorTexto
                );

        lblRegresar.setBounds(
                posRegresar.x,
                posRegresar.y,
                boton.getWidth(),
                boton.getHeight()
        );

        panel.add(lblRegresar);

        btnIngresar =
                crearBotonInvisible();

        btnIngresar.setBounds(
                posIngresar.x,
                posIngresar.y,
                boton.getWidth(),
                boton.getHeight()
        );

        panel.add(btnIngresar);

        btnCrear =
                crearBotonInvisible();

        btnCrear.setBounds(
                posCrear.x,
                posCrear.y,
                boton.getWidth(),
                boton.getHeight()
        );

        panel.add(btnCrear);

        btnRegresar =
                crearBotonInvisible();

        btnRegresar.setBounds(
                posRegresar.x,
                posRegresar.y,
                boton.getWidth(),
                boton.getHeight()
        );

        panel.add(btnRegresar);

        btnIngresar.addActionListener(
                e -> iniciarSesion()
        );

        btnCrear.addActionListener(
                e -> crearJugador()
        );

        btnRegresar.addActionListener(
                e -> dispose()
        );
    }

    private JLabel crearEtiqueta(
            String texto,
            int tamaño,
            Color color) {

        JLabel etiqueta =
                new JLabel(
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

    private JTextField crearCampo(Color color) {

        JTextField campo =
                new JTextField();

        campo.setOpaque(false);
        campo.setBorder(null);
        campo.setForeground(color);
        campo.setCaretColor(color);

        campo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        campo.setFont(fuente);

        return campo;
    }

    private JPasswordField crearCampoContraseña(
            Color color) {

        JPasswordField campo =
                new JPasswordField();

        campo.setOpaque(false);
        campo.setBorder(null);
        campo.setForeground(color);
        campo.setCaretColor(color);

        campo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        campo.setFont(fuente);

        return campo;
    }

    private JButton crearBotonInvisible() {

        JButton boton =
                new JButton();

        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(null);
        boton.setFocusable(false);

        return boton;
    }

    private void iniciarSesion() {

        String usuario =
                txtUsuario.getText();

        String contraseña =
                new String(
                        txtContraseña.getPassword()
                );

        boolean correcto =
                sistema.iniciarSesion(
                        usuario,
                        contraseña
                );

        if (correcto) {

            dispose();

            MenuPrincipal menu =
                    new MenuPrincipal(sistema);

            menu.setVisible(true);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Usuario o contraseña incorrectos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void crearJugador() {

        String usuario =
                txtUsuario.getText();

        String contraseña =
                new String(
                        txtContraseña.getPassword()
                );

        try {

            boolean creado =
                    sistema.registrarJugador(
                            usuario,
                            contraseña,
                            java.time.LocalDateTime
                                    .now()
                                    .toString()
                    );

            if (creado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Jugador creado correctamente."
                );

                dispose();

                MenuPrincipal menu =
                        new MenuPrincipal(sistema);

                menu.setVisible(true);
            }

        } catch (ContraseñaException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (UsuarioDupException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
