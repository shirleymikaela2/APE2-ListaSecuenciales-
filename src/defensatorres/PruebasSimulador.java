package defensatorres;

import java.util.Arrays;

/** Pruebas de escritorio automatizadas sin dependencias externas. */
public final class PruebasSimulador {
    private static int pruebasCorrectas;

    private PruebasSimulador() {
    }

    public static void main(String[] args) {
        probarColaCircular();
        probarBusquedaRuta();
        probarDeshacerRehacer();
        probarOleadaCompleta();
        System.out.println("Pruebas correctas: " + pruebasCorrectas + "/4");
    }

    private static void probarColaCircular() {
        ColaCircular<Integer> cola = new ColaCircular<Integer>(3);
        cola.encolar(10);
        cola.encolar(20);
        verificar(cola.desencolar() == 10, "La cola debe respetar FIFO.");
        cola.encolar(30);
        cola.encolar(40);
        verificar(cola.comoLista().equals(Arrays.asList(20, 30, 40)),
                "Los índices deben circular sin perder el orden.");
        pruebasCorrectas++;
    }

    private static void probarBusquedaRuta() {
        Mapa mapa = new Mapa(5, 7, new Posicion(2, 0), new Posicion(2, 6));
        Torre obstaculo = new Torre(TipoTorre.BASICA, new Posicion(2, 3));
        verificar(mapa.colocarTorre(obstaculo),
                "Debe existir una ruta alternativa alrededor de la torre.");
        verificar(!mapa.getRuta().contains(new Posicion(2, 3)),
                "La ruta BFS no puede atravesar una torre.");
        verificar(mapa.getRuta().get(0).equals(mapa.getEntrada())
                        && mapa.getRuta().get(mapa.getRuta().size() - 1)
                                .equals(mapa.getFuente()),
                "La ruta debe unir la entrada con la fuente.");
        pruebasCorrectas++;
    }

    private static void probarDeshacerRehacer() {
        MotorJuego motor = new MotorJuego();
        int monedasIniciales = motor.getMonedas();
        Posicion posicion = new Posicion(3, 5);

        verificar(motor.colocarTorre(TipoTorre.BASICA, posicion),
                "La torre debe colocarse en una celda válida.");
        verificar(motor.getMapa().getTorres().size() == 1
                        && motor.getMonedas() < monedasIniciales,
                "Colocar debe agregar la torre y cobrar su costo.");

        verificar(motor.deshacer(), "Deshacer debe estar disponible.");
        verificar(motor.getMapa().getTorres().isEmpty()
                        && motor.getMonedas() == monedasIniciales,
                "Deshacer debe retirar la torre y devolver el costo.");

        verificar(motor.rehacer(), "Rehacer debe estar disponible.");
        verificar(motor.getMapa().getTorres().size() == 1,
                "Rehacer debe volver a colocar la torre.");
        pruebasCorrectas++;
    }

    private static void probarOleadaCompleta() {
        MotorJuego motor = new MotorJuego();
        verificar(motor.iniciarSiguienteOleada(),
                "La primera oleada debe iniciar.");
        int limite = 200;
        while (motor.isOleadaEnCurso() && limite-- > 0) {
            motor.procesarQuantum();
        }
        verificar(limite > 0, "La simulación no debe quedar en un ciclo infinito.");
        verificar(!motor.isOleadaEnCurso()
                        && motor.getNumeroOleadaActual() == 1,
                "La primera oleada debe finalizar y vaciar la cola.");
        pruebasCorrectas++;
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
