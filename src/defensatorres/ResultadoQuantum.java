package defensatorres;

/**
 * Resume lo ocurrido durante un quantum de tiempo.
 */
public final class ResultadoQuantum {
    private final String mensaje;
    private final int derrotados;
    private final int escaparon;
    private final boolean oleadaTerminada;
    private final boolean juegoTerminado;

    public ResultadoQuantum(
            String mensaje,
            int derrotados,
            int escaparon,
            boolean oleadaTerminada,
            boolean juegoTerminado) {

        this.mensaje = mensaje;
        this.derrotados = derrotados;
        this.escaparon = escaparon;
        this.oleadaTerminada = oleadaTerminada;
        this.juegoTerminado = juegoTerminado;
    }

    public static ResultadoQuantum sinCambios(String mensaje) {
        return new ResultadoQuantum(
                mensaje,
                0,
                0,
                false,
                false
        );
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getDerrotados() {
        return derrotados;
    }

    public int getEscaparon() {
        return escaparon;
    }

    public boolean isOleadaTerminada() {
        return oleadaTerminada;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
}