package defensatorres;

public class MejorarTorreComando implements ComandoTorre {

    private final Torre torre;

    public MejorarTorreComando(Torre torre) {
        this.torre = torre;
    }

    @Override
    public void ejecutar() {
        torre.mejorar();
    }

    @Override
    public void deshacer() {
        torre.deshacerMejora();
    }
}