import java.util.ArrayList;
import java.util.List;

public class ActaPartido {

    private Partido partido;
    private int minimoJugadoresCancha;
    private boolean cerrada;
    private List<Jugador> alineacionLocal = new ArrayList<>();
    private List<Jugador> alineacionVisitante = new ArrayList<>();
    private List<Gol> goles = new ArrayList<>();
    private List<Tarjeta> tarjetas = new ArrayList<>();

    public ActaPartido(Partido partido, int minimoJugadoresCancha) {
        this.partido = partido;
        this.minimoJugadoresCancha = minimoJugadoresCancha;
        this.cerrada = false;
    }

    public Partido getPartido() {
        return partido;
    }

    public int getMinimoJugadoresCancha() {
        return minimoJugadoresCancha;
    }

    public void setMinimoJugadoresCancha(int minimoJugadoresCancha) {
        this.minimoJugadoresCancha = minimoJugadoresCancha;
    }

    public boolean isCerrada() {
        return cerrada;
    }

    public void setCerrada(boolean cerrada) {
        this.cerrada = cerrada;
    }

    public List<Jugador> getAlineacionLocal() {
        return alineacionLocal;
    }

    public List<Jugador> getAlineacionVisitante() {
        return alineacionVisitante;
    }

    public List<Gol> getGoles() {
        return goles;
    }

    public List<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public void registrarGol(Jugador jugador, String nombreEquipo, int minuto) {
        goles.add(new Gol(jugador, nombreEquipo, minuto));
    }

    public void registrarTarjeta(Tarjeta tarjeta) {
        tarjetas.add(tarjeta);
        if (tarjeta.isExpulsion() && tarjeta.getPartidosSuspension() > 0) {
            int actual = tarjeta.getJugador().getPartidosSuspension();
            tarjeta.getJugador().setPartidosSuspension(actual + tarjeta.getPartidosSuspension());
        }
    }

    public int contarAmarillasJugador(String cedula) {
        int total = 0;
        for (Tarjeta t : tarjetas) {
            if (t.getJugador().getCedula().equals(cedula) && t.getTipo().equals("AMARILLA")) {
                total++;
            }
        }
        return total;
    }

    public int contarRojasJugador(String cedula) {
        int total = 0;
        for (Tarjeta t : tarjetas) {
            if (t.getJugador().getCedula().equals(cedula) && t.isExpulsion()) {
                total++;
            }
        }
        return total;
    }

    public int contarGolesJugador(String cedula) {
        int total = 0;
        for (Gol g : goles) {
            if (g.getJugador().getCedula().equals(cedula)) {
                total++;
            }
        }
        return total;
    }
}