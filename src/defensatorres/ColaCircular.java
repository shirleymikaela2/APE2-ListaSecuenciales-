package defensatorres;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Cola circular genérica de capacidad fija. Los índices vuelven a cero al
 * alcanzar el final del arreglo, evitando desplazamientos de elementos.
 */
public final class ColaCircular<T> {
    private final Object[] elementos;
    private int frente;
    private int fin;
    private int cantidad;

    public ColaCircular(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
        elementos = new Object[capacidad];
    }

    public void encolar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede encolar un valor null.");
        }
        if (estaLlena()) {
            throw new IllegalStateException("La cola circular está llena.");
        }
        elementos[fin] = elemento;
        fin = (fin + 1) % elementos.length;
        cantidad++;
    }

    @SuppressWarnings("unchecked")
    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola circular está vacía.");
        }
        T elemento = (T) elementos[frente];
        elementos[frente] = null;
        frente = (frente + 1) % elementos.length;
        cantidad--;
        return elemento;
    }

    @SuppressWarnings("unchecked")
    public T verFrente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola circular está vacía.");
        }
        return (T) elementos[frente];
    }

    public boolean estaVacia() {
        return cantidad == 0;
    }

    public boolean estaLlena() {
        return cantidad == elementos.length;
    }

    public int tamanio() {
        return cantidad;
    }

    public int capacidad() {
        return elementos.length;
    }

    public void limpiar() {
        while (!estaVacia()) {
            desencolar();
        }
        frente = 0;
        fin = 0;
    }

    @SuppressWarnings("unchecked")
    public List<T> comoLista() {
        List<T> copia = new ArrayList<T>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            int indice = (frente + i) % elementos.length;
            copia.add((T) elementos[indice]);
        }
        return Collections.unmodifiableList(copia);
    }
}
