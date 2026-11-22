import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Calendario {

    private List<Partido> partidos = new ArrayList<>();
    private List<String> diasSemanaJuego = new ArrayList<>();
    private List<Integer> horasJuego24 = new ArrayList<>();
    private List<String> fechasFeriados = new ArrayList<>();
    private boolean jugarEnFeriados = false;
    private int cantidadCanchas = 1;
    private final int duracionPartidoMin = 90;
    private final int descansoMin = 15;

    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public List<Partido> getPartidos() {
        return partidos;
    }

    public int getDuracionTotalBloqueMin() {
        return duracionPartidoMin + descansoMin; // 105 minutos
    }

    public void configurarParametros(LocalDate fechaInicio, List<String> diasSemana,
                                     List<Integer> horas24, int canchas,
                                     List<String> feriados, boolean jugarFeriados) {
        this.diasSemanaJuego = new ArrayList<>(diasSemana);
        this.horasJuego24 = new ArrayList<>(horas24);
        this.cantidadCanchas = Math.max(1, canchas);
        this.fechasFeriados = new ArrayList<>(feriados);
        this.jugarEnFeriados = jugarFeriados;
    }

    private String nombreDiaEspanol(LocalDate fecha) {
        switch (fecha.getDayOfWeek()) {
            case MONDAY: return "Lunes";
            case TUESDAY: return "Martes";
            case WEDNESDAY: return "Miércoles";
            case THURSDAY: return "Jueves";
            case FRIDAY: return "Viernes";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }

    public boolean esDiaValido(LocalDate fecha) {
        String diaStr = nombreDiaEspanol(fecha);
        if (!diasSemanaJuego.contains(diaStr)) {
            return false;
        }
        String fechaTexto = fecha.format(formatoFecha);
        if (fechasFeriados.contains(fechaTexto) && !jugarEnFeriados) {
            return false;
        }
        return true;
    }

    public LocalDate generarCalendarioGrupos(LocalDate fechaInicio, List<Equipo> equipos) {
        partidos.clear();
        if (equipos.size() < 2) {
            return fechaInicio;
        }

        List<String[]> cruces = new ArrayList<>();
        for (int i = 0; i < equipos.size(); i++) {
            for (int j = i + 1; j < equipos.size(); j++) {
                cruces.add(new String[]{equipos.get(i).getNombreEquipo(), equipos.get(j).getNombreEquipo()});
            }
        }

        LocalDate fechaActual = fechaInicio;
        int indiceCruce = 0;
        int numeroPartido = 1;
        LocalDate ultimaFecha = fechaInicio;

        List<Integer> horasValidas = horasJuego24.isEmpty() ? List.of(19) : horasJuego24;

        while (indiceCruce < cruces.size()) {
            if (esDiaValido(fechaActual)) {
                String fechaStr = fechaActual.format(formatoFecha);
                List<String> equiposOcupadosEnTurno = new ArrayList<>();

                for (int hIdx = 0; hIdx < horasValidas.size() && indiceCruce < cruces.size(); hIdx++) {
                    int hora = horasValidas.get(hIdx);
                    equiposOcupadosEnTurno.clear();

                    for (int c = 1; c <= cantidadCanchas && indiceCruce < cruces.size(); c++) {
                        int buscado = -1;
                        for (int k = indiceCruce; k < cruces.size(); k++) {
                            String eqA = cruces.get(k)[0];
                            String eqB = cruces.get(k)[1];
                            if (!equiposOcupadosEnTurno.contains(eqA) && !equiposOcupadosEnTurno.contains(eqB)) {
                                buscado = k;
                                break;
                            }
                        }

                        if (buscado != -1) {
                            String[] temp = cruces.get(indiceCruce);
                            cruces.set(indiceCruce, cruces.get(buscado));
                            cruces.set(buscado, temp);

                            String[] par = cruces.get(indiceCruce);
                            Partido p = new Partido(numeroPartido++, "Fase de Grupos",
                                    par[0], par[1], "Cancha " + c, fechaStr, hora);
                            partidos.add(p);
                            equiposOcupadosEnTurno.add(par[0]);
                            equiposOcupadosEnTurno.add(par[1]);
                            ultimaFecha = fechaActual;
                            indiceCruce++;
                        }
                    }
                }
            }
            fechaActual = fechaActual.plusDays(1);
        }

        return ultimaFecha;
    }

    public boolean reprogramarManual(int numeroPartido, String nuevaFecha, int nuevaHora) {
        Partido objetivo = buscarPartidoPorNumero(numeroPartido);
        if (objetivo == null || nuevaHora < 0 || nuevaHora > 23) return false;

        for (Partido otro : partidos) {
            if (otro != objetivo
                    && otro.getCancha().equalsIgnoreCase(objetivo.getCancha())
                    && otro.getFecha().equals(nuevaFecha)
                    && otro.getHora() == nuevaHora) {
                return false;
            }
            if (otro != objetivo
                    && otro.getFecha().equals(nuevaFecha)
                    && otro.getHora() == nuevaHora
                    && (otro.getLocal().equals(objetivo.getLocal())
                    || otro.getVisitante().equals(objetivo.getLocal())
                    || otro.getLocal().equals(objetivo.getVisitante())
                    || otro.getVisitante().equals(objetivo.getVisitante()))) {
                return false;
            }
        }

        objetivo.setFecha(nuevaFecha);
        objetivo.setHora(nuevaHora);
        objetivo.setEstado("Reprogramado");
        return true;
    }

    public boolean reprogramarAutomatico(int numeroPartido, LocalDate fechaBase) {
        Partido objetivo = buscarPartidoPorNumero(numeroPartido);
        if (objetivo == null) return false;

        List<Integer> horasValidas = horasJuego24.isEmpty() ? List.of(19) : horasJuego24;
        LocalDate candidato = fechaBase;

        for (int dias = 0; dias < 365; dias++) {
            if (esDiaValido(candidato)) {
                String fechaStr = candidato.format(formatoFecha);
                for (int hora : horasValidas) {
                    boolean ocupado = false;
                    for (Partido otro : partidos) {
                        if (otro != objetivo && otro.getFecha().equals(fechaStr) && otro.getHora() == hora) {
                            boolean mismaCancha = otro.getCancha().equalsIgnoreCase(objetivo.getCancha());
                            boolean mismoEquipo = otro.getLocal().equals(objetivo.getLocal())
                                    || otro.getVisitante().equals(objetivo.getLocal())
                                    || otro.getLocal().equals(objetivo.getVisitante())
                                    || otro.getVisitante().equals(objetivo.getVisitante());
                            if (mismaCancha || mismoEquipo) {
                                ocupado = true;
                                break;
                            }
                        }
                    }
                    if (!ocupado) {
                        objetivo.setFecha(fechaStr);
                        objetivo.setHora(hora);
                        objetivo.setEstado("Reprogramado (Auto)");
                        return true;
                    }
                }
            }
            candidato = candidato.plusDays(1);
        }
        return false;
    }

    public void generarFaseFinal(String nombreFase, List<String> equiposClasificados, LocalDate fechaBase) {
        if (equiposClasificados.size() < 2) return;
        List<Integer> horasValidas = horasJuego24.isEmpty() ? List.of(19) : horasJuego24;
        LocalDate fechaActual = fechaBase;
        int numero = partidos.size() + 1;
        int idx = 0;

        while (idx + 1 < equiposClasificados.size()) {
            if (esDiaValido(fechaActual)) {
                String fechaStr = fechaActual.format(formatoFecha);
                int hora = horasValidas.get(idx % horasValidas.size());
                String cancha = "Cancha " + ((idx % cantidadCanchas) + 1);
                Partido p = new Partido(numero++, nombreFase,
                        equiposClasificados.get(idx), equiposClasificados.get(idx + 1),
                        cancha, fechaStr, hora);
                partidos.add(p);
                idx += 2;
            }
            fechaActual = fechaActual.plusDays(1);
        }
    }

    public Partido buscarPartidoPorNumero(int numero) {
        for (Partido p : partidos) {
            if (p.getNumero() == numero) return p;
        }
        return null;
    }
}