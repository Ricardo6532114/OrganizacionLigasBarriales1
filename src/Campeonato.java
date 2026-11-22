import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Campeonato {

    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFinEstimada;
    private List<Equipo> equipos = new ArrayList<>();
    private Calendario calendario = new Calendario();
    private TablaPosiciones tablaPosiciones = new TablaPosiciones();
    private FinanzasCampeonato finanzas = new FinanzasCampeonato();

    public Campeonato(String nombre, int diaInicio, int mesInicio, int anioInicio) {
        this.nombre = nombre;
        this.fechaInicio = LocalDate.of(anioInicio, mesInicio, diaInicio);
        this.fechaFinEstimada = this.fechaInicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(int dia, int mes, int anio) {
        this.fechaInicio = LocalDate.of(anio, mes, dia);
    }

    public LocalDate getFechaFinEstimada() {
        return fechaFinEstimada;
    }

    public void setFechaFinEstimada(LocalDate fechaFinEstimada) {
        this.fechaFinEstimada = fechaFinEstimada;
    }

    public String getFechaInicioFormateada() {
        return fechaInicio.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getFechaFinEstimadaFormateada() {
        return fechaFinEstimada.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public Calendario getCalendario() {
        return calendario;
    }

    public TablaPosiciones getTablaPosiciones() {
        return tablaPosiciones;
    }

    public FinanzasCampeonato getFinanzas() {
        return finanzas;
    }

    public void estimarYGenerarCalendario(List<String> diasSemana, List<Integer> horas24,
                                          int canchas, List<String> feriados, boolean jugarFeriados) {
        calendario.configurarParametros(fechaInicio, diasSemana, horas24, canchas, feriados, jugarFeriados);
        LocalDate finGrupos = calendario.generarCalendarioGrupos(fechaInicio, equipos);
        int totalEquipos = equipos.size();
        int diasExtraFasesFinales = 0;
        if (totalEquipos >= 8) {
            diasExtraFasesFinales += 7; // Cuartos
        }
        if (totalEquipos >= 4) {
            diasExtraFasesFinales += 7; // Semifinal
        }
        if (totalEquipos >= 2) {
            diasExtraFasesFinales += 7; // Final
        }
        this.fechaFinEstimada = finGrupos.plusDays(diasExtraFasesFinales);
        tablaPosiciones.inicializarEquipos(equipos);
    }
}