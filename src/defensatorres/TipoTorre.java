package defensatorres;

/** Catálogo secuencial de torres disponibles para el jugador. */
public enum TipoTorre {
    BASICA("Básica", 80, 15, 2.4),
    RAPIDA("Rápida", 110, 10, 3.2),
    PESADA("Pesada", 160, 30, 1.9);

    private final String nombre;
    private final int costoBase;
    private final int danioBase;
    private final double alcanceBase;

    TipoTorre(String nombre, int costoBase, int danioBase, double alcanceBase) {
        this.nombre = nombre;
        this.costoBase = costoBase;
        this.danioBase = danioBase;
        this.alcanceBase = alcanceBase;
    }

    public int calcularCostoMejora(int nivelActual) {
        return (costoBase / 2) + (nivelActual * 20);
    }

    public String getNombre() {
        return nombre;
    }

    public int getCostoBase() {
        return costoBase;
    }

    public int getDanioBase() {
        return danioBase;
    }

    public double getAlcanceBase() {
        return alcanceBase;
    }
}
