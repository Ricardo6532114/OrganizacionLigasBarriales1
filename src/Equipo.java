import java.util.ArrayList;
import java.util.List;

public class Equipo {

    private String nombreEquipo;
    private String categoria;
    private boolean inscripcionPagada;
    private Jugador capitan;
    private List<Jugador> jugadores = new ArrayList<>();

    public Equipo(String nombreEquipo, boolean inscripcionPagada) {
        this.nombreEquipo = nombreEquipo;
        this.inscripcionPagada = inscripcionPagada;
        this.categoria = "Sin asignar";
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isInscripcionPagada() {
        return inscripcionPagada;
    }

    public void setInscripcionPagada(boolean inscripcionPagada) {
        this.inscripcionPagada = inscripcionPagada;
    }

    public Jugador getCapitan() {
        return capitan;
    }

    public void setCapitan(Jugador capitan) {
        this.capitan = capitan;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public String toString() {
        String info = "EQUIPO: " + nombreEquipo
                + " | Categoría: " + categoria
                + " | Inscripción: " + (inscripcionPagada ? "Sí pagó" : "No pagó") + "\n";
        if (capitan != null) {
            info += "Capitán/Delegado: " + capitan.getNombre() + " " + capitan.getApellido()
                    + " (#" + capitan.getNumeroCamiseta() + ", Tel: " + capitan.getTelefono() + ")\n";
        } else {
            info += "Capitán/Delegado: Sin asignar\n";
        }
        info += "Plantilla (" + jugadores.size() + " jugadores):\n";
        if (jugadores.isEmpty()) {
            info += "  (Aún no tiene jugadores inscritos)\n";
        } else {
            for (Jugador j : jugadores) {
                info += "  - " + j.toString() + "\n";
            }
        }
        return info;
    }
}