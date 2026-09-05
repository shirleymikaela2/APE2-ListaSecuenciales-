package defensatorres;

/** Contrato de una acción reversible realizada sobre una torre. */
public interface ComandoTorre {
    boolean ejecutar();

    void deshacer();

    String getDescripcion();
}
