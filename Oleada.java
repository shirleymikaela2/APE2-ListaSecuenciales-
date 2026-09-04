package defensatorres;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Define la composición y dificultad de una oleada. */
public final class Oleada {
    private final int numero;
    private final int pvBase;
    private final List<TipoCozy> tipos;
    private int generados;

    public Oleada(int numero, int cantidadCozy, int pvBase) {
        if (numero <= 0 || cantidadCozy <= 0 || pvBase <= 0) {
            throw new IllegalArgumentException("Los datos de la oleada no son válidos.");
        }
        this.numero = numero;
        this.pvBase = pvBase;
        this.tipos = new ArrayList<TipoCozy>(cantidadCozy);
        for (int i = 0; i < cantidadCozy; i++) {
            if ((i + 1) % 5 == 0) {
                tipos.add(TipoCozy.RESISTENTE);
            } else if ((i + 1) % 3 == 0) {
                tipos.add(TipoCozy.RAPIDO);
            } else {
                tipos.add(TipoCozy.NORMAL);
            }
        }
    }

    public Cozy crearSiguienteCozy() {
        if (estaCompletamenteGenerada()) {
            return null;
        }
        TipoCozy tipo = tipos.get(generados);
        generados++;
        String id = String.format("O%02d-C%02d", numero, generados);
        return new Cozy(id, tipo, pvBase);
    }

    public boolean estaCompletamenteGenerada() {
        return generados >= tipos.size();
    }

    public int getNumero() {
        return numero;
    }

    public int getCantidadTotal() {
        return tipos.size();
    }

    public int getCantidadGenerada() {
        return generados;
    }

    public int getCantidadPendiente() {
        return tipos.size() - generados;
    }

    public int getPvBase() {
        return pvBase;
    }

    public List<TipoCozy> getTipos() {
        return Collections.unmodifiableList(tipos);
    }
}
