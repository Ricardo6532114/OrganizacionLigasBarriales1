import java.util.ArrayList;
import java.util.List;

public class TablaPosiciones {

    private List<FilaPosicion> filas = new ArrayList<>();

    public void inicializarEquipos(List<Equipo> equipos) {
        filas.clear();
        for (Equipo eq : equipos) {
            filas.add(new FilaPosicion(eq.getNombreEquipo()));
        }
    }

    public FilaPosicion obtenerFila(String nombreEquipo) {
        for (FilaPosicion f : filas) {
            if (f.getNombreEquipo().equalsIgnoreCase(nombreEquipo)) {
                return f;
            }
        }
        FilaPosicion nueva = new FilaPosicion(nombreEquipo);
        filas.add(nueva);
        return nueva;
    }

    public void actualizarDesdePartido(Partido p) {
        if (!p.isJugado()) return;
        FilaPosicion local = obtenerFila(p.getLocal());
        FilaPosicion visita = obtenerFila(p.getVisitante());

        if (p.getGolesLocal() > p.getGolesVisitante()) {
            local.registrarResultado(p.getGolesLocal(), p.getGolesVisitante(), "G");
            visita.registrarResultado(p.getGolesVisitante(), p.getGolesLocal(), "P");
        } else if (p.getGolesVisitante() > p.getGolesLocal()) {
            visita.registrarResultado(p.getGolesVisitante(), p.getGolesLocal(), "G");
            local.registrarResultado(p.getGolesLocal(), p.getGolesVisitante(), "P");
        } else {
            if (p.getEstado().contains("Ningún equipo") || p.getEstado().contains("Ambos pierden")) {
                local.registrarResultado(p.getGolesLocal(), p.getGolesVisitante(), "P");
                visita.registrarResultado(p.getGolesVisitante(), p.getGolesLocal(), "P");
            } else {
                local.registrarResultado(p.getGolesLocal(), p.getGolesVisitante(), "E");
                visita.registrarResultado(p.getGolesVisitante(), p.getGolesLocal(), "E");
            }
        }
    }

    public void recalcularTodo(List<Equipo> equipos, List<Partido> partidos) {
        inicializarEquipos(equipos);
        for (Partido p : partidos) {
            if ("Fase de Grupos".equals(p.getFase()) && p.isJugado()) {
                actualizarDesdePartido(p);
            }
        }
    }

    public List<FilaPosicion> getFilasOrdenadas() {
        List<FilaPosicion> copia = new ArrayList<>(filas);
        for (int i = 0; i < copia.size(); i++) {
            for (int j = i + 1; j < copia.size(); j++) {
                FilaPosicion a = copia.get(i);
                FilaPosicion b = copia.get(j);
                boolean debeCambiar = false;
                if (b.getPuntos() != a.getPuntos()) {
                    debeCambiar = b.getPuntos() > a.getPuntos();
                } else if (b.getDg() != a.getDg()) {
                    debeCambiar = b.getDg() > a.getDg();
                } else if (b.getGf() != a.getGf()) {
                    debeCambiar = b.getGf() > a.getGf();
                }
                if (debeCambiar) {
                    copia.set(i, b);
                    copia.set(j, a);
                }
            }
        }
        return copia;
    }
}