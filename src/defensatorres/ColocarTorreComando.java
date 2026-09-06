package defensatorres;

/** Acción reversible para colocar una torre. */
public final class ColocarTorreComando implements ComandoTorre {
    private final MotorJuego motor;
    private final Torre torre;

    public ColocarTorreComando(MotorJuego motor, Torre torre) {
        this.motor = motor;
        this.torre = torre;
    }

    @Override
    public boolean ejecutar() {
        return motor.colocarTorreSinHistorial(torre);
    }

    @Override
    public void deshacer() {
        motor.quitarTorreSinHistorial(torre);
    }

    @Override
    public String getDescripcion() {
        return "Colocar " + torre.getTipo().getNombre() + " en " + torre.getPosicion();
    }
}
