public class Gol {

    private Jugador jugador;
    private String nombreEquipo;
    private int minuto;

    public Gol(Jugador jugador, String nombreEquipo, int minuto) {
        this.jugador = jugador;
        this.nombreEquipo = nombreEquipo;
        this.minuto = minuto;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public int getMinuto() {
        return minuto;
    }

    @Override
    public String toString() {
        return "Min " + minuto + "' - Gol de " + jugador.getNombre() + " " + jugador.getApellido()
                + " (#" + jugador.getNumeroCamiseta() + ") [" + nombreEquipo + "]";
    }
}