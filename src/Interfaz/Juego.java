



package Interfaz;

import Modelo.Jugador;
import Modelo.Partida;
import Modelo.Sistema;
import Piezas.Pieza;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Juego extends JFrame {

    private static final int ANCHO = 1920, ALTO = 1080;
    private static final int N = 6, CELDA = 150;
    private static final int X_TABLERO = 430, Y_TABLERO = 80;

    private final Sistema sistema;
    private Partida partida;
    private final Casilla[][] casillas = new Casilla[N][N];
    private final Map<String, BufferedImage> piezas = new HashMap<>();

    private BufferedImage fondo, claro, cafe, marco;
    private BufferedImage trianguloLobo, trianguloNecro, trianguloVampiro;
    private Font fuente;
    private JLabel turno, giros;
    private JLabel log, resumenAtaque, descripcionRuleta;
    private PanelFondo panelPrincipal;
    private JButton girar, detener;
    private Ruleta ruleta;
    private Timer brillo, spin;
    private int filaOrigen = -1, columnaOrigen = -1;
    private int girosUsados;
    private Jugador jugadorDelTurno;
    private boolean partidaIniciada, brilloFuerte, girandoRuleta, finalMostrado;

    public Juego(Sistema sistema) {
        this.sistema = sistema;
        seleccionarOponente();
        if (partida == null) {
            return;
        }
        jugadorDelTurno = partida.getJugadorActual();

        setTitle("Vampire Wargame");
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        cargarRecursos();
        crearInterfaz();
        ToolTipManager.sharedInstance().setInitialDelay(0);
        registrar("La partida ha comenzado.");
        actualizarTablero();
        iniciarBrillo();
        partidaIniciada = true;
    }

    
    public boolean tienePartidaIniciada() {
        return partidaIniciada;
    }

    private void seleccionarOponente() {
        Jugador[] lista = sistema.obtenerOponentes();
        int cantidad = 0;
        for (Jugador jugador : lista) {
            if (jugador != null) cantidad++;
        }
        if (cantidad == 0) {
            JOptionPane.showMessageDialog(null, "No hay jugadores disponibles para jugar.");
            return;
        }

        String[] nombres = new String[cantidad];
        int i = 0;
        for (Jugador jugador : lista) {
            if (jugador != null) nombres[i++] = jugador.getUsuario();
        }
        String elegido = (String) JOptionPane.showInputDialog(null,
                "Seleccione su oponente:", "Nueva partida",
                JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
        if (elegido != null) partida = sistema.crearPartida(sistema.buscarJugador(elegido));
    }

    private void cargarRecursos() {
        fondo = imagen("/Recursos/fondoWarGame.png");
        claro = imagen("/Recursos/tileCeramicaMedieval.png");
        cafe = imagen("/Recursos/tileCeramicaCafe.png");
        marco = imagen("/Recursos/IngresarCrearJugadorYregresar.png");
        trianguloLobo = recortar(imagen("/Recursos/trianguloHombreLobo.png"));
        trianguloNecro = recortar(imagen("/Recursos/trianguloNecromante.png"));
        trianguloVampiro = recortar(imagen("/Recursos/trianguloVampiro.png"));

        cargarPieza("Hombre Lobo", "Blanco", "hombreLoboBlanco.png");
        cargarPieza("Hombre Lobo", "Negro", "hombreLoboNegro.png");
        cargarPieza("Necromante", "Blanco", "necromanteBlanco.png");
        cargarPieza("Necromante", "Negro", "necromanteNegro.png");
        cargarPieza("Vampiro", "Blanco", "vampiroBlanco.png");
        cargarPieza("Vampiro", "Negro", "vampiroNegro.png");
        cargarPieza("Zombie", "Blanco", "zombieBlanco.png");
        cargarPieza("Zombie", "Negro", "zombieNegro.png");

        try (InputStream in = getClass().getResourceAsStream("/Recursos/AppleGaramond-Bold.ttf")) {
            if (in == null) throw new IllegalStateException();
            fuente = Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (Exception e) {
            fuente = new Font("Serif", Font.BOLD, 26);
        }
    }

    private BufferedImage imagen(String ruta) {
        try (InputStream in = getClass().getResourceAsStream(ruta)) {
            return in == null ? null : ImageIO.read(in);
        } catch (Exception e) {
            return null;
        }
    }

    private void cargarPieza(String tipo, String color, String nombre) {
        piezas.put(tipo + "-" + color, imagen("/Recursos/" + nombre));
    }

   
    private BufferedImage recortar(BufferedImage original) {
        if (original == null) return null;
        int minX = original.getWidth(), minY = original.getHeight(), maxX = -1, maxY = -1;
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int p = original.getRGB(x, y), a = p >>> 24;
                int luz = ((p >>> 16) & 255) + ((p >>> 8) & 255) + (p & 255);
                if (a > 10 && luz > 35) {
                    minX = Math.min(minX, x); minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < 0) return original;
        minX = Math.max(0, minX - 4); minY = Math.max(0, minY - 4);
        maxX = Math.min(original.getWidth() - 1, maxX + 4);
        maxY = Math.min(original.getHeight() - 1, maxY + 4);
        BufferedImage resultado = new BufferedImage(maxX - minX + 1, maxY - minY + 1,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultado.createGraphics();
        g.drawImage(original, -minX, -minY, null); g.dispose();
        return resultado;
    }

    private void crearInterfaz() {
        PanelFondo panel = new PanelFondo();
        panel.setLayout(null);
        panelPrincipal = panel;
        setContentPane(panel);
        crearTablero(panel);

        Color dorado = Color.decode("#FFF6A3");
        JLabel rival = etiqueta(partida.getJugador2().getUsuario(), 24, dorado);
        rival.setBounds(78, 67, 320, 58); panel.add(rival);

        log = new JLabel("", SwingConstants.CENTER);
        log.setVerticalAlignment(SwingConstants.CENTER);
        log.setForeground(dorado);
        log.setFont(fuente.deriveFont(Font.BOLD, 16f));
        log.setBounds(38, 190, 354, 195);
        panel.add(log);

        resumenAtaque = new JLabel("", SwingConstants.CENTER);
        resumenAtaque.setVerticalAlignment(SwingConstants.CENTER);
        resumenAtaque.setForeground(dorado);
        resumenAtaque.setFont(fuente.deriveFont(Font.BOLD, 14f));
        resumenAtaque.setBounds(38, 460, 354, 195);
        panel.add(resumenAtaque);
        registrarResumenAtaqueInicial();

        descripcionRuleta = new JLabel("", SwingConstants.CENTER);
        descripcionRuleta.setVerticalAlignment(SwingConstants.CENTER);
        descripcionRuleta.setForeground(dorado);
        descripcionRuleta.setFont(fuente.deriveFont(Font.BOLD, 12f));
        descripcionRuleta.setBounds(38, 720, 354, 140);
        panel.add(descripcionRuleta);

        JButton abandonar = boton("ABANDONAR LA PARTIDA", 22);
        abandonar.setBounds(70, 910, 330, 74);
        abandonar.addActionListener(e -> retirarse()); panel.add(abandonar);

        turno = etiqueta("", 21, dorado);
        turno.setBounds(1450, 220, 350, 56); panel.add(turno);
        giros = etiqueta("", 19, dorado);
        giros.setBounds(1480, 294, 285, 45); panel.add(giros);

        ruleta = new Ruleta(trianguloLobo, trianguloNecro, trianguloVampiro);
        ruleta.setBounds(1400, 350, 430, 315); panel.add(ruleta);

        girar = boton("GIRAR RULETA", 23);
        girar.setBounds(1460, 690, 325, 70);
        girar.addActionListener(e -> iniciarSpin()); panel.add(girar);
        detener = boton("DETENER SPIN", 23);
        detener.setBounds(1460, 774, 325, 70); detener.setEnabled(false);
        detener.addActionListener(e -> detenerSpin()); panel.add(detener);

        JLabel principal = etiqueta(partida.getJugador1().getUsuario(), 24, dorado);
        principal.setBounds(1452, 925, 360, 58); panel.add(principal);
    }

    private void crearTablero(JPanel panel) {
        for (int vista = 0; vista < N; vista++) {
            int fila = N - 1 - vista;
            for (int columna = 0; columna < N; columna++) {
                Casilla casilla = new Casilla((vista + columna) % 2 == 0 ? cafe : claro);
                casilla.setBounds(X_TABLERO + columna * CELDA, Y_TABLERO + vista * CELDA, CELDA, CELDA);
                final int f = fila, c = columna;
                casilla.addActionListener(e -> seleccionarCasilla(f, c));
                casillas[fila][columna] = casilla;
                panel.add(casilla);
            }
        }
    }

    private JLabel etiqueta(String texto, int tamano, Color color) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setForeground(color); label.setFont(fuente.deriveFont(Font.BOLD, (float) tamano));
        return label;
    }

    private JButton boton(String texto, int tamano) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente.deriveFont(Font.BOLD, (float) tamano));
        boton.setForeground(Color.decode("#FFF6A3"));
        boton.setOpaque(false); boton.setContentAreaFilled(false); boton.setBorderPainted(false);
        boton.setFocusPainted(false); boton.setFocusable(false); boton.setRolloverEnabled(true);
        return boton;
    }

    private void iniciarBrillo() {
        brillo = new Timer(240, e -> {
            brilloFuerte = !brilloFuerte;
            for (Casilla[] fila : casillas) for (Casilla casilla : fila) casilla.brilFuerte(brilloFuerte);
        });
        brillo.start();
    }

    private void iniciarSpin() {
        if (girandoRuleta || partida.isFinalizada()) return;
        if (girosRestantes() <= 0) {
            mensaje("Ya utilizaste todos los giros de este turno."); return;
        }
        if (!hayTipoDisponible()) {
            mensaje("No quedan piezas disponibles para seleccionar en la ruleta."); return;
        }
        filaOrigen = columnaOrigen = -1;
        girandoRuleta = true; girar.setEnabled(false); detener.setEnabled(true);
        ruleta.iniciar(); actualizarTablero();
        registrar(partida.getJugadorActual().getUsuario() + " gira la ruleta.");
        spin = new Timer(62, e -> ruleta.avanzar()); spin.start();
    }

    private void detenerSpin() {
        if (!girandoRuleta) return;
        spin.stop(); girandoRuleta = false; detener.setEnabled(false);
        String tipo = girarRuletaDisponible();
        if (tipo == null) {
            mensaje("No quedan piezas disponibles para seleccionar en la ruleta.");
            actualizarTablero();
            return;
        }
        girosUsados++;
        ruleta.detenerEn(indiceRuleta(tipo));
        registrar("Ruleta: " + tipo + " fue seleccionado.");
        actualizarTablero();
    }

   
    private String girarRuletaDisponible() {
        if (!hayTipoDisponible()) return null;
        String tipo;
        do {
            tipo = partida.girarRuleta();
        } while (!tienePiezaDisponible(tipo));
        return tipo;
    }

    private boolean hayTipoDisponible() {
        return tienePiezaDisponible("Hombre Lobo")
                || tienePiezaDisponible("Necromante")
                || tienePiezaDisponible("Vampiro");
    }

    private boolean tienePiezaDisponible(String tipo) {
        String color = colorJugadorActual();
        for (int fila = 0; fila < N; fila++) {
            for (int columna = 0; columna < N; columna++) {
                Pieza pieza = partida.getTablero().obtenerPieza(fila, columna);
                if (pieza != null && color.equals(pieza.getColor()) && tipo.equals(pieza.getTipo())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String colorJugadorActual() {
        return partida.getJugadorActual() == partida.getJugador1() ? "Blanco" : "Negro";
    }

    private int indiceRuleta(String tipo) {
        int primeraCasilla;
        if ("Hombre Lobo".equals(tipo)) primeraCasilla = 0;
        else if ("Necromante".equals(tipo)) primeraCasilla = 1;
        else primeraCasilla = 2;

      
        return primeraCasilla + (Math.random() < 0.5 ? 0 : 3);
    }

    private void seleccionarCasilla(int fila, int columna) {
        if (partida.isFinalizada() || girandoRuleta) return;
        Pieza pieza = partida.getTablero().obtenerPieza(fila, columna);
        if (filaOrigen < 0) {
            if (pieza == null) return;
            if (!partida.puedeMoverPieza(pieza)) {
                mensaje(partida.getTipoSeleccionado() == null ? "Primero debes girar la ruleta."
                        : "La ruleta no seleccionó esa pieza para este turno.");
                return;
            }
            filaOrigen = fila; columnaOrigen = columna; actualizarTablero(); return;
        }
        if (fila == filaOrigen && columna == columnaOrigen) {
            filaOrigen = columnaOrigen = -1; actualizarTablero(); return;
        }
        ejecutarAccion(filaOrigen, columnaOrigen, fila, columna);
        filaOrigen = columnaOrigen = -1;
        actualizarTablero(); comprobarFinal();
    }

    private void ejecutarAccion(int fo, int co, int fd, int cd) {
        Pieza atacante = partida.getTablero().obtenerPieza(fo, co);
        Pieza destino = partida.getTablero().obtenerPieza(fd, cd);
        String jugador = partida.getJugadorActual().getUsuario();
        if (destino == null) {
            if ("Necromante".equals(atacante.getTipo())) {
                String[] opciones = {"Mover necromante", "Invocar Zombie", "Cancelar"};
                int opcion = JOptionPane.showOptionDialog(this,
                        "Seleccione la acción del necromante:", "Necromante",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opciones, opciones[0]);
                if (opcion == 1) {
                    if (partida.invocarZombie(fd, cd)) {
                        registrar(jugador + " invocó un Zombie en " + casilla(fd, cd) + ".");
                    } else {
                        mensaje("El Zombie debe invocarse en una casilla vacía.");
                    }
                    return;
                }
                if (opcion != 0) return;
            }
            if (partida.moverPieza(fo, co, fd, cd)) {
                registrar(jugador + " movió " + atacante.getTipo() + " de " + casilla(fo, co)
                        + " a " + casilla(fd, cd) + ".");
            } else mensaje("Movimiento inválido.");
            return;
        }
        if (atacante.getColor().equals(destino.getColor())) {
            mensaje("No puedes atacar una pieza propia."); return;
        }
        String[] opciones = {"Ataque normal", "Ataque especial", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione una acción:", "Acción",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (opcion == 0) {
            int escudoAntes = destino.getEscudo();
            int vidaAntes = destino.getVida();
            if (partida.atacar(fo, co, fd, cd)) {
                if (destino.getVida() <= 0) animarMuerte(destino, fd, cd);
                registrarResumenAtaque(atacante, jugador, destino, escudoAntes, vidaAntes);
                registrar(jugador + " atacó con " + atacante.getTipo() + " en " + casilla(fd, cd) + ".");
            } else mensaje("Ataque inválido.");
        } else if (opcion == 1) {
            ataqueEspecial(atacante, jugador, fo, co, fd, cd);
        }
    }

    private void ataqueEspecial(Pieza atacante, String jugador, int fo, int co, int fd, int cd) {
        Pieza objetivo = partida.getTablero().obtenerPieza(fd, cd);
        int escudoAntes = objetivo == null ? 0 : objetivo.getEscudo();
        int vidaAntes = objetivo == null ? 0 : objetivo.getVida();
        boolean hecho = false;
        if ("Vampiro".equals(atacante.getTipo())) {
            hecho = partida.ataqueEspecialVampiro(fo, co, fd, cd);
        } else if ("Necromante".equals(atacante.getTipo())) {
            int df = Math.abs(fd - fo), dc = Math.abs(cd - co);
            boolean puedeLanzar = (df == 2 && dc == 0) || (df == 0 && dc == 2);
            boolean hayZombie = distanciaAlMenosTres(fd, cd)
                    && !zombiesQuePuedenAtacar(atacante.getColor(), fd, cd).isEmpty();

            if (puedeLanzar && hayZombie) {
                String[] opciones = {"Lanzar lanza", "Ordenar Zombie", "Cancelar"};
                int opcion = JOptionPane.showOptionDialog(this,
                        "Seleccione el ataque especial del Necromante:", "Necromante",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, opciones, opciones[0]);
                if (opcion == 0) {
                    hecho = partida.ataqueLanza(fo, co, fd, cd);
                } else if (opcion == 1) {
                    ejecutarAtaqueConZombie(atacante, jugador, fo, co, fd, cd);
                    return;
                } else {
                    return;
                }
            } else if (puedeLanzar) {
                hecho = partida.ataqueLanza(fo, co, fd, cd);
            } else if (hayZombie) {
                ejecutarAtaqueConZombie(atacante, jugador, fo, co, fd, cd);
                return;
            } else {
                mensaje("La lanza ataca a 2 casillas en línea recta o usa un Zombie aliado junto al enemigo.");
                return;
            }
        } else { mensaje("Esta pieza no tiene un ataque especial ofensivo."); return; }
        if (hecho) {
            if (objetivo != null && objetivo.getVida() <= 0) animarMuerte(objetivo, fd, cd);
            registrarResumenAtaque(atacante, jugador, objetivo, escudoAntes, vidaAntes);
            registrar(jugador + " usó ataque especial de " + atacante.getTipo()
                    + " en " + casilla(fd, cd) + ".");
        }
        else mensaje("Ataque especial inválido.");
    }

    private String casilla(int filaModelo, int columna) {
        return String.valueOf((char) ('A' + columna)) + (N - filaModelo);
    }

    private List<Point> zombiesQuePuedenAtacar(String color, int filaEnemigo, int columnaEnemigo) {
        List<Point> candidatos = new ArrayList<>();

        for (int fila = 0; fila < N; fila++) {
            for (int columna = 0; columna < N; columna++) {
                Pieza pieza = partida.getTablero().obtenerPieza(fila, columna);
                int distanciaFila = Math.abs(filaEnemigo - fila);
                int distanciaColumna = Math.abs(columnaEnemigo - columna);

                if (pieza != null
                        && "Zombie".equals(pieza.getTipo())
                        && color.equals(pieza.getColor())
                        && distanciaFila <= 1
                        && distanciaColumna <= 1
                        && (distanciaFila != 0 || distanciaColumna != 0)) {
                    candidatos.add(new Point(columna, fila));
                }
            }
        }

        return candidatos;
    }

    private void ejecutarAtaqueConZombie(Pieza necromante, String jugador,
                                         int filaNecromante, int columnaNecromante,
                                         int filaEnemigo, int columnaEnemigo) {
        List<Point> candidatos = zombiesQuePuedenAtacar(
                necromante.getColor(), filaEnemigo, columnaEnemigo
        );

        if (candidatos.isEmpty()) {
            mensaje("No hay un Zombie aliado junto a esa pieza enemiga.");
            return;
        }

        Point zombieElegido;

        if (candidatos.size() == 1) {
            zombieElegido = candidatos.get(0);
        } else {
            String[] opciones = new String[candidatos.size()];
            for (int i = 0; i < candidatos.size(); i++) {
                Point candidato = candidatos.get(i);
                opciones[i] = "Zombie en " + casilla(candidato.y, candidato.x);
            }

            String opcion = (String) JOptionPane.showInputDialog(this,
                    "Seleccione el Zombie que atacará:", "Ataque con Zombie",
                    JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            if (opcion == null) return;

            zombieElegido = candidatos.get(0);
            for (int i = 0; i < opciones.length; i++) {
                if (opciones[i].equals(opcion)) {
                    zombieElegido = candidatos.get(i);
                    break;
                }
            }
        }

        Pieza zombie = partida.getTablero().obtenerPieza(zombieElegido.y, zombieElegido.x);
        Pieza objetivo = partida.getTablero().obtenerPieza(filaEnemigo, columnaEnemigo);
        int escudoAntes = objetivo == null ? 0 : objetivo.getEscudo();
        int vidaAntes = objetivo == null ? 0 : objetivo.getVida();

        boolean hecho = partida.ataqueConZombie(
                filaNecromante,
                columnaNecromante,
                zombieElegido.y,
                zombieElegido.x,
                filaEnemigo,
                columnaEnemigo
        );

        if (!hecho) {
            mensaje("El enemigo debe estar junto al Zombie y a más de 2 casillas del Necromante.");
            return;
        }

        if (objetivo != null && objetivo.getVida() <= 0) {
            animarMuerte(objetivo, filaEnemigo, columnaEnemigo);
        }

        registrarResumenAtaque(zombie, jugador, objetivo, escudoAntes, vidaAntes);
        registrar(jugador + " ordenó a un Zombie atacar en "
                + casilla(filaEnemigo, columnaEnemigo) + ".");
    }

   
    private void animarMuerte(Pieza piezaEliminada, int filaModelo, int columna) {
        BufferedImage imagenPieza = piezas.get(
                piezaEliminada.getTipo() + "-" + piezaEliminada.getColor()
        );

        if (imagenPieza == null || panelPrincipal == null) return;

        int filaVista = N - 1 - filaModelo;
        PiezaDesvaneciendo efecto = new PiezaDesvaneciendo(imagenPieza);
        efecto.setBounds(X_TABLERO + columna * CELDA,
                Y_TABLERO + filaVista * CELDA, CELDA, CELDA);

        panelPrincipal.add(efecto);
        panelPrincipal.setComponentZOrder(efecto, 0);
        panelPrincipal.repaint();

        long inicio = System.currentTimeMillis();
        Timer desvanecer = new Timer(16, null);

        desvanecer.addActionListener(e -> {
            float progreso = Math.min(1f,
                    (System.currentTimeMillis() - inicio) / 1000f);
            efecto.setOpacidad(1f - progreso);

            if (progreso >= 1f) {
                desvanecer.stop();
                panelPrincipal.remove(efecto);
                panelPrincipal.revalidate();
                panelPrincipal.repaint();
            }
        });

        desvanecer.start();
    }

    private void actualizarTablero() {
        for (int fila = 0; fila < N; fila++) for (int columna = 0; columna < N; columna++) {
            Pieza pieza = partida.getTablero().obtenerPieza(fila, columna);
            Casilla c = casillas[fila][columna];
            c.pieza(pieza == null ? null : piezas.get(pieza.getTipo() + "-" + pieza.getColor()));
            c.seleccionada(fila == filaOrigen && columna == columnaOrigen);
            c.iluminada(filaOrigen >= 0 && destinoPermitido(fila, columna));
            c.setToolTipText(pieza == null ? null : "<html><b>" + pieza.getTipo() + " " + pieza.getColor()
                    + "</b><br>Vida: " + pieza.getVida() + "<br>Escudo: " + pieza.getEscudo() + "</html>");
        }
        actualizarLateral();
    }

    private boolean destinoPermitido(int fd, int cd) {
        if (fd == filaOrigen && cd == columnaOrigen) return false;
        Pieza origen = partida.getTablero().obtenerPieza(filaOrigen, columnaOrigen);
        Pieza destino = partida.getTablero().obtenerPieza(fd, cd);
        if (origen == null || (destino != null && origen.getColor().equals(destino.getColor()))) return false;

        if (destino == null && "Necromante".equals(origen.getTipo())) return true;
        int df = Math.abs(fd - filaOrigen), dc = Math.abs(cd - columnaOrigen);
        if (destino != null) {
            boolean ataqueNormal = df <= 1 && dc <= 1;
            boolean ataqueLanza = "Necromante".equals(origen.getTipo())
                    && ((df == 2 && dc == 0) || (df == 0 && dc == 2))
                    && intermediaLibre(fd, cd);
            boolean ataqueZombie = "Necromante".equals(origen.getTipo())
                    && distanciaAlMenosTres(fd, cd)
                    && !zombiesQuePuedenAtacar(origen.getColor(), fd, cd).isEmpty();
            return ataqueNormal || ataqueLanza || ataqueZombie;
        }
        if ("Zombie".equals(origen.getTipo())) return false;
        if ("Hombre Lobo".equals(origen.getTipo())) {
            return df <= 2 && dc <= 2 && (df != 0 || dc != 0) && intermediaLibre(fd, cd);
        }
        return df <= 1 && dc <= 1;
    }

    private boolean intermediaLibre(int fd, int cd) {
        if (Math.abs(fd - filaOrigen) < 2 && Math.abs(cd - columnaOrigen) < 2) return true;
        return partida.getTablero().casillaVacia((filaOrigen + fd) / 2, (columnaOrigen + cd) / 2);
    }

    private boolean distanciaAlMenosTres(int filaDestino, int columnaDestino) {
        return Math.max(Math.abs(filaDestino - filaOrigen),
                Math.abs(columnaDestino - columnaOrigen)) > 2;
    }

    private void actualizarLateral() {
        if (turno == null) return;
        sincronizarGirosConTurno();
        turno.setText("TURNO: " + partida.getJugadorActual().getUsuario());
        giros.setText("GIROS DISPONIBLES: " + girosRestantes());
        actualizarDescripcionRuleta();
        if (!girandoRuleta) girar.setEnabled(!partida.isFinalizada() && girosRestantes() > 0
                && hayTipoDisponible());
        if (!girandoRuleta && partida.getTipoSeleccionado() == null) ruleta.limpiar();
    }

    private void sincronizarGirosConTurno() {
        if (jugadorDelTurno != partida.getJugadorActual()) {
            jugadorDelTurno = partida.getJugadorActual();
            girosUsados = 0;
        }
    }

    
    private int girosPermitidos() {
        int piezasInicialesRestantes = 0;
        String color = colorJugadorActual();
        for (int fila = 0; fila < N; fila++) {
            for (int columna = 0; columna < N; columna++) {
                Pieza pieza = partida.getTablero().obtenerPieza(fila, columna);
                if (pieza != null && color.equals(pieza.getColor())
                        && !"Zombie".equals(pieza.getTipo())) {
                    piezasInicialesRestantes++;
                }
            }
        }
        int perdidas = Math.max(0, 6 - piezasInicialesRestantes);
        if (perdidas >= 4) return 3;
        if (perdidas >= 2) return 2;
        return 1;
    }

    private int girosRestantes() {
        sincronizarGirosConTurno();
        return Math.max(0, girosPermitidos() - girosUsados);
    }

    private void actualizarDescripcionRuleta() {
        if (descripcionRuleta == null) return;

        String tipo = partida.getTipoSeleccionado();
        String descripcion;

        if (tipo == null) {
            descripcion = "Gira la ruleta para conocer la habilidad de la pieza seleccionada.";
        } else if ("Hombre Lobo".equals(tipo)) {
            descripcion = "<b>HOMBRE LOBO</b><br>Mueve hasta 2 casillas vacías.";
        } else if ("Vampiro".equals(tipo)) {
            descripcion = "<b>VAMPIRO</b><br>Roba 1 vida a un enemigo adyacente y recupera 1.";
        } else {
            descripcion = "<b>NECROMANTE</b><br>Invoca un Zombie en una casilla vacía.<br>"
                    + "La lanza alcanza 2 casillas.<br>"
                    + "También ordena atacar a un Zombie aliado.";
        }

        descripcionRuleta.setText("<html><div style='text-align:center; width:340px; line-height:15px;'>"
                + descripcion + "</div></html>");
    }

    private void comprobarFinal() {
        if (!partida.isFinalizada() || finalMostrado) return;
        Jugador ganador = partida.obtenerGanador();
        if (ganador != null) {
            finalizarYRegresar(ganador, null);
        }
    }

    private void finalizarYRegresar(Jugador ganador, Jugador retirado) {
        if (finalMostrado) return;
        finalMostrado = true;

        sistema.registrarResultadoPartida(partida, ganador, retirado);
        String mensajeFinal;

        if (retirado == null) {
            Jugador perdedor = ganador == partida.getJugador1()
                    ? partida.getJugador2() : partida.getJugador1();
            mensajeFinal = ganador.getUsuario() + " venció a " + perdedor.getUsuario()
                    + ".\n¡Felicidades, has ganado 3 puntos!";
        } else {
            mensajeFinal = retirado.getUsuario() + " se ha retirado.\n¡Felicidades, "
                    + ganador.getUsuario() + ", has ganado 3 puntos!";
        }

        if (brillo != null) brillo.stop();
        if (spin != null) spin.stop();
        JOptionPane.showMessageDialog(this, mensajeFinal,
                "Partida finalizada", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        MenuPrincipal menu = new MenuPrincipal(sistema);
        menu.setVisible(true);
    }

    private void registrar(String texto) {
        if (log != null) {
            log.setText("<html><div style='text-align:center; width:340px; line-height:18px;'>"
                    + texto + "</div></html>");
        }
    }

  
    private void registrarResumenAtaque(Pieza atacante, String jugadorAtacante,
                                        Pieza objetivo, int escudoAntes, int vidaAntes) {
        if (resumenAtaque == null || atacante == null || objetivo == null) return;

        int escudoPerdido = Math.max(0, escudoAntes - objetivo.getEscudo());
        int vidaPerdida = Math.max(0, vidaAntes - Math.max(0, objetivo.getVida()));
        String jugadorDefensor = nombreJugadorDelColor(objetivo.getColor());

        String texto = "<b>ÚLTIMO ATAQUE</b><br>"
                + atacante.getTipo() + " de " + jugadorAtacante + " atacó a "
                + objetivo.getTipo() + " de " + jugadorDefensor + ".<br>";

        if (objetivo.getVida() <= 0) {
            texto += "Perdió " + escudoPerdido + " de escudo y " + vidaPerdida
                    + " de vida.<br>La pieza fue destruida.";
        } else {
            texto += "Perdió " + escudoPerdido + " de escudo y " + vidaPerdida
                    + " de vida.<br>Le quedan " + objetivo.getEscudo() + " de escudo y "
                    + objetivo.getVida() + " de vida.";
        }

        resumenAtaque.setText("<html><div style='text-align:center; width:340px; line-height:16px;'>"
                + texto + "</div></html>");
    }

    private void registrarResumenAtaqueInicial() {
        if (resumenAtaque != null) {
            resumenAtaque.setText("<html><div style='text-align:center; width:340px; line-height:16px;'>"
                    + "<b>ÚLTIMO ATAQUE</b><br>Aún no se ha realizado<br>ningún ataque."
                    + "</div></html>");
        }
    }

    private String nombreJugadorDelColor(String color) {
        return "Blanco".equals(color)
                ? partida.getJugador1().getUsuario()
                : partida.getJugador2().getUsuario();
    }

    private void retirarse() {
        if (JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea abandonar la partida?",
                "Abandonar partida", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Jugador retirado = partida.getJugadorActual();
        Jugador ganador = retirado == partida.getJugador1()
                ? partida.getJugador2() : partida.getJugador1();
        partida.finalizarPartida();
        finalizarYRegresar(ganador, retirado);
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Vampire Wargame", JOptionPane.INFORMATION_MESSAGE);
    }

    private class PanelFondo extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            if (fondo != null) g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
            else { g2.setColor(new Color(129, 93, 45)); g2.fillRect(0, 0, getWidth(), getHeight()); }
            g2.setColor(new Color(40, 24, 10, 28)); g2.fillRect(0, 0, getWidth(), getHeight());
            marco(g2, 60, 55, 355, 80); marco(g2, 20, 165, 390, 245);
            marco(g2, 20, 435, 390, 245); marco(g2, 20, 705, 390, 170);
            marco(g2, 70, 910, 330, 74); marco(g2, 1438, 210, 370, 68);
            marco(g2, 1472, 285, 290, 56); marco(g2, 1460, 690, 325, 70);
            marco(g2, 1460, 774, 325, 70); marco(g2, 1440, 915, 380, 82);
            g2.setColor(new Color(71, 43, 18, 190)); g2.setStroke(new BasicStroke(5f));
            g2.drawRect(X_TABLERO - 4, Y_TABLERO - 4, N * CELDA + 8, N * CELDA + 8);
            g2.dispose();
        }
        private void marco(Graphics2D g, int x, int y, int w, int h) {
            if (marco != null) g.drawImage(marco, x, y, w, h, null);
            else { g.setColor(new Color(25, 23, 20, 230)); g.fillRoundRect(x, y, w, h, 18, 18);
                g.setColor(new Color(181, 144, 68)); g.drawRoundRect(x, y, w, h, 18, 18); }
        }
    }

    private class Ruleta extends JComponent {
        private final BufferedImage[] triangulos;
        private double angulo;
        private int activo = -1;
        private boolean gira, destello;

        Ruleta(BufferedImage lobo, BufferedImage necro, BufferedImage vampiro) {
            triangulos = new BufferedImage[]{
                lobo, necro, vampiro,
                lobo, necro, vampiro
            };
            setOpaque(false);
        }
        void iniciar() { gira = true; activo = bajoCuerda(); repaint(); }
        void avanzar() { angulo += Math.PI / 18; activo = bajoCuerda(); destello = !destello; repaint(); }
        void detenerEn(int ganador) {
            gira = false;
            activo = ganador;
            angulo = -ganador * 2 * Math.PI / triangulos.length;
            repaint();
        }
        void limpiar() { if (!gira && activo != -1) { activo = -1; repaint(); } }
        private int bajoCuerda() {
            int mejor = 0; double menor = Double.MAX_VALUE;
            for (int i = 0; i < triangulos.length; i++) {
                double a = normalizar(angulo + i * 2 * Math.PI / triangulos.length), d = Math.abs(a);
                if (d < menor) { menor = d; mejor = i; }
            }
            return mejor;
        }
        private double normalizar(double a) {
            while (a > Math.PI) a -= 2 * Math.PI;
            while (a < -Math.PI) a += 2 * Math.PI;
            return a;
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D base = (Graphics2D) g.create();
            base.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int cx = getWidth() / 2, cy = 180, w = 145, h = 155;
            base.translate(cx, cy);
            for (int i = 0; i < triangulos.length; i++) {
                Graphics2D s = (Graphics2D) base.create();
                s.rotate(angulo + i * 2 * Math.PI / triangulos.length);
                Polygon p = new Polygon(new int[]{-w / 2, w / 2, 0}, new int[]{-h, -h, 0}, 3);
                if (i == activo) {
                    s.setColor(new Color(255, 222, 84, gira && !destello ? 90 : 185)); s.fillPolygon(p);
                    s.setColor(new Color(255, 244, 155, gira ? 230 : 255)); s.setStroke(new BasicStroke(gira ? 5f : 7f));
                } else { s.setColor(new Color(20, 20, 20, 175)); s.fillPolygon(p);
                    s.setColor(new Color(160, 145, 110, 150)); s.setStroke(new BasicStroke(2f)); }
                s.drawPolygon(p);
                if (triangulos[i] != null) s.drawImage(triangulos[i], -w / 2, -h, w, h, null);
                s.dispose();
            }
          
            base.setColor(new Color(58, 39, 22)); base.setStroke(new BasicStroke(5f)); base.drawLine(0, -180, 0, -142);
            base.setColor(new Color(255, 238, 142)); base.setStroke(new BasicStroke(2f)); base.drawLine(0, -180, 0, -142);
            base.fillPolygon(new int[]{-11, 11, 0}, new int[]{-142, -142, -120}, 3);
            base.setColor(new Color(255, 240, 155)); base.fillOval(-7, -7, 14, 14); base.dispose();
        }
    }

    private class PiezaDesvaneciendo extends JComponent {
        private final BufferedImage imagen;
        private float opacidad = 1f;

        PiezaDesvaneciendo(BufferedImage imagen) {
            this.imagen = imagen;
            setOpaque(false);
        }

        void setOpacidad(float opacidad) {
            this.opacidad = Math.max(0f, Math.min(1f, opacidad));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));

            double escala = Math.min(
                    (double) (getWidth() - 16) / imagen.getWidth(),
                    (double) (getHeight() - 10) / imagen.getHeight()
            );
            int ancho = (int) Math.round(imagen.getWidth() * escala);
            int alto = (int) Math.round(imagen.getHeight() * escala);
            g2.drawImage(imagen, (getWidth() - ancho) / 2,
                    getHeight() - alto - 4, ancho, alto, null);
            g2.dispose();
        }
    }

    private class Casilla extends JButton {
        private final BufferedImage tile;
        private BufferedImage pieza;
        private boolean iluminada, seleccionada, pulso;
        Casilla(BufferedImage tile) {
            this.tile = tile; setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false); setFocusable(false); setRolloverEnabled(true); setMargin(new Insets(0, 0, 0, 0));
        }
        void pieza(BufferedImage imagen) { pieza = imagen; repaint(); }
        void iluminada(boolean valor) { iluminada = valor; repaint(); }
        void seleccionada(boolean valor) { seleccionada = valor; repaint(); }
        void brilFuerte(boolean valor) { pulso = valor; repaint(); }

        @Override
        public JToolTip createToolTip() {
            JToolTip ayuda = super.createToolTip();
            ayuda.setBackground(new Color(28, 26, 23));
            ayuda.setForeground(Color.decode("#FFF6A3"));
            ayuda.setFont(fuente.deriveFont(Font.BOLD, 17f));
            ayuda.setBorder(new LineBorder(new Color(190, 148, 74), 2));
            return ayuda;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            if (tile != null) g2.drawImage(tile, 0, 0, getWidth(), getHeight(), null);
            else { g2.setColor(new Color(170, 130, 81)); g2.fillRect(0, 0, getWidth(), getHeight()); }
            if (iluminada) {
                int a = pulso ? 145 : 72; Paint anterior = g2.getPaint();
                g2.setPaint(new RadialGradientPaint(getWidth() / 2f, getHeight() / 2f, getWidth() * .72f,
                        new float[]{0f, .68f, 1f}, new Color[]{new Color(255,244,122,a),
                            new Color(255,205,50,a / 2), new Color(255,220,83,0)}));
                g2.fillRect(0, 0, getWidth(), getHeight()); g2.setPaint(anterior);
                g2.setColor(new Color(255, 242, 130, pulso ? 255 : 160)); g2.setStroke(new BasicStroke(pulso ? 5f : 3f));
                g2.drawRect(3, 3, getWidth() - 6, getHeight() - 6);
            }
            if (seleccionada) { g2.setColor(new Color(90, 228, 255, 230)); g2.setStroke(new BasicStroke(6f));
                g2.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 14, 14); }
            if (pieza != null) {
                double escala = Math.min((double) (getWidth() - 16) / pieza.getWidth(),
                        (double) (getHeight() - 10) / pieza.getHeight());
                int w = Math.max(1, (int) Math.round(pieza.getWidth() * escala));
                int h = Math.max(1, (int) Math.round(pieza.getHeight() * escala));
                g2.drawImage(pieza, (getWidth() - w) / 2, getHeight() - h - 4, w, h, null);
            }
            if (getModel().isRollover() && !iluminada) { g2.setColor(new Color(255,255,255,38)); g2.fillRect(0,0,getWidth(),getHeight()); }
            g2.dispose();
        }
    }
}
