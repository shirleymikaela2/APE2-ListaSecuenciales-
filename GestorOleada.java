package defensatorres;

import java.util.ArrayDeque;
import java.util.Queue;

/** Gestiona el orden FIFO de las oleadas. */
public final class GestorOleadas {
    private static final int TOTAL_OLEADAS = 5;

    private final Queue<Oleada> pendientes;
    private Oleada actual;

    public GestorOleadas() {
        pendientes = new ArrayDeque<Oleada>();
        reiniciar();
    }

    public void reiniciar() {
        pendientes.clear();
        actual = null;
        for (int numero = 1; numero <= TOTAL_OLEADAS; numero++) {
            int cantidad = 3 + (numero * 2);
            int pvBase = 30 + (numero * 15);
            pendientes.offer(new Oleada(numero, cantidad, pvBase));
        }
    }

    public Oleada iniciarSiguiente() {
        if (actual != null) {
            return actual;
        }
        actual = pendientes.poll();
        return actual;
    }

    public void cerrarActual() {
        actual = null;
    }

    public boolean hayPendientes() {
        return !pendientes.isEmpty();
    }

    public int getCantidadPendiente() {
        return pendientes.size();
    }

    public Oleada getActual() {
        return actual;
    }

    public int getTotalOleadas() {
        return TOTAL_OLEADAS;
    }
}
