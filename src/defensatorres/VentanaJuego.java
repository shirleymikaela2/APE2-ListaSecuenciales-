package defensatorres;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** Interfaz gráfica del simulador de defensa de torres. */
public final class VentanaJuego extends JFrame {
    private final MotorJuego motor;
    private final PanelMapa panelMapa;
    private final JLabel etiquetaVida;
    private final JLabel etiquetaMonedas;
    private final JLabel etiquetaOleada;
    private final JLabel etiquetaPuntuacion;
    private final JLabel etiquetaCozy;
    private final JTextArea areaEventos;
    private final JButton botonIniciar;
    private final JButton botonPausa;
    private final JButton botonMejorar;
    private final JButton botonDeshacer;
    private final JButton botonRehacer;
    private final Timer temporizador;

    private TipoTorre tipoSeleccionado;
    private Posicion posicionSeleccionada;

    public VentanaJuego() {
        super("Cozy Defense - Colas, pilas y búsqueda de rutas");
        motor = new MotorJuego();
        panelMapa = new PanelMapa(motor);
        etiquetaVida = crearEtiquetaEstado();
        etiquetaMonedas = crearEtiquetaEstado();
        etiquetaOleada = crearEtiquetaEstado();
        etiquetaPuntuacion = crearEtiquetaEstado();
        etiquetaCozy = crearEtiquetaEstado();
        areaEventos = new JTextArea(5, 30);
        botonIniciar = new JButton("Iniciar oleada");
        botonPausa = new JButton("Pausar");
        botonMejorar = new JButton("Mejorar seleccionada");
        botonDeshacer = new JButton("Deshacer");
        botonRehacer = new JButton("Rehacer");
        tipoSeleccionado = TipoTorre.BASICA;

        configurarVentana();
        construirInterfaz();
        conectarEventos();

        temporizador = new Timer(650, evento -> avanzarQuantum());
        temporizador.start();
        registrarMensaje(motor.getUltimoMensaje());
        actualizarVista();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(245, 247, 250));
        setMinimumSize(new Dimension(1050, 720));
    }

    private void construirInterfaz() {
        add(crearPanelEstado(), BorderLayout.NORTH);
        add(crearPanelControles(), BorderLayout.EAST);

        areaEventos.setEditable(false);
        areaEventos.setLineWrap(true);
        areaEventos.setWrapStyleWord(true);
        areaEventos.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollEventos = new JScrollPane(areaEventos);
        scrollEventos.setBorder(BorderFactory.createTitledBorder("Registro de la simulación"));

        JScrollPane scrollMapa = new JScrollPane(panelMapa);
        scrollMapa.setBorder(BorderFactory.createTitledBorder("Mapa y ruta calculada con BFS"));

        JSplitPane divisor = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scrollMapa, scrollEventos);
        divisor.setResizeWeight(0.82);
        divisor.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 0));
        add(divisor, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 8, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 2, 8));
        panel.add(etiquetaVida);
        panel.add(etiquetaMonedas);
        panel.add(etiquetaOleada);
        panel.add(etiquetaPuntuacion);
        panel.add(etiquetaCozy);
        return panel;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(235, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 8, 8),
                BorderFactory.createTitledBorder("Controles")));

        JLabel tituloTorres = new JLabel("1. Seleccione una torre");
        tituloTorres.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(tituloTorres);
        panel.add(Box.createVerticalStrut(6));

        ButtonGroup grupoTorres = new ButtonGroup();
        for (TipoTorre tipo : TipoTorre.values()) {
            JToggleButton boton = new JToggleButton(tipo.getNombre() + " - $"
                    + tipo.getCostoBase() + " - DD " + tipo.getDanioBase());
            boton.setAlignmentX(LEFT_ALIGNMENT);
            boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            boton.addActionListener(evento -> tipoSeleccionado = tipo);
            grupoTorres.add(boton);
            panel.add(boton);
            panel.add(Box.createVerticalStrut(4));
            if (tipo == TipoTorre.BASICA) {
                boton.setSelected(true);
            }
        }

        JLabel instruccion = new JLabel("2. Haga clic en una celda");
        instruccion.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(8));
        panel.add(instruccion);
        panel.add(Box.createVerticalStrut(12));

        prepararBoton(botonMejorar, panel);
        prepararBoton(botonDeshacer, panel);
        prepararBoton(botonRehacer, panel);
        panel.add(Box.createVerticalStrut(14));
        prepararBoton(botonIniciar, panel);
        prepararBoton(botonPausa, panel);

        JButton botonReiniciar = new JButton("Reiniciar partida");
        prepararBoton(botonReiniciar, panel);
        botonReiniciar.addActionListener(evento -> reiniciarPartida());

        panel.add(Box.createVerticalGlue());
        JLabel leyenda = new JLabel("<html><b>Leyenda</b><br>"
                + "B: Básica &nbsp; R: Rápida &nbsp; P: Pesada<br>"
                + "El número indica el nivel.<br><br>"
                + "Los Cozy avanzan por la ruta amarilla.</html>");
        leyenda.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(leyenda);
        return panel;
    }

    private void prepararBoton(JButton boton, JPanel panel) {
        boton.setAlignmentX(LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        panel.add(boton);
        panel.add(Box.createVerticalStrut(5));
    }

    private void conectarEventos() {
        panelMapa.setAlSeleccionar(this::seleccionarCelda);
        botonMejorar.addActionListener(evento -> {
            motor.mejorarTorre(posicionSeleccionada);
            registrarMensaje(motor.getUltimoMensaje());
            actualizarVista();
        });
        botonDeshacer.addActionListener(evento -> {
            motor.deshacer();
            registrarMensaje(motor.getUltimoMensaje());
            actualizarVista();
        });
        botonRehacer.addActionListener(evento -> {
            motor.rehacer();
            registrarMensaje(motor.getUltimoMensaje());
            actualizarVista();
        });
        botonIniciar.addActionListener(evento -> {
            motor.iniciarSiguienteOleada();
            registrarMensaje(motor.getUltimoMensaje());
            actualizarVista();
        });
        botonPausa.addActionListener(evento -> {
            motor.alternarPausa();
            registrarMensaje(motor.getUltimoMensaje());
            actualizarVista();
        });
    }

    private void seleccionarCelda(Posicion posicion) {
        posicionSeleccionada = posicion;
        panelMapa.setSeleccionada(posicion);
        Torre torre = motor.getMapa().buscarTorre(posicion);
        if (torre != null) {
            registrarMensaje("Torre seleccionada: " + torre + ".");
        } else {
            motor.colocarTorre(tipoSeleccionado, posicion);
            registrarMensaje(motor.getUltimoMensaje());
        }
        actualizarVista();
    }

    private void avanzarQuantum() {
        ResultadoQuantum resultado = motor.procesarQuantum();
        if (!resultado.getMensaje().isEmpty()) {
            registrarMensaje(resultado.getMensaje());
        }
        actualizarVista();
    }

    private void reiniciarPartida() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea reiniciar toda la partida?",
                "Confirmar reinicio", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            motor.reiniciar();
            posicionSeleccionada = null;
            panelMapa.setSeleccionada(null);
            areaEventos.setText("");
            registrarMensaje(motor.getUltimoMensaje());
            actualizarVista();
        }
    }

    private void actualizarVista() {
        etiquetaVida.setText("Vida: " + motor.getVidaJugador());
        etiquetaMonedas.setText("Monedas: $" + motor.getMonedas());
        etiquetaOleada.setText("Oleada: " + motor.getNumeroOleadaActual()
                + "/" + motor.getTotalOleadas());
        etiquetaPuntuacion.setText("Puntos: " + motor.getPuntuacion());
        etiquetaCozy.setText("Cozy activos: " + motor.getCozyActivos().size());

        botonDeshacer.setEnabled(motor.getHistorial().puedeDeshacer()
                && !motor.isOleadaEnCurso());
        botonRehacer.setEnabled(motor.getHistorial().puedeRehacer()
                && !motor.isOleadaEnCurso());
        botonMejorar.setEnabled(!motor.isOleadaEnCurso()
                && !motor.isJuegoGanado() && !motor.isJuegoPerdido());
        botonPausa.setEnabled(motor.isOleadaEnCurso());
        botonPausa.setText(motor.isPausado() ? "Reanudar" : "Pausar");
        botonIniciar.setEnabled(!motor.isJuegoGanado() && !motor.isJuegoPerdido());
        botonIniciar.setText(motor.isPausado()
                ? "Reanudar oleada" : "Iniciar siguiente oleada");
        panelMapa.repaint();
    }

    private void registrarMensaje(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return;
        }
        areaEventos.append(mensaje.trim() + System.lineSeparator());
        areaEventos.setCaretPosition(areaEventos.getDocument().getLength());
    }

    private JLabel crearEtiquetaEstado() {
        JLabel etiqueta = new JLabel(" ", SwingConstants.CENTER);
        etiqueta.setOpaque(true);
        etiqueta.setBackground(new Color(34, 51, 68));
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 13f));
        etiqueta.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        return etiqueta;
    }
}
