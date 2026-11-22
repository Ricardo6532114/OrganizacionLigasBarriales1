import java.util.ArrayList;
import java.util.List;

public class Partido {

    private int numero;
    private String fase; // "Fase de Grupos", "Cuartos de Final", "Semifinal", "Final"
    private String local;
    private String visitante;
    private String cancha;
    private String fecha;
    private int hora; // Formato 24h (ej. 19)
    private String estado; // "Programado", "Reprogramado", "Finalizado", "Perdido: ..."
    private int golesLocal;
    private int golesVisitante;
    private boolean jugado;
    private ActaPartido acta;
    private List<String> jugadoresEnCancha = new ArrayList<>();

    public Partido(int numero, String fase, String local, String visitante, String cancha, String fecha, int hora) {
        this.numero = numero;
        this.fase = fase;
        this.local = local;
        this.visitante = visitante;
        this.cancha = cancha;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "Programado";
        this.jugado = false;
    }

    public int getNumero() {
        return numero;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public String getLocal() {
        return local;
    }

    public String getVisitante() {
        return visitante;
    }

    public String getCancha() {
        return cancha;
    }

    public void setCancha(String cancha) {
        this.cancha = cancha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public boolean isJugado() {
        return jugado;
    }

    public void setJugado(boolean jugado) {
        this.jugado = jugado;
    }

    public ActaPartido getActa() {
        return acta;
    }

    public void setActa(ActaPartido acta) {
        this.acta = acta;
    }

    public List<String> getJugadoresEnCancha() {
        return jugadoresEnCancha;
    }

    public void setJugadoresEnCancha(List<String> jugadoresEnCancha) {
        this.jugadoresEnCancha = jugadoresEnCancha;
    }

    public String getHoraFormato24() {
        return String.format("%02d:00", hora);
    }

    @Override
    public String toString() {
        String marcador = jugado ? " (" + golesLocal + " - " + golesVisitante + ")" : "";
        return "#" + numero + " [" + fase + " | " + estado + "] "
                + local + " vs " + visitante + marcador
                + " | " + cancha + " | " + fecha + " | " + getHoraFormato24();
    }
}