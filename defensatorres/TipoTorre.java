package defensatorres;

public enum TipoTorre {

    BASICA(10, 2, 100),
    RAPIDA(5, 4, 120),
    FUERTE(25, 1, 200);

    private final int dano;
    private final int velocidadAtaque;
    private final int costo;

    TipoTorre(int dano, int velocidadAtaque, int costo) {
        this.dano = dano;
        this.velocidadAtaque = velocidadAtaque;
        this.costo = costo;
    }

    public int getDano() {
        return dano;
    }

    public int getVelocidadAtaque() {
        return velocidadAtaque;
    }

    public int getCosto() {
        return costo;
    }
}