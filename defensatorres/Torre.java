package defensatorres;

public class Torre {

    private TipoTorre tipo;
    private Posicion posicion;
    private int nivel;
    private int alcance;

    public Torre(TipoTorre tipo, Posicion posicion) {
        this.tipo = tipo;
        this.posicion = posicion;
        this.nivel = 1;
        this.alcance = 2;
    }

    public TipoTorre getTipo() {
        return tipo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public int getNivel() {
        return nivel;
    }

    public int getAlcance() {
        return alcance;
    }

    public int getDano() {
        return tipo.getDano() * nivel;
    }

    public void mejorar() {
        nivel++;
        alcance++;
    }

    @Override
    public String toString() {
        return "Torre{" +
                "tipo=" + tipo +
                ", posicion=" + posicion +
                ", nivel=" + nivel +
                ", alcance=" + alcance +
                ", dano=" + getDano() +
                '}';
    }
}