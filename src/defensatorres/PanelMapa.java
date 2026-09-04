package defensatorres;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.JPanel;

/** Vista grafica del mapa, la ruta, las torres y los Cozy activos. */
public final class PanelMapa extends JPanel {
    private static final Color COLOR_CESPED = new Color(226, 242, 218);
    private static final Color COLOR_RUTA = new Color(246, 220, 162);
    private static final Color COLOR_CUADRICULA = new Color(154, 170, 148);

    private final MotorJuego motor;
    private Consumer<Posicion> alSeleccionar;
    private Posicion seleccionada;

    public PanelMapa(MotorJuego motor) {
        this.motor = motor;
        setPreferredSize(new Dimension(780, 520));
        setBackground(COLOR_CESPED);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                Posicion posicion = convertirClic(evento.getX(), evento.getY());
                if (posicion != null && alSeleccionar != null) {
                    alSeleccionar.accept(posicion);
                }
            }
        });
    }

    public void setAlSeleccionar(Consumer<Posicion> alSeleccionar) {
        this.alSeleccionar = alSeleccionar;
    }

    public void setSeleccionada(Posicion seleccionada) {
        this.seleccionada = seleccionada;
        repaint();
    }

    private Posicion convertirClic(int x, int y) {
        Mapa mapa = motor.getMapa();
        int anchoCelda = Math.max(1, getWidth() / mapa.getColumnas());
        int altoCelda = Math.max(1, getHeight() / mapa.getFilas());
        int columna = x / anchoCelda;
        int fila = y / altoCelda;
        Posicion posicion = new Posicion(fila, columna);
        return mapa.estaDentro(posicion) ? posicion : null;
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        Graphics2D g2 = (Graphics2D) graficos.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Mapa mapa = motor.getMapa();
        int anchoCelda = Math.max(1, getWidth() / mapa.getColumnas());
        int altoCelda = Math.max(1, getHeight() / mapa.getFilas());
        Set<Posicion> ruta = new HashSet<Posicion>(mapa.getRuta());

        for (int fila = 0; fila < mapa.getFilas(); fila++) {
            for (int columna = 0; columna < mapa.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                int x = columna * anchoCelda;
                int y = fila * altoCelda;

                if (posicion.equals(mapa.getEntrada())) {
                    g2.setColor(new Color(80, 180, 105));
                } else if (posicion.equals(mapa.getFuente())) {
                    g2.setColor(new Color(84, 127, 205));
                } else if (ruta.contains(posicion)) {
                    g2.setColor(COLOR_RUTA);
                } else {
                    g2.setColor(COLOR_CESPED);
                }
                g2.fillRect(x, y, anchoCelda, altoCelda);
                g2.setColor(COLOR_CUADRICULA);
                g2.drawRect(x, y, anchoCelda, altoCelda);
            }
        }

        dibujarTorres(g2, anchoCelda, altoCelda, mapa.getTorres());
        dibujarCozy(g2, anchoCelda, altoCelda, mapa.getRuta(),
                motor.getCozyActivos());
        dibujarEtiquetas(g2, anchoCelda, altoCelda, mapa);
        g2.dispose();
    }

    private void dibujarTorres(Graphics2D g2, int ancho, int alto,
            List<Torre> torres) {
        for (Torre torre : torres) {
            Posicion posicion = torre.getPosicion();
            int x = posicion.getColumna() * ancho;
            int y = posicion.getFila() * alto;
            int margen = Math.max(6, Math.min(ancho, alto) / 7);

            g2.setColor(colorTorre(torre.getTipo()));
            g2.fillOval(x + margen, y + margen,
                    ancho - (2 * margen), alto - (2 * margen));
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(x + margen, y + margen,
                    ancho - (2 * margen), alto - (2 * margen));

            if (posicion.equals(seleccionada)) {
                g2.setStroke(new BasicStroke(4f));
                g2.setColor(new Color(255, 193, 7));
                g2.drawRect(x + 3, y + 3, ancho - 6, alto - 6);
            }

            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD,
                    Math.max(12f, Math.min(ancho, alto) * 0.25f)));
            String texto = inicialTorre(torre.getTipo()) + torre.getNivel();
            int textoX = x + (ancho - g2.getFontMetrics().stringWidth(texto)) / 2;
            int textoY = y + (alto + g2.getFontMetrics().getAscent()) / 2 - 2;
            g2.drawString(texto, textoX, textoY);
        }
    }

    private void dibujarCozy(Graphics2D g2, int ancho, int alto,
            List<Posicion> ruta, List<Cozy> cozyActivos) {
        int indiceVisual = 0;
        for (Cozy cozy : cozyActivos) {
            Posicion posicion = cozy.getPosicionActual(ruta);
            int diametro = Math.max(16, Math.min(ancho, alto) / 3);
            int desplazamiento = (indiceVisual % 3) * 4;
            int x = posicion.getColumna() * ancho + (ancho - diametro) / 2
                    + desplazamiento - 4;
            int y = posicion.getFila() * alto + (alto - diametro) / 2
                    + desplazamiento - 4;

            g2.setColor(colorCozy(cozy.getTipo()));
            g2.fillOval(x, y, diametro, diametro);
            g2.setColor(new Color(90, 35, 35));
            g2.drawOval(x, y, diametro, diametro);

            int anchoBarra = Math.max(18, ancho - 14);
            int barraX = posicion.getColumna() * ancho + 7;
            int barraY = posicion.getFila() * alto + 5;
            g2.setColor(new Color(80, 80, 80));
            g2.fillRect(barraX, barraY, anchoBarra, 5);
            g2.setColor(new Color(69, 190, 92));
            g2.fillRect(barraX, barraY,
                    (int) Math.round(anchoBarra * cozy.getProporcionVida()), 5);
            indiceVisual++;
        }
    }

    private void dibujarEtiquetas(Graphics2D g2, int ancho, int alto, Mapa mapa) {
        g2.setFont(getFont().deriveFont(Font.BOLD, 10f));
        g2.setColor(Color.WHITE);
        Posicion entrada = mapa.getEntrada();
        g2.drawString("INICIO", entrada.getColumna() * ancho + 5,
                entrada.getFila() * alto + alto - 7);
        Posicion fuente = mapa.getFuente();
        g2.drawString("FUENTE", fuente.getColumna() * ancho + 4,
                fuente.getFila() * alto + alto - 7);
    }

    private Color colorTorre(TipoTorre tipo) {
        switch (tipo) {
            case RAPIDA:
                return new Color(33, 150, 243);
            case PESADA:
                return new Color(103, 58, 183);
            default:
                return new Color(255, 152, 0);
        }
    }

    private Color colorCozy(TipoCozy tipo) {
        switch (tipo) {
            case RAPIDO:
                return new Color(255, 193, 7);
            case RESISTENTE:
                return new Color(142, 36, 170);
            default:
                return new Color(229, 57, 53);
        }
    }

    private String inicialTorre(TipoTorre tipo) {
        switch (tipo) {
            case RAPIDA:
                return "R";
            case PESADA:
                return "P";
            default:
                return "B";
        }
    }
}
