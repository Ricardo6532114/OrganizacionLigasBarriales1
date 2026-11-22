public class Tarjeta {

    private Jugador jugador;
    private String nombreEquipo;
    private String tipo; // "AMARILLA", "DOBLE_AMARILLA_ROJA", "ROJA_DIRECTA"
    private String motivo;
    private int minuto;
    private int partidosSuspension;
    private boolean expulsion;

    public Tarjeta(Jugador jugador, String nombreEquipo, String tipo, String motivo,
                   int minuto, int partidosSuspension, boolean expulsion) {
        this.jugador = jugador;
        this.nombreEquipo = nombreEquipo;
        this.tipo = tipo;
        this.motivo = motivo;
        this.minuto = minuto;
        this.partidosSuspension = partidosSuspension;
        this.expulsion = expulsion;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public int getMinuto() {
        return minuto;
    }

    public int getPartidosSuspension() {
        return partidosSuspension;
    }

    public boolean isExpulsion() {
        return expulsion;
    }

    @Override
    public String toString() {
        return "Min " + minuto + "' - " + tipo + " a " + jugador.getNombre() + " " + jugador.getApellido()
                + " (" + nombreEquipo + ") | Motivo: " + motivo
                + (expulsion ? " | EXPULSADO (" + partidosSuspension + " partido(s))" : " | Llamada de atención");
    }
}