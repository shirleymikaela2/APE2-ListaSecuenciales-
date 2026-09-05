package defensatorres;

/**
 * Tipos disponibles de Cozy. En este proyecto, la palabra Cozy reemplaza
 * obligatoriamente a la palabra Enemigo.
 */
public enum TipoCozy {
    NORMAL("Normal", 0, 1, 1, 20),
    RAPIDO("Rápido", -10, 2, 1, 25),
    RESISTENTE("Resistente", 30, 1, 2, 35);

    private final String nombre;
    private final int modificadorPv;
    private final int velocidad;
    private final int danioFuente;
    private final int recompensa;

    TipoCozy(String nombre, int modificadorPv, int velocidad,
            int danioFuente, int recompensa) {
        this.nombre = nombre;
        this.modificadorPv = modificadorPv;
        this.velocidad = velocidad;
        this.danioFuente = danioFuente;
        this.recompensa = recompensa;
    }

    public String getNombre() {
        return nombre;
    }

    public int getModificadorPv() {
        return modificadorPv;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getDanioFuente() {
        return danioFuente;
    }

    public int getRecompensa() {
        return recompensa;
    }
}
