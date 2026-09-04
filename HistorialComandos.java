package defensatorres;

import java.util.Stack;

/**
 * Mantiene dos pilas LIFO: una para deshacer y otra para rehacer.
 */
public final class HistorialComandos {
    private final Stack<ComandoTorre> pilaDeshacer;
    private final Stack<ComandoTorre> pilaRehacer;

    public HistorialComandos() {
        pilaDeshacer = new Stack<ComandoTorre>();
        pilaRehacer = new Stack<ComandoTorre>();
    }

    public boolean ejecutar(ComandoTorre comando) {
        if (comando == null || !comando.ejecutar()) {
            return false;
        }
        pilaDeshacer.push(comando);
        pilaRehacer.clear();
        return true;
    }

    public ComandoTorre deshacer() {
        if (pilaDeshacer.empty()) {
            return null;
        }
        ComandoTorre comando = pilaDeshacer.pop();
        comando.deshacer();
        pilaRehacer.push(comando);
        return comando;
    }

    public ComandoTorre rehacer() {
        if (pilaRehacer.empty()) {
            return null;
        }
        ComandoTorre comando = pilaRehacer.pop();
        if (!comando.ejecutar()) {
            pilaRehacer.push(comando);
            return null;
        }
        pilaDeshacer.push(comando);
        return comando;
    }

    public void limpiar() {
        pilaDeshacer.clear();
        pilaRehacer.clear();
    }

    public boolean puedeDeshacer() {
        return !pilaDeshacer.empty();
    }

    public boolean puedeRehacer() {
        return !pilaRehacer.empty();
    }

    public int getCantidadDeshacer() {
        return pilaDeshacer.size();
    }

    public int getCantidadRehacer() {
        return pilaRehacer.size();
    }
}