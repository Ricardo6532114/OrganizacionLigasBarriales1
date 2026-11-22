import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SistemaCLI {

    private Scanner sc;
    private Campeonato campeonato;
    private List<Persona> personasRegistradas = new ArrayList<>();

    public SistemaCLI() {
        sc = new Scanner(System.in);
        campeonato = new Campeonato("Campeonato Barrial", 21, 11, 2026);
    }

    public int menu() {
        System.out.println("\n=== CAMPEONATO BARRIAL - MENU PRINCIPAL ===");
        System.out.println("1. Configurar campeonato y tarifas (Finanzas)");
        System.out.println("2. Registrar persona");
        System.out.println("3. Crear equipo (Inscripcion Si/No)");
        System.out.println("4. Asignar jugadores y capitan a equipo");
        System.out.println("5. Ver equipos y personas");
        System.out.println("6. Generar calendario y reprogramar partido");
        System.out.println("7. Registrar acta, goles y tarjetas");
        System.out.println("8. Ver tabla de posiciones y consultar partidos");
        System.out.println("9. Abrir interfaz grafica (Swing)");
        System.out.print("Seleccione una opcion: ");
        return sc.nextInt();
    }

    public void configurarCampeonatoYFinanzas() {
        System.out.print("Nombre del campeonato (sin espacios): ");
        String nombre = sc.next();

        System.out.print("Dia de inicio (1-31): ");
        int dia = sc.nextInt();
        System.out.print("Mes de inicio (1-12): ");
        int mes = sc.nextInt();
        System.out.print("Anio de inicio: ");
        int anio = sc.nextInt();

        System.out.print("Valor de inscripcion: ");
        double insc = sc.nextDouble();
        System.out.print("Multa tarjeta amarilla: ");
        double mAma = sc.nextDouble();
        System.out.print("Multa tarjeta roja: ");
        double mRoj = sc.nextDouble();
        System.out.print("Valor de vocalia: ");
        double voc = sc.nextDouble();

        if (insc < 0 || mAma < 0 || mRoj < 0 || voc < 0) {
            System.out.println("Error: No se permiten valores negativos.");
            return;
        }

        campeonato.setNombre(nombre);
        campeonato.setFechaInicio(dia, mes, anio);
        campeonato.getFinanzas().configurarTarifasIniciales(insc, mAma, mRoj, voc);
        System.out.println("Configuracion guardada. Fecha inicio: " + campeonato.getFechaInicioFormateada());
    }

    public void crearPersona() {
        System.out.println("\n--- Registrar Persona ---");
        System.out.print("Nombre (sin espacios): ");
        String nombre = sc.next();
        System.out.print("Apellido (sin espacios): ");
        String apellido = sc.next();
        System.out.print("Edad (14-60): ");
        int edad = sc.nextInt();
        System.out.print("Cedula (10 digitos): ");
        String cedula = sc.next();
        System.out.print("Telefono (10 digitos): ");
        String telefono = sc.next();

        Persona p = new Persona(nombre, apellido, edad, cedula, telefono);
        personasRegistradas.add(p);
        System.out.println("Persona registrada correctamente.");
    }

    public void crearEquipo() {
        System.out.println("\n--- Crear Equipo ---");
        System.out.print("Nombre del equipo (sin espacios): ");
        String nombreEquipo = sc.next();
        System.out.print("Pago inscripcion 1.Si / 2.No: ");
        int pagoOpc = sc.nextInt();

        if (pagoOpc != 1) {
            System.out.println("No se puede inscribir el equipo porque no ha pagado la inscripcion.");
            return;
        }

        Equipo eq = new Equipo(nombreEquipo, true);
        System.out.print("Categoria 1.Juvenil / 2.Senior / 3.Master: ");
        int cat = sc.nextInt();
        if (cat == 1) {
            eq.setCategoria("Juvenil (Sub-18)");
        } else if (cat == 2) {
            eq.setCategoria("Senior");
        } else {
            eq.setCategoria("Máster (+40)");
        }

        campeonato.getEquipos().add(eq);
        campeonato.getFinanzas().registrarPago(nombreEquipo, "Inscripcion",
                campeonato.getFinanzas().getValorInscripcion());
        System.out.println("Equipo creado con exito.");
    }

    public void asignarJugadoresAEquipo() {
        if (campeonato.getEquipos().isEmpty() || personasRegistradas.isEmpty()) {
            System.out.println("Debe haber al menos un equipo y una persona registrada.");
            return;
        }

        for (int i = 0; i < campeonato.getEquipos().size(); i++) {
            System.out.println(i + ". " + campeonato.getEquipos().get(i).getNombreEquipo());
        }
        System.out.print("Seleccione indice del equipo: ");
        int idxEq = sc.nextInt();
        if (idxEq < 0 || idxEq >= campeonato.getEquipos().size()) return;
        Equipo eq = campeonato.getEquipos().get(idxEq);

        for (int i = 0; i < personasRegistradas.size(); i++) {
            System.out.println(i + ". " + personasRegistradas.get(i));
        }
        System.out.print("Seleccione indice de la persona: ");
        int idxPer = sc.nextInt();
        if (idxPer < 0 || idxPer >= personasRegistradas.size()) return;
        Persona per = personasRegistradas.get(idxPer);

        System.out.print("Numero de camiseta (0-30): ");
        int dorsal = sc.nextInt();
        if (dorsal < 0 || dorsal > 30) {
            System.out.println("Numero de camiseta fuera de rango (0 a 30).");
            return;
        }

        System.out.print("Es capitan 1.Si / 2.No: ");
        boolean cap = sc.nextInt() == 1;

        Jugador j = new Jugador(per.getNombre(), per.getApellido(), per.getEdad(),
                per.getCedula(), per.getTelefono(), cap, dorsal);
        eq.getJugadores().add(j);
        if (cap || eq.getCapitan() == null) {
            eq.setCapitan(j);
        }
        System.out.println("Jugador agregado al equipo.");
    }

    public void mostrarEquiposYPersonas() {
        System.out.println("\n--- Personas ---");
        for (Persona p : personasRegistradas) {
            System.out.println(p);
        }
        System.out.println("\n--- Equipos ---");
        for (Equipo e : campeonato.getEquipos()) {
            System.out.println(e);
        }
    }

    public void gestionarCalendarioYReprogramacion() {
        System.out.println("\n1. Generar calendario");
        System.out.println("2. Ver partidos");
        System.out.println("3. Reprogramar partido");
        System.out.print("Opcion: ");
        int sub = sc.nextInt();

        if (sub == 1) {
            List<String> dias = new ArrayList<>();
            dias.add("Viernes");
            dias.add("Sábado");
            dias.add("Domingo");

            List<Integer> horas = new ArrayList<>();
            System.out.print("Hora del primer partido (formato 24h, ej. 19): ");
            horas.add(sc.nextInt());

            System.out.print("Cantidad de canchas: ");
            int canchas = sc.nextInt();

            System.out.print("Jugar en feriados 1.Si / 2.No: ");
            boolean feriados = sc.nextInt() == 1;

            campeonato.estimarYGenerarCalendario(dias, horas, canchas, new ArrayList<>(), feriados);
            System.out.println("Calendario generado. Fecha fin estimada: "
                    + campeonato.getFechaFinEstimadaFormateada());
        } else if (sub == 2) {
            for (Partido p : campeonato.getCalendario().getPartidos()) {
                System.out.println(p);
            }
        } else if (sub == 3) {
            System.out.print("Numero de partido: ");
            int num = sc.nextInt();
            System.out.print("Tipo reprogramacion 1.Manual / 2.Automatica: ");
            int tipo = sc.nextInt();
            if (tipo == 1) {
                System.out.print("Nueva fecha (dd-MM-yyyy): ");
                String fecha = sc.next();
                System.out.print("Nueva hora (0-23): ");
                int hora = sc.nextInt();
                boolean ok = campeonato.getCalendario().reprogramarManual(num, fecha, hora);
                System.out.println(ok ? "Reprogramado." : "No disponible / cruce.");
            } else {
                boolean ok = campeonato.getCalendario().reprogramarAutomatico(num, LocalDate.now());
                System.out.println(ok ? "Reprogramado automaticamente." : "Sin cupo.");
            }
        }
    }

    public void gestionarActaYTarjetas() {
        List<Partido> partidos = campeonato.getCalendario().getPartidos();
        if (partidos.isEmpty()) {
            System.out.println("No hay partidos generados.");
            return;
        }
        for (Partido p : partidos) {
            System.out.println(p);
        }
        System.out.print("Ingrese numero de partido: ");
        int num = sc.nextInt();
        Partido p = campeonato.getCalendario().buscarPartidoPorNumero(num);
        if (p == null) return;

        if (p.getActa() == null) {
            System.out.print("Minimo de jugadores en cancha: ");
            int minJug = sc.nextInt();
            p.setActa(new ActaPartido(p, minJug));
        }

        System.out.println("1. Registrar gol  |  2. Registrar tarjeta  |  3. Cerrar acta");
        int op = sc.nextInt();
        if (op == 1) {
            System.out.print("Equipo que anota (1.Local / 2.Visitante): ");
            String eqNom = sc.nextInt() == 1 ? p.getLocal() : p.getVisitante();
            System.out.print("Minuto del gol (entero): ");
            int min = sc.nextInt();
            Jugador autor = new Jugador("Jugador", "Gol", 20, "0000000000", "0999999999", false, 9);
            p.getActa().registrarGol(autor, eqNom, min);
            if (eqNom.equals(p.getLocal())) p.setGolesLocal(p.getGolesLocal() + 1);
            else p.setGolesVisitante(p.getGolesVisitante() + 1);
            System.out.println("Gol registrado.");
        } else if (op == 2) {
            System.out.print("Equipo sancionado (1.Local / 2.Visitante): ");
            String eqNom = sc.nextInt() == 1 ? p.getLocal() : p.getVisitante();
            System.out.print("Tipo 1.Amarilla / 2.Roja directa: ");
            boolean roja = sc.nextInt() == 2;
            System.out.print("Minuto (entero): ");
            int min = sc.nextInt();
            System.out.print("Motivo (sin espacios): ");
            String motivo = sc.next();
            int susp = roja ? 1 : 0;
            if (roja) {
                System.out.print("Partidos de suspension: ");
                susp = sc.nextInt();
            }
            Jugador sanc = new Jugador("Jugador", "Tarjeta", 20, "1111111111", "0999999999", false, 4);
            Tarjeta t = new Tarjeta(sanc, eqNom, roja ? "ROJA_DIRECTA" : "AMARILLA", motivo, min, susp, roja);
            p.getActa().registrarTarjeta(t);
            System.out.println("Tarjeta registrada.");
        } else if (op == 3) {
            p.setJugado(true);
            p.setEstado("Finalizado");
            p.getActa().setCerrada(true);
            campeonato.getTablaPosiciones().recalcularTodo(campeonato.getEquipos(), partidos);
            System.out.println("Acta cerrada y tabla actualizada.");
        }
    }

    public void verPosicionesYConsultar() {
        System.out.println("\n--- TABLA DE POSICIONES ---");
        System.out.println("Equipo\t\tPJ\tPG\tPE\tPP\tGF\tGC\tDG\tPTS");
        for (FilaPosicion f : campeonato.getTablaPosiciones().getFilasOrdenadas()) {
            System.out.printf("%s\t\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d%n",
                    f.getNombreEquipo(), f.getPj(), f.getPg(), f.getPe(), f.getPp(),
                    f.getGf(), f.getGc(), f.getDg(), f.getPuntos());
        }

        System.out.print("\nConsultar partidos por nombre de equipo: ");
        String eq = sc.next();
        ConsultaPartido consulta = new ConsultaPartido(campeonato.getCalendario().getPartidos());
        for (Partido p : consulta.consultarPorEquipo(eq)) {
            System.out.println(p);
        }
    }

    public void abrirInterfazGrafica() {
        javax.swing.SwingUtilities.invokeLater(() -> new InterfazCampeonatoApp().setVisible(true));
    }
}