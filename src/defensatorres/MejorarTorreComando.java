package defensatorres;

/** Acción reversible para mejorar una torre. */
public final class MejorarTorreComando implements ComandoTorre {
    private final MotorJuego motor;
    private final Torre torre;
    private int costoPagado;

    public MejorarTorreComando(MotorJuego motor, Torre torre) {
        this.motor = motor;
        this.torre = torre;
    }

    @Override
    public boolean ejecutar() {
        int costo = motor.mejorarTorreSinHistorial(torre);
        if (costo < 0) {
            return false;
        }
        costoPagado = costo;
        return true;
    }

    @Override
    public void deshacer() {
        motor.revertirMejoraSinHistorial(torre, costoPagado);
    }

    @Override
    public String getDescripcion() {
        return "Mejorar " + torre.getTipo().getNombre() + " en " + torre.getPosicion();
    }
}
