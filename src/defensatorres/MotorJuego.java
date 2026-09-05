package defensatorres;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Coordina el mapa, las oleadas, el combate y el historial de comandos.
 */
public final class MotorJuego {
    private static final int VIDA_INICIAL = 20;
    private static final int MONEDAS_INICIALES = 500;
    private static final int CAPACIDAD_COLA_COZY = 128;

    private final Mapa mapa;
    private final ColaCircular<Cozy> colaCozy;
    private final GestorOleadas gestorOleadas;
    private final HistorialComandos historial;

    private int vidaJugador;
    private int monedas;
    private int puntuacion;
    private int ultimaOleadaIniciada;
    private boolean oleadaEnCurso;
    private boolean pausado;
    private boolean juegoPerdido;
    private boolean juegoGanado;
    private String ultimoMensaje;

    public MotorJuego() {
        mapa = new Mapa(8, 12, new Posicion(3, 0), new Posicion(3, 11));
        colaCozy = new ColaCircular<Cozy>(CAPACIDAD_COLA_COZY);
        gestorOleadas = new GestorOleadas();
        historial = new HistorialComandos();
        reiniciar();
    }

    public boolean colocarTorre(TipoTorre tipo, Posicion posicion) {
        if (!sePuedeEditarMapa()) {
            ultimoMensaje = "Las torres solo se modifican entre oleadas.";
            return false;
        }
        if (tipo == null || posicion == null) {
            ultimoMensaje = "Seleccione un tipo de torre y una celda válida.";
            return false;
        }
        if (monedas < tipo.getCostoBase()) {
            ultimoMensaje = "No hay monedas suficientes para esa torre.";
            return false;
        }
        if (mapa.buscarTorre(posicion) != null) {
            ultimoMensaje = "La celda seleccionada ya contiene una torre.";
            return false;
        }

        Torre torre = new Torre(tipo, posicion);
        if (!historial.ejecutar(new ColocarTorreComando(this, torre))) {
            ultimoMensaje = "No se puede colocar allí: la ruta quedaría bloqueada o la celda no es válida.";
            return false;
        }
        ultimoMensaje = "Torre colocada: " + torre + ".";
        return true;
    }

    public boolean mejorarTorre(Posicion posicion) {
        if (!sePuedeEditarMapa()) {
            ultimoMensaje = "Las torres solo se mejoran entre oleadas.";
            return false;
        }

        Torre torre = mapa.buscarTorre(posicion);
        if (torre == null) {
            ultimoMensaje = "Seleccione primero una torre existente.";
            return false;
        }
        if (!torre.puedeMejorar()) {
            ultimoMensaje = "La torre ya está en el nivel máximo.";
            return false;
        }

        int costo = torre.getCostoSiguienteMejora();
        if (monedas < costo) {
            ultimoMensaje = "Se necesitan " + costo + " monedas para mejorarla.";
            return false;
        }
        if (!historial.ejecutar(new MejorarTorreComando(this, torre))) {
            ultimoMensaje = "No fue posible mejorar la torre.";
            return false;
        }

        ultimoMensaje = "Torre mejorada a nivel " + torre.getNivel() + ".";
        return true;
    }

    public boolean deshacer() {
        if (!sePuedeEditarMapa()) {
            ultimoMensaje = "Deshacer está disponible únicamente entre oleadas.";
            return false;
        }

        ComandoTorre comando = historial.deshacer();
        if (comando == null) {
            ultimoMensaje = "No existen acciones para deshacer.";
            return false;
        }

        ultimoMensaje = "Acción deshecha: " + comando.getDescripcion() + ".";
        return true;
    }

    public boolean rehacer() {
        if (!sePuedeEditarMapa()) {
            ultimoMensaje = "Rehacer está disponible únicamente entre oleadas.";
            return false;
        }

        ComandoTorre comando = historial.rehacer();
        if (comando == null) {
            ultimoMensaje = "No existen acciones válidas para rehacer.";
            return false;
        }

        ultimoMensaje = "Acción rehecha: " + comando.getDescripcion() + ".";
        return true;
    }

    public boolean iniciarSiguienteOleada() {
        if (juegoPerdido || juegoGanado) {
            ultimoMensaje = "Reinicie la partida para volver a jugar.";
            return false;
        }

        if (oleadaEnCurso) {
            if (pausado) {
                pausado = false;
                ultimoMensaje = "Simulación reanudada.";
                return true;
            }

            ultimoMensaje = "Ya existe una oleada en curso.";
            return false;
        }

        Oleada siguiente = gestorOleadas.iniciarSiguiente();
        if (siguiente == null) {
            juegoGanado = true;
            ultimoMensaje = "Todas las oleadas fueron superadas.";
            return false;
        }

        ultimaOleadaIniciada = siguiente.getNumero();
        oleadaEnCurso = true;
        pausado = false;
        ultimoMensaje = "Inició la oleada " + siguiente.getNumero()
                + " con " + siguiente.getCantidadTotal() + " Cozy.";
        return true;
    }

    public boolean alternarPausa() {
        if (!oleadaEnCurso) {
            ultimoMensaje = "No existe una oleada para pausar.";
            return false;
        }

        pausado = !pausado;
        ultimoMensaje = pausado
                ? "Simulación pausada."
                : "Simulación reanudada.";
        return true;
    }

    /**
     * Atiende una vuelta completa de la cola circular. Cada Cozy sale del
     * frente y solo vuelve al final si continúa con PV y no alcanzó la fuente.
     */
    public ResultadoQuantum procesarQuantum() {
        if (!oleadaEnCurso || pausado || juegoPerdido || juegoGanado) {
            return ResultadoQuantum.sinCambios("");
        }

        Oleada actual = gestorOleadas.getActual();
        if (actual == null) {
            return ResultadoQuantum.sinCambios("");
        }

        StringBuilder eventos = new StringBuilder();

        Cozy nuevo = actual.crearSiguienteCozy();
        if (nuevo != null) {
            colaCozy.encolar(nuevo);
            eventos.append("Apareció ").append(nuevo.getId()).append(". ");
        }

        int cantidadAProcesar = colaCozy.tamanio();
        int derrotados = 0;
        int escaparon = 0;

        Set<Torre> torresQueDispararon = new HashSet<Torre>();
        List<Posicion> ruta = mapa.getRuta();

        for (int i = 0; i < cantidadAProcesar; i++) {
            Cozy cozy = colaCozy.desencolar();
            Posicion posicionCozy = cozy.getPosicionActual(ruta);

            for (Torre torre : mapa.getTorres()) {
                if (!torresQueDispararon.contains(torre)
                        && torre.puedeAtacar(posicionCozy)) {
                    cozy.recibirDanio(torre.getDanioActual());
                    torresQueDispararon.add(torre);
                    break;
                }
            }

            if (cozy.estaDerrotado()) {
                derrotados++;
                puntuacion += 100 + (actual.getNumero() * 10);
                monedas += cozy.getTipo().getRecompensa();
                continue;
            }

            if (cozy.avanzar(ruta.size())) {
                escaparon++;
                vidaJugador = Math.max(
                        0,
                        vidaJugador - cozy.getTipo().getDanioFuente()
                );
            } else {
                colaCozy.encolar(cozy);
            }
        }

        if (derrotados > 0) {
            eventos.append("Derrotados: ")
                    .append(derrotados)
                    .append(". ");
        }

        if (escaparon > 0) {
            eventos.append("Llegaron a la fuente: ")
                    .append(escaparon)
                    .append(". ");
        }

        if (vidaJugador <= 0) {
            colaCozy.limpiar();
            oleadaEnCurso = false;
            pausado = false;
            juegoPerdido = true;

            eventos.append("La fuente quedó sin vida. Fin de la partida.");
            ultimoMensaje = eventos.toString().trim();

            return new ResultadoQuantum(
                    ultimoMensaje,
                    derrotados,
                    escaparon,
                    false,
                    true
            );
        }

        boolean oleadaTerminada =
                actual.estaCompletamenteGenerada() && colaCozy.estaVacia();

        if (oleadaTerminada) {
            int numeroTerminada = actual.getNumero();

            gestorOleadas.cerrarActual();
            oleadaEnCurso = false;
            pausado = false;
            monedas += 50 * numeroTerminada;

            eventos.append("Oleada ")
                    .append(numeroTerminada)
                    .append(" superada. ");

            if (!gestorOleadas.hayPendientes()) {
                juegoGanado = true;
                eventos.append(
                        "¡Victoria! Todas las oleadas fueron superadas."
                );
            }
        }

        ultimoMensaje = eventos.toString().trim();

        return new ResultadoQuantum(
                ultimoMensaje,
                derrotados,
                escaparon,
                oleadaTerminada,
                juegoGanado
        );
    }

    public void reiniciar() {
        mapa.limpiarTorres();
        colaCozy.limpiar();
        gestorOleadas.reiniciar();
        historial.limpiar();

        vidaJugador = VIDA_INICIAL;
        monedas = MONEDAS_INICIALES;
        puntuacion = 0;
        ultimaOleadaIniciada = 0;
        oleadaEnCurso = false;
        pausado = false;
        juegoPerdido = false;
        juegoGanado = false;
        ultimoMensaje =
                "Partida preparada. Coloque torres antes de iniciar.";
    }

    boolean colocarTorreSinHistorial(Torre torre) {
        if (torre == null || monedas < torre.getTipo().getCostoBase()) {
            return false;
        }

        if (!mapa.colocarTorre(torre)) {
            return false;
        }

        monedas -= torre.getTipo().getCostoBase();
        return true;
    }

    void quitarTorreSinHistorial(Torre torre) {
        if (mapa.quitarTorre(torre)) {
            monedas += torre.getTipo().getCostoBase();
        }
    }

    int mejorarTorreSinHistorial(Torre torre) {
        if (torre == null
                || !mapa.getTorres().contains(torre)
                || !torre.puedeMejorar()) {
            return -1;
        }

        int costo = torre.getCostoSiguienteMejora();
        if (monedas < costo) {
            return -1;
        }

        torre.mejorar();
        monedas -= costo;
        return costo;
    }

    void revertirMejoraSinHistorial(Torre torre, int costoPagado) {
        if (torre != null
                && mapa.getTorres().contains(torre)
                && torre.getNivel() > 1) {
            torre.revertirMejora();
            monedas += costoPagado;
        }
    }

    private boolean sePuedeEditarMapa() {
        return !oleadaEnCurso && !juegoPerdido && !juegoGanado;
    }

    public Mapa getMapa() {
        return mapa;
    }

    public List<Cozy> getCozyActivos() {
        return Collections.unmodifiableList(colaCozy.comoLista());
    }

    public int getVidaJugador() {
        return vidaJugador;
    }

    public int getMonedas() {
        return monedas;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public int getNumeroOleadaActual() {
        return ultimaOleadaIniciada;
    }

    public int getTotalOleadas() {
        return gestorOleadas.getTotalOleadas();
    }

    public boolean isOleadaEnCurso() {
        return oleadaEnCurso;
    }

    public boolean isPausado() {
        return pausado;
    }

    public boolean isJuegoPerdido() {
        return juegoPerdido;
    }

    public boolean isJuegoGanado() {
        return juegoGanado;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public HistorialComandos getHistorial() {
        return historial;
    }
}