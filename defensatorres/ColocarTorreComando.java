package defensatorres;

public class ColocarTorreComando implements ComandoTorre {

    private final Mapa mapa;
    private final Torre torre;

    public ColocarTorreComando(Mapa mapa, Torre torre) {
        this.mapa = mapa;
        this.torre = torre;
    }

    @Override
    public void ejecutar() {
        mapa.agregarTorre(torre);
    }

    @Override
    public void deshacer() {
        mapa.eliminarTorre(torre);
    }
}