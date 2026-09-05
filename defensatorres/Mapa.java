package defensatorres;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Mantiene las torres y la ruta válida entre la entrada y la fuente. */
public final class Mapa {
    private final int filas;
    private final int columnas;
    private final Posicion entrada;
    private final Posicion fuente;
    private final List<Torre> torres;
    private List<Posicion> ruta;

    public Mapa(int filas, int columnas, Posicion entrada, Posicion fuente) {
        this.filas = filas;
        this.columnas = columnas;
        this.entrada = entrada;
        this.fuente = fuente;
        this.torres = new ArrayList<Torre>();
        this.ruta = calcularRuta();
        if (ruta.isEmpty()) {
            throw new IllegalArgumentException("El mapa inicial no tiene una ruta válida.");
        }
    }

    /**
     * Coloca una torre solo si la celda es válida y BFS todavía encuentra ruta.
     */
    public boolean colocarTorre(Torre torre) {
        if (torre == null || !estaDentro(torre.getPosicion())
                || torre.getPosicion().equals(entrada)
                || torre.getPosicion().equals(fuente)
                || buscarTorre(torre.getPosicion()) != null) {
            return false;
        }

        torres.add(torre);
        List<Posicion> nuevaRuta = calcularRuta();
        if (nuevaRuta.isEmpty()) {
            torres.remove(torre);
            return false;
        }
        ruta = nuevaRuta;
        return true;
    }

    public boolean quitarTorre(Torre torre) {
        if (torre == null || !torres.remove(torre)) {
            return false;
        }
        ruta = calcularRuta();
        return true;
    }

    public Torre buscarTorre(Posicion posicion) {
        for (Torre torre : torres) {
            if (torre.getPosicion().equals(posicion)) {
                return torre;
            }
        }
        return null;
    }

    public void limpiarTorres() {
        torres.clear();
        ruta = calcularRuta();
    }

    private List<Posicion> calcularRuta() {
        Set<Posicion> bloqueadas = new HashSet<Posicion>();
        for (Torre torre : torres) {
            bloqueadas.add(torre.getPosicion());
        }
        return BuscadorRuta.buscar(filas, columnas, entrada, fuente, bloqueadas);
    }

    public boolean estaDentro(Posicion posicion) {
        return posicion != null
                && posicion.getFila() >= 0 && posicion.getFila() < filas
                && posicion.getColumna() >= 0 && posicion.getColumna() < columnas;
    }

    public List<Torre> getTorres() {
        return Collections.unmodifiableList(new ArrayList<Torre>(torres));
    }

    public List<Posicion> getRuta() {
        return Collections.unmodifiableList(new ArrayList<Posicion>(ruta));
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public Posicion getEntrada() {
        return entrada;
    }

    public Posicion getFuente() {
        return fuente;
    }
}
