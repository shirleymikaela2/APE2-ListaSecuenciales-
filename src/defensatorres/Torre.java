package defensatorres;

/** Representa una torre colocada en el mapa. */
public final class Torre {
    public static final int NIVEL_MAXIMO = 3;

    private final TipoTorre tipo;
    private final Posicion posicion;
    private int nivel;

    public Torre(TipoTorre tipo, Posicion posicion) {
        if (tipo == null || posicion == null) {
            throw new IllegalArgumentException("El tipo y la posición son obligatorios.");
        }
        this.tipo = tipo;
        this.posicion = posicion;
        this.nivel = 1;
    }

    public boolean puedeAtacar(Posicion posicionCozy) {
        return posicion.distanciaA(posicionCozy) <= getAlcanceActual();
    }

    public boolean puedeMejorar() {
        return nivel < NIVEL_MAXIMO;
    }

    public void mejorar() {
        if (!puedeMejorar()) {
            throw new IllegalStateException("La torre ya alcanzó el nivel máximo.");
        }
        nivel++;
    }

    public void revertirMejora() {
        if (nivel <= 1) {
            throw new IllegalStateException("La torre ya está en el nivel inicial.");
        }
        nivel--;
    }

    public int getDanioActual() {
        double incremento = 1.0 + ((nivel - 1) * 0.55);
        return (int) Math.round(tipo.getDanioBase() * incremento);
    }

    public double getAlcanceActual() {
        return tipo.getAlcanceBase() + ((nivel - 1) * 0.25);
    }

    public int getCostoSiguienteMejora() {
        return tipo.calcularCostoMejora(nivel);
    }

    public TipoTorre getTipo() {
        return tipo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public int getNivel() {
        return nivel;
    }

    @Override
    public String toString() {
        return tipo.getNombre() + " nivel " + nivel + " en " + posicion;
    }
}
