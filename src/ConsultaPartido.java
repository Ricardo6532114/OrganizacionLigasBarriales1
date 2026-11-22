import java.util.ArrayList;
import java.util.List;

public class ConsultaPartido {

    private List<Partido> partidos;

    public ConsultaPartido(List<Partido> partidos) {
        this.partidos = partidos;
    }

    public List<Partido> consultarPorEquipo(String nombreEquipo) {
        List<Partido> resultado = new ArrayList<>();
        if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) return resultado;
        for (Partido p : partidos) {
            if (p.getLocal().equalsIgnoreCase(nombreEquipo.trim())
                    || p.getVisitante().equalsIgnoreCase(nombreEquipo.trim())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Partido> consultarPartidosJugados() {
        List<Partido> jugados = new ArrayList<>();
        for (Partido p : partidos) {
            if (p.isJugado()) {
                jugados.add(p);
            }
        }
        return jugados;
    }
}