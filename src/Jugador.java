public class Jugador extends Persona {

    private boolean esCapitan;
    private int numeroCamiseta;
    private int partidosSuspension;

    public Jugador(String nombre, String apellido, int edad, String cedula, String telefono,
                   boolean esCapitan, int numeroCamiseta) {
        super(nombre, apellido, edad, cedula, telefono);
        this.esCapitan = esCapitan;
        this.numeroCamiseta = numeroCamiseta;
        this.partidosSuspension = 0;
    }

    public boolean isEsCapitan() {
        return esCapitan;
    }

    public void setEsCapitan(boolean esCapitan) {
        this.esCapitan = esCapitan;
    }

    public int getNumeroCamiseta() {
        return numeroCamiseta;
    }

    public void setNumeroCamiseta(int numeroCamiseta) {
        this.numeroCamiseta = numeroCamiseta;
    }

    public int getPartidosSuspension() {
        return partidosSuspension;
    }

    public void setPartidosSuspension(int partidosSuspension) {
        this.partidosSuspension = partidosSuspension;
    }

    public boolean estaHabilitado() {
        return partidosSuspension <= 0;
    }

    public void descontarPartidoSuspension() {
        if (partidosSuspension > 0) {
            partidosSuspension--;
        }
    }

    @Override
    public String toString() {
        String estado = estaHabilitado() ? "" : " [SUSPENDIDO: " + partidosSuspension + " partido(s)]";
        return "#" + numeroCamiseta + " - " + super.toString()
                + (esCapitan ? " [CAPITÁN / DELEGADO]" : "") + estado;
    }
}