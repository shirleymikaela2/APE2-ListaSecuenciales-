package defensatorres;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/** Busca la ruta más corta mediante búsqueda en anchura (BFS). */
public final class BuscadorRuta {
    private static final int[][] DIRECCIONES = {
        {0, 1}, {1, 0}, {-1, 0}, {0, -1}
    };

    private BuscadorRuta() {
    }

    public static List<Posicion> buscar(int filas, int columnas,
            Posicion entrada, Posicion fuente, Set<Posicion> bloqueadas) {
        if (filas <= 0 || columnas <= 0 || entrada == null || fuente == null) {
            throw new IllegalArgumentException("Los datos del mapa no son válidos.");
        }

        Set<Posicion> obstaculos = bloqueadas == null
                ? Collections.<Posicion>emptySet()
                : new HashSet<Posicion>(bloqueadas);
        if (obstaculos.contains(entrada) || obstaculos.contains(fuente)) {
            return Collections.emptyList();
        }

        Queue<Posicion> pendientes = new ArrayDeque<Posicion>();
        Set<Posicion> visitadas = new HashSet<Posicion>();
        Map<Posicion, Posicion> anterior = new HashMap<Posicion, Posicion>();

        pendientes.offer(entrada);
        visitadas.add(entrada);

        while (!pendientes.isEmpty()) {
            Posicion actual = pendientes.poll();
            if (actual.equals(fuente)) {
                return reconstruirRuta(anterior, fuente);
            }

            for (int[] direccion : DIRECCIONES) {
                Posicion vecina = new Posicion(
                        actual.getFila() + direccion[0],
                        actual.getColumna() + direccion[1]);

                if (estaDentro(vecina, filas, columnas)
                        && !obstaculos.contains(vecina)
                        && visitadas.add(vecina)) {
                    anterior.put(vecina, actual);
                    pendientes.offer(vecina);
                }
            }
        }
        return Collections.emptyList();
    }

    private static boolean estaDentro(Posicion posicion, int filas, int columnas) {
        return posicion.getFila() >= 0 && posicion.getFila() < filas
                && posicion.getColumna() >= 0 && posicion.getColumna() < columnas;
    }

    private static List<Posicion> reconstruirRuta(
            Map<Posicion, Posicion> anterior, Posicion fuente) {
        LinkedList<Posicion> ruta = new LinkedList<Posicion>();
        Posicion actual = fuente;
        while (actual != null) {
            ruta.addFirst(actual);
            actual = anterior.get(actual);
        }
        return Collections.unmodifiableList(new ArrayList<Posicion>(ruta));
    }
}
