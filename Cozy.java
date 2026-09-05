package defensatorres;

import java.util.List;

/** Contiene el estado de un Cozy durante la simulación. */
public final class Cozy {
    private final String id;
    private final TipoCozy tipo;
    private final int pvMaximos;
    private int pvActuales;
    private int indiceRuta;

    public Cozy(String id, TipoCozy tipo, int pvBase) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del Cozy es obligatorio.");
        }
        if (tipo == null || pvBase <= 0) {
            throw new IllegalArgumentException("Los datos del Cozy no son válidos.");
        }
        this.id = id;
        this.tipo = tipo;
        this.pvMaximos = Math.max(1, pvBase + tipo.getModificadorPv());
        this.pvActuales = pvMaximos;
        this.indiceRuta = 0;
    }

    public void recibirDanio(int danio) {
        if (danio < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo.");
        }
        pvActuales = Math.max(0, pvActuales - danio);
    }

    public boolean estaDerrotado() {
        return pvActuales == 0;
    }

    /**
     * Avanza según la velocidad del Cozy.
     *
     * @return true si alcanzó la fuente de energía.
     */
    public boolean avanzar(int longitudRuta) {
        if (longitudRuta <= 0) {
            return false;
        }
        indiceRuta = Math.min(longitudRuta - 1, indiceRuta + tipo.getVelocidad());
        return indiceRuta >= longitudRuta - 1;
    }

    public Posicion getPosicionActual(List<Posicion> ruta) {
        if (ruta == null || ruta.isEmpty()) {
            throw new IllegalStateException("No existe una ruta disponible.");
        }
        int indiceValido = Math.min(indiceRuta, ruta.size() - 1);
        return ruta.get(indiceValido);
    }

    public double getProporcionVida() {
        return (double) pvActuales / pvMaximos;
    }

    public String getId() {
        return id;
    }

    public TipoCozy getTipo() {
        return tipo;
    }

    public int getPvMaximos() {
        return pvMaximos;
    }

    public int getPvActuales() {
        return pvActuales;
    }

    public int getIndiceRuta() {
        return indiceRuta;
    }

    @Override
    public String toString() {
        return id + " [" + tipo.getNombre() + ", PV=" + pvActuales + "/" + pvMaximos + "]";
    }
}
