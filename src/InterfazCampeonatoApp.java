import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class InterfazCampeonatoApp extends JFrame {

    private int minJugadoresInicio = 6;
    private static final int MIN_JUEGO = 90;
    private static final int BLOQUE_TOTAL_MIN = 125;

    private static final String[] CATEGORIAS_OFICIALES = {"Sub-18", "Senior", "Máster"};

    private static final Color BG_GENERAL = new Color(243, 247, 255);
    private static final Color AZUL_HEADER = new Color(29, 78, 216);
    private static final Color CARD_AZUL = new Color(235, 244, 255);
    private static final Color CARD_VERDE = new Color(232, 250, 240);
    private static final Color CARD_AMBAR = new Color(255, 250, 232);
    private static final Color CARD_LILA = new Color(245, 239, 255);
    private static final Color BOTON_VERDE = new Color(22, 163, 74);
    private static final Color BOTON_AZUL = new Color(37, 99, 235);
    private static final Color BOTON_NARANJA = new Color(234, 88, 12);
    private static final Color BOTON_ROJO = new Color(220, 38, 38);
    private static final Color BOTON_GRIS = new Color(100, 116, 139);

    private static final Color VERDE_PTS_HEADER = new Color(163, 230, 53);
    private static final Color VERDE_PTS_COL = new Color(190, 242, 100);

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final Campeonato campeonato;
    private final List<Persona> personasRegistradas = new ArrayList<>();
    private final Map<String, int[]> faltasPendientesPorEquipo = new HashMap<>();
    private final Set<Integer> actasCobroAplicadas = new HashSet<>();
    private final Set<String> plantillasConfirmadas = new HashSet<>();
    private final Map<Integer, String> categoriaPorPartido = new HashMap<>();
    private final Map<Integer, String> localPorPartido = new HashMap<>();
    private final Map<Integer, String> visitantePorPartido = new HashMap<>();
    private final Map<Integer, String> horaExactaPorPartido = new HashMap<>();

    // Vocalía automática por partido y control por ronda
    private final Map<Integer, String> vocalAsignadoPorPartido = new HashMap<>();
    private final Set<String> vocalesUsadosEnRonda = new HashSet<>();

    private boolean tarifasConfiguradas = false;
    private boolean campeonatoGuardadoFijo = false;

    // 1. Campeonato + Tabla de posiciones
    private JTextField txtNombreCampeonato;
    private JTextField txtFechaInicioEscrita;
    private JButton btnGuardarCamp;
    private JLabel lblFechasCampeonato;
    private DefaultTableModel modeloPosiciones;

    // 2. Finanzas
    private JTextField txtValorInscripcion, txtMultaAmarilla, txtMultaRoja, txtValorVocalia;
    private JButton btnGuardarTarifas;
    private JLabel lblResumenTarifas;
    private JComboBox<String> comboEquipoCobro;
    private JLabel lblFaltasAcumuladasEquipo;
    private JLabel lblTotalSiguientePartido;
    private JTextArea areaHistorialFinanzas;

    // 3. Personas
    private JLabel lblTotalPersonas;
    private JTextField txtPerNombre, txtPerApellido, txtPerEdad, txtPerCedula, txtPerTelefono;
    private JTextField txtBuscarPersonaLista;
    private DefaultTableModel modeloPersonas;
    private JTable tablaPersonas;

    // 4. Equipos
    private JTextField txtEqNombre;
    private JComboBox<String> comboEqPago;
    private JComboBox<String> comboEqCategoria;
    private JButton btnInscribirEq;
    private JButton btnEditarEq;
    private DefaultTableModel modeloEquipos;
    private JTable tablaEquipos;

    // 5. Plantilla
    private JComboBox<String> comboEqPlantilla;
    private JLabel lblRangoEdadCategoria;
    private JTextField txtBuscarJugador;
    private JComboBox<String> comboPersonasFiltradas;
    private JTextField txtDorsalJugador;
    private JButton btnGuardarEnPlantilla;
    private DefaultListModel<String> modeloPlantilla;
    private JList<String> listaPlantilla;

    // 6. Calendario
    private final List<String> ordenDias = Arrays.asList(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
    private final Map<String, JCheckBox> checksDias = new HashMap<>();
    private final Map<String, JPanel> panelesHorasDia = new HashMap<>();
    private final Map<String, JLabel> etiquetasFechaDia = new HashMap<>();
    private final Map<String, JTextField> camposHoraDesdeDia = new HashMap<>();
    private final Map<String, JTextField> camposHoraHastaDia = new HashMap<>();
    private JPanel contenedorConfigDias;
    private JSpinner spCantidadCanchas;
    private DefaultTableModel modeloPartidos;
    private JComboBox<Integer> comboPartidoReprog;
    private JRadioButton rbReprogAuto, rbReprogManual;
    private JPanel panelCamposManual;
    private JLabel lblReprogFechaSeleccionada;
    private JTextField txtReprogHoraManual;
    private JComboBox<String> comboFaseFinal;

    // 7. Acta y Consulta
    private JComboBox<Integer> comboPartidoActa;
    private JLabel lblActaFechaHora;
    private JLabel lblActaVocalAuto;
    private JLabel lblEstadoActaBloqueo;
    private JTextField txtMinJugadoresActa;
    private JButton btnAplicarMinJugadores;
    private JButton btnAplicarAsist;
    private JButton btnCerrarActa;
    private JButton btnInicioReanudar;
    private JButton btnAlistar;
    private JLabel lblRelojPartido;
    private javax.swing.Timer relojTimer;
    private int segundosTranscurridos = 0;

    private JComboBox<String> comboAsistencia;
    private DefaultListModel<String> modeloHabilitados;
    private JList<String> listaHabilitados;
    private DefaultListModel<String> modeloEnCancha;
    private JList<String> listaEnCancha;
    private JLabel lblConteoCancha;

    private JLabel lblTituloEquipoLocal;
    private JComboBox<String> comboJugadorLocalPlanilla;
    private JSpinner spAmarillaLocal;
    private JSpinner spRojaLocal;
    private JSpinner spMinutoLocal;
    private JButton btnGolLocal;
    private JButton btnAnotarLocal;
    private JTable tablaPlanillaLocal;
    private DefaultTableModel modeloPlanillaLocal;

    private JLabel lblTituloEquipoVisita;
    private JComboBox<String> comboJugadorVisitaPlanilla;
    private JSpinner spAmarillaVisita;
    private JSpinner spRojaVisita;
    private JSpinner spMinutoVisita;
    private JButton btnGolVisita;
    private JButton btnAnotarVisita;
    private JTable tablaPlanillaVisita;
    private DefaultTableModel modeloPlanillaVisita;

    private JTextArea areaResumenActa;
    private JComboBox<String> comboConsultaEquipo;
    private JPanel contenedorTarjetasConsulta;

    public InterfazCampeonatoApp() {
        campeonato = new Campeonato("Copa Barrial", 21, 11, 2026);
        setTitle("Sistema de Campeonato Barrial");
        setSize(1280, 820);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarReloj();
        construirVentana();
        refrescarTodo();
    }

    private void inicializarReloj() {
        relojTimer = new javax.swing.Timer(1000, e -> {
            segundosTranscurridos++;
            int minActual = segundosTranscurridos / 60;
            int segActual = segundosTranscurridos % 60;
            lblRelojPartido.setText(String.format("Cronómetro: %02d:%02d (Min %d/90)",
                    minActual, segActual, Math.min(90, minActual)));

            if (minActual >= 90) {
                relojTimer.stop();
                JOptionPane.showMessageDialog(this,
                        "Se cumplieron los 90 minutos reglamentarios.",
                        "Fin de Tiempo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void construirVentana() {
        JPanel contenedorPrincipal = new JPanel(new BorderLayout(10, 10));
        contenedorPrincipal.setBackground(BG_GENERAL);
        contenedorPrincipal.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel banner = crearTarjetaRedondeada(AZUL_HEADER, new Color(59, 130, 246), 18);
        banner.setLayout(new BorderLayout(10, 5));
        banner.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel lblTitulo = new JLabel("⚽ CAMPEONATO BARRIAL — PANEL INTERACTIVO");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        lblFechasCampeonato = new JLabel("Inicio: 21-11-2026 | Fin estimado: Pendiente");
        lblFechasCampeonato.setForeground(new Color(219, 234, 254));
        lblFechasCampeonato.setFont(new Font("SansSerif", Font.BOLD, 12));

        banner.add(lblTitulo, BorderLayout.WEST);
        banner.add(lblFechasCampeonato, BorderLayout.EAST);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font("SansSerif", Font.BOLD, 12));
        pestanas.addTab("1. Campeonato", crearTabCampeonato());
        pestanas.addTab("2. Finanzas", crearTabFinanzas());
        pestanas.addTab("3. Personas", crearTabPersonas());
        pestanas.addTab("4. Equipos", crearTabEquipos());
        pestanas.addTab("5. Plantilla", crearTabPlantilla());
        pestanas.addTab("6. Calendario", crearTabCalendario());
        pestanas.addTab("7. Acta y Consultas", crearTabActaConsultas());

        contenedorPrincipal.add(banner, BorderLayout.NORTH);
        contenedorPrincipal.add(pestanas, BorderLayout.CENTER);
        setContentPane(contenedorPrincipal);
    }

    // =========================================================================
    // CALENDARIITO VISUAL (MUESTRA EL DÍA EXACTO Y PERMITE SELECCIONARLO)
    // =========================================================================
    private LocalDate abrirSelectorCalendario(LocalDate fechaInicial) {
        final LocalDate[] seleccionada = {null};
        LocalDate base = fechaInicial != null ? fechaInicial : campeonato.getFechaInicio();
        final YearMonth[] mesActual = {YearMonth.from(base)};

        JDialog dialog = new JDialog(this, "Seleccionar Fecha del Calendario", true);
        dialog.setSize(410, 340);
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerMes = new JPanel(new BorderLayout());
        headerMes.setOpaque(false);

        JButton btnPrev = crearBotonRedondeado("< Mes", BOTON_AZUL);
        JButton btnNext = crearBotonRedondeado("Mes >", BOTON_AZUL);
        JLabel lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblMesAnio.setForeground(AZUL_HEADER);

        headerMes.add(btnPrev, BorderLayout.WEST);
        headerMes.add(lblMesAnio, BorderLayout.CENTER);
        headerMes.add(btnNext, BorderLayout.EAST);

        JPanel gridDias = new JPanel(new GridLayout(7, 7, 4, 4));
        gridDias.setOpaque(false);

        Runnable renderMes = () -> {
            gridDias.removeAll();
            YearMonth ym = mesActual[0];
            String nombreMes = ym.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new Locale("es", "EC"));
            lblMesAnio.setText(nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1) + " " + ym.getYear());

            String[] cabeceras = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
            for (String cab : cabeceras) {
                JLabel l = new JLabel(cab, SwingConstants.CENTER);
                l.setFont(new Font("SansSerif", Font.BOLD, 11));
                l.setForeground(new Color(71, 85, 105));
                gridDias.add(l);
            }

            LocalDate primerDia = ym.atDay(1);
            int offset = primerDia.getDayOfWeek().getValue() - 1; // Lunes = 0
            for (int i = 0; i < offset; i++) {
                gridDias.add(new JLabel(""));
            }

            int diasMes = ym.lengthOfMonth();
            for (int d = 1; d <= diasMes; d++) {
                LocalDate fechaDia = ym.atDay(d);
                String nomDiaCorto = nombreDiaEspanol(fechaDia.getDayOfWeek());
                JButton bDia = new JButton(String.valueOf(d));
                bDia.setFont(new Font("SansSerif", Font.BOLD, 12));
                bDia.setToolTipText(nomDiaCorto + " " + fechaDia.format(FMT_FECHA));
                bDia.setMargin(new Insets(2, 2, 2, 2));

                if (fechaDia.equals(base)) {
                    bDia.setBackground(BOTON_VERDE);
                    bDia.setForeground(Color.WHITE);
                } else {
                    bDia.setBackground(CARD_AZUL);
                    bDia.setForeground(new Color(15, 23, 42));
                }

                bDia.addActionListener(ev -> {
                    seleccionada[0] = fechaDia;
                    dialog.dispose();
                });
                gridDias.add(bDia);
            }

            int totalCeldasUsadas = 7 + offset + diasMes;
            while (totalCeldasUsadas < 49) {
                gridDias.add(new JLabel(""));
                totalCeldasUsadas++;
            }

            gridDias.revalidate();
            gridDias.repaint();
        };

        btnPrev.addActionListener(ev -> {
            mesActual[0] = mesActual[0].minusMonths(1);
            renderMes.run();
        });
        btnNext.addActionListener(ev -> {
            mesActual[0] = mesActual[0].plusMonths(1);
            renderMes.run();
        });

        renderMes.run();

        JLabel lblAyuda = new JLabel("Haz clic en un día para seleccionar (muestra día exacto al pasar el cursor).",
                SwingConstants.CENTER);
        lblAyuda.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblAyuda.setForeground(new Color(100, 116, 139));

        root.add(headerMes, BorderLayout.NORTH);
        root.add(gridDias, BorderLayout.CENTER);
        root.add(lblAyuda, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
        return seleccionada[0];
    }

    private String nombreDiaEspanol(DayOfWeek dow) {
        switch (dow) {
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

    private LocalDate parsearFechaSeguro(String texto, LocalDate respaldo) {
        if (texto == null) return respaldo;
        String limpio = texto.replaceAll("^[A-Za-zÁÉÍÓÚáéíóú]+\\s+", "").trim();
        try {
            return LocalDate.parse(limpio, FMT_FECHA);
        } catch (DateTimeParseException ex) {
            return respaldo;
        }
    }

    // =========================================================================
    // REGLAS DE EDAD POR CATEGORÍA (SOLO SUB-18, SENIOR Y MÁSTER)
    // =========================================================================
    private String normalizarCategoria(String categoria) {
        if (categoria == null) return "Sub-18";
        String c = categoria.trim().toLowerCase();
        if (c.contains("sub-18") || c.contains("sub 18") || c.contains("sub-juvenil") || c.contains("sub juvenil")) {
            return "Sub-18";
        }
        if (c.contains("senior")) {
            return "Senior";
        }
        if (c.contains("máster") || c.contains("master") || c.contains("40")) {
            return "Máster";
        }
        return categoria.trim();
    }

    private int[] obtenerRangoEdadCategoria(String categoria) {
        String cat = normalizarCategoria(categoria);
        if ("Sub-18".equalsIgnoreCase(cat)) {
            return new int[]{14, 17};
        }
        if ("Senior".equalsIgnoreCase(cat)) {
            return new int[]{18, 39};
        }
        if ("Máster".equalsIgnoreCase(cat)) {
            return new int[]{40, 60};
        }
        return new int[]{14, 60};
    }

    private boolean edadValidaParaCategoria(int edad, String categoria) {
        int[] rango = obtenerRangoEdadCategoria(categoria);
        return edad >= rango[0] && edad <= rango[1];
    }

    private String descripcionRangoCategoria(String categoria) {
        int[] rango = obtenerRangoEdadCategoria(categoria);
        return rango[0] + " a " + rango[1] + " años";
    }

    // =========================================================================
    // 1. CAMPEONATO (BLOQUEADO AL GUARDAR) + TABLA DE POSICIONES
    // =========================================================================
    private JPanel crearTabCampeonato() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(BG_GENERAL);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardCamp = crearTarjetaRedondeada(CARD_AZUL, new Color(147, 197, 253), 16);
        cardCamp.setLayout(new GridLayout(3, 2, 10, 10));
        cardCamp.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(147, 197, 253), 14), "Datos Generales del Campeonato"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        txtNombreCampeonato = campoRedondeado(campeonato.getNombre());
        txtFechaInicioEscrita = campoRedondeado(campeonato.getFechaInicioFormateada());

        btnGuardarCamp = crearBotonRedondeado("Guardar Campeonato", BOTON_AZUL);
        btnGuardarCamp.addActionListener(e -> {
            if (campeonatoGuardadoFijo) {
                aviso("El nombre y la fecha del campeonato ya fueron guardados y no se pueden modificar.");
                return;
            }
            String nom = txtNombreCampeonato.getText().trim();
            String fechaTexto = txtFechaInicioEscrita.getText().trim();
            try {
                LocalDate fecha = LocalDate.parse(fechaTexto, FMT_FECHA);
                campeonato.setNombre(nom.isEmpty() ? "Copa Barrial" : nom);
                campeonato.setFechaInicio(fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
                campeonatoGuardadoFijo = true;
                bloquearCamposCampeonato();
                refrescarTodo();
                JOptionPane.showMessageDialog(this, "Campeonato guardado y fijado: " + campeonato.getNombre()
                        + " (" + campeonato.getFechaInicioFormateada() + ")");
            } catch (DateTimeParseException ex) {
                aviso("Escriba la fecha de inicio con formato dd-MM-yyyy (ejemplo: 21-11-2026).");
            }
        });

        cardCamp.add(new JLabel("Nombre del Campeonato:"));
        cardCamp.add(txtNombreCampeonato);
        cardCamp.add(new JLabel("Fecha de Inicio (dd-MM-yyyy):"));
        cardCamp.add(txtFechaInicioEscrita);
        cardCamp.add(new JLabel(""));
        cardCamp.add(btnGuardarCamp);

        JPanel cardPosiciones = crearTarjetaRedondeada(Color.WHITE, new Color(132, 204, 22), 16);
        cardPosiciones.setLayout(new BorderLayout(8, 8));
        cardPosiciones.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(132, 204, 22), 14), "TABLA DE POSICIONES"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        modeloPosiciones = new DefaultTableModel(
                new String[]{"Pos.", "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC", "Dif."}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable tablaPos = new JTable(modeloPosiciones);
        tablaPos.setRowHeight(28);
        tablaPos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaPos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tablaPos.getTableHeader().setBackground(VERDE_PTS_HEADER);
        tablaPos.getTableHeader().setForeground(new Color(20, 40, 10));

        DefaultTableCellRenderer centrarRenderer = new DefaultTableCellRenderer();
        centrarRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer ptsRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("SansSerif", Font.BOLD, 14));
                setBackground(isSelected ? new Color(132, 204, 22) : VERDE_PTS_COL);
                setForeground(new Color(15, 23, 42));
                return this;
            }
        };

        tablaPos.getColumnModel().getColumn(0).setMaxWidth(55);
        tablaPos.getColumnModel().getColumn(0).setCellRenderer(centrarRenderer);
        tablaPos.getColumnModel().getColumn(2).setMaxWidth(65);
        tablaPos.getColumnModel().getColumn(2).setCellRenderer(ptsRenderer);
        for (int col = 3; col < tablaPos.getColumnCount(); col++) {
            tablaPos.getColumnModel().getColumn(col).setMaxWidth(60);
            tablaPos.getColumnModel().getColumn(col).setCellRenderer(centrarRenderer);
        }

        cardPosiciones.add(new JScrollPane(tablaPos), BorderLayout.CENTER);

        panel.add(cardCamp, BorderLayout.NORTH);
        panel.add(cardPosiciones, BorderLayout.CENTER);
        return panel;
    }

    private void bloquearCamposCampeonato() {
        txtNombreCampeonato.setEditable(false);
        txtFechaInicioEscrita.setEditable(false);
        txtNombreCampeonato.setEnabled(false);
        txtFechaInicioEscrita.setEnabled(false);
        btnGuardarCamp.setEnabled(false);
    }

    // =========================================================================
    // 2. FINANZAS
    // =========================================================================
    private JPanel crearTabFinanzas() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setBackground(BG_GENERAL);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardTarifas = crearTarjetaRedondeada(CARD_AMBAR, new Color(253, 224, 71), 16);
        cardTarifas.setLayout(new BorderLayout(8, 8));
        cardTarifas.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(253, 224, 71), 14), "Tarifas Iniciales del Campeonato"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel formTarifas = new JPanel(new GridLayout(4, 2, 8, 8));
        formTarifas.setOpaque(false);

        txtValorInscripcion = campoRedondeado("40.0");
        txtMultaAmarilla = campoRedondeado("2.5");
        txtMultaRoja = campoRedondeado("6.0");
        txtValorVocalia = campoRedondeado("5.0");

        formTarifas.add(new JLabel("Valor de Inscripción ($):"));
        formTarifas.add(txtValorInscripcion);
        formTarifas.add(new JLabel("Multa Tarjeta Amarilla ($):"));
        formTarifas.add(txtMultaAmarilla);
        formTarifas.add(new JLabel("Multa Tarjeta Roja ($):"));
        formTarifas.add(txtMultaRoja);
        formTarifas.add(new JLabel("Valor Vocalía por Partido ($):"));
        formTarifas.add(txtValorVocalia);

        btnGuardarTarifas = crearBotonRedondeado("Establecer Tarifa Inicial", BOTON_AZUL);
        lblResumenTarifas = new JLabel("Estado: Obligatorio establecer tarifa inicial antes de inscribir equipos.");
        lblResumenTarifas.setFont(new Font("SansSerif", Font.BOLD, 12));

        btnGuardarTarifas.addActionListener(e -> {
            if (tarifasConfiguradas) {
                aviso("Las tarifas iniciales ya fueron establecidas y quedan fijas.");
                return;
            }
            try {
                double insc = Double.parseDouble(txtValorInscripcion.getText().trim());
                double mAma = Double.parseDouble(txtMultaAmarilla.getText().trim());
                double mRoj = Double.parseDouble(txtMultaRoja.getText().trim());
                double voc = Double.parseDouble(txtValorVocalia.getText().trim());
                if (insc < 0 || mAma < 0 || mRoj < 0 || voc < 0) {
                    aviso("No se permiten valores negativos.");
                    return;
                }
                campeonato.getFinanzas().configurarTarifasIniciales(insc, mAma, mRoj, voc);
                tarifasConfiguradas = true;
                bloquearCamposTarifas();
                refrescarTodo();
                JOptionPane.showMessageDialog(this,
                        "Tarifas iniciales establecidas. Ya puede inscribir equipos.");
            } catch (NumberFormatException ex) {
                aviso("Ingrese valores numéricos válidos.");
            }
        });

        JPanel pieTarifas = new JPanel(new GridLayout(2, 1, 6, 6));
        pieTarifas.setOpaque(false);
        pieTarifas.add(lblResumenTarifas);
        pieTarifas.add(btnGuardarTarifas);

        cardTarifas.add(formTarifas, BorderLayout.CENTER);
        cardTarifas.add(pieTarifas, BorderLayout.SOUTH);

        JPanel cardCobro = crearTarjetaRedondeada(CARD_VERDE, new Color(134, 239, 172), 16);
        cardCobro.setLayout(new BorderLayout(8, 8));
        cardCobro.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(134, 239, 172), 14),
                        "Cobro Siguiente Partido (Automático al Cerrar Partido)"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel formCobro = new JPanel(new GridLayout(3, 2, 6, 6));
        formCobro.setOpaque(false);

        comboEquipoCobro = new JComboBox<>();
        comboEquipoCobro.addActionListener(e -> actualizarResumenCobroEquipo(false));

        lblFaltasAcumuladasEquipo = new JLabel("Amarillas acumuladas: 0 | Rojas acumuladas: 0");
        lblFaltasAcumuladasEquipo.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton btnCalcularCobro = crearBotonRedondeado("Calcular Cobro Siguiente Partido", BOTON_VERDE);

        formCobro.add(new JLabel("Equipo:"));
        formCobro.add(comboEquipoCobro);
        formCobro.add(new JLabel("Faltas acumuladas:"));
        formCobro.add(lblFaltasAcumuladasEquipo);
        formCobro.add(new JLabel(""));
        formCobro.add(btnCalcularCobro);

        lblTotalSiguientePartido = new JLabel("Vocalía + Multas siguiente partido: $0.00");
        lblTotalSiguientePartido.setFont(new Font("SansSerif", Font.BOLD, 12));

        areaHistorialFinanzas = new JTextArea();
        areaHistorialFinanzas.setEditable(false);
        areaHistorialFinanzas.setFont(new Font("Monospaced", Font.PLAIN, 11));

        btnCalcularCobro.addActionListener(e -> {
            if (!tarifasConfiguradas) {
                aviso("Primero establezca las tarifas iniciales del campeonato.");
                return;
            }
            String eq = (String) comboEquipoCobro.getSelectedItem();
            if (eq == null) {
                aviso("Seleccione un equipo.");
                return;
            }
            actualizarResumenCobroEquipo(true);
        });

        JPanel centroCobro = new JPanel(new BorderLayout(5, 5));
        centroCobro.setOpaque(false);
        centroCobro.add(lblTotalSiguientePartido, BorderLayout.NORTH);
        centroCobro.add(new JScrollPane(areaHistorialFinanzas), BorderLayout.CENTER);

        cardCobro.add(formCobro, BorderLayout.NORTH);
        cardCobro.add(centroCobro, BorderLayout.CENTER);

        panel.add(cardTarifas);
        panel.add(cardCobro);
        return panel;
    }

    private void bloquearCamposTarifas() {
        txtValorInscripcion.setEditable(false);
        txtMultaAmarilla.setEditable(false);
        txtMultaRoja.setEditable(false);
        txtValorVocalia.setEditable(false);
        txtValorInscripcion.setEnabled(false);
        txtMultaAmarilla.setEnabled(false);
        txtMultaRoja.setEnabled(false);
        txtValorVocalia.setEnabled(false);
        btnGuardarTarifas.setEnabled(false);
    }

    private void actualizarResumenCobroEquipo(boolean mostrarAlerta) {
        String eq = (String) comboEquipoCobro.getSelectedItem();
        if (eq == null || lblFaltasAcumuladasEquipo == null || lblTotalSiguientePartido == null) return;

        int[] faltas = faltasPendientesPorEquipo.getOrDefault(eq, new int[]{0, 0});
        int ama = faltas[0];
        int roj = faltas[1];
        lblFaltasAcumuladasEquipo.setText("Amarillas acumuladas: " + ama + " | Rojas acumuladas: " + roj);

        double vocalia = campeonato.getFinanzas().getValorVocalia();
        double multas = campeonato.getFinanzas().calcularMultaPartido(ama, roj);
        double total = vocalia + multas;

        String detalle = String.format(
                "%s -> Vocalía ($%.2f) + Multas (%dA, %dR = $%.2f) = $%.2f",
                eq, vocalia, ama, roj, multas, total);
        lblTotalSiguientePartido.setText(detalle);

        if (mostrarAlerta) {
            JOptionPane.showMessageDialog(this, detalle,
                    "Cálculo Siguiente Partido", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void aplicarCobroAlCerrarPartido(Partido p) {
        if (p == null || actasCobroAplicadas.contains(p.getNumero())) return;
        actasCobroAplicadas.add(p.getNumero());

        String loc = obtenerLocalPartido(p);
        String vis = obtenerVisitantePartido(p);
        String[] equiposPartido = {loc, vis};
        for (String eqNom : equiposPartido) {
            int[] faltas = faltasPendientesPorEquipo.computeIfAbsent(eqNom, k -> new int[]{0, 0});
            int ama = faltas[0];
            int roj = faltas[1];
            double vocalia = campeonato.getFinanzas().getValorVocalia();
            double multas = campeonato.getFinanzas().calcularMultaPartido(ama, roj);
            double totalSiguiente = vocalia + multas;

            String movimiento = String.format(
                    "Cierre Partido #%d | %s debe pagar en siguiente partido: $%.2f (Vocalía $%.2f + %d Amarillas + %d Rojas = Multas $%.2f)",
                    p.getNumero(), eqNom, totalSiguiente, vocalia, ama, roj, multas);
            campeonato.getFinanzas().registrarPago(eqNom, movimiento, totalSiguiente);
        }
    }

    // =========================================================================
    // 3. PERSONAS
    // =========================================================================
    private JPanel crearTabPersonas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_GENERAL);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardForm = crearTarjetaRedondeada(CARD_AZUL, new Color(147, 197, 253), 16);
        cardForm.setLayout(new BorderLayout(8, 8));
        cardForm.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(147, 197, 253), 14), "Registro de Personas"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JPanel barraSuperior = new JPanel(new BorderLayout(8, 4));
        barraSuperior.setOpaque(false);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelBusqueda.setOpaque(false);
        txtBuscarPersonaLista = campoRedondeado("");
        txtBuscarPersonaLista.setPreferredSize(new Dimension(240, 28));
        txtBuscarPersonaLista.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refrescarTablaPersonas(); }
            public void removeUpdate(DocumentEvent e) { refrescarTablaPersonas(); }
            public void changedUpdate(DocumentEvent e) { refrescarTablaPersonas(); }
        });
        panelBusqueda.add(new JLabel("Buscar (nombre o cédula):"));
        panelBusqueda.add(txtBuscarPersonaLista);

        lblTotalPersonas = new JLabel("Personas inscritas: 0");
        lblTotalPersonas.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotalPersonas.setForeground(AZUL_HEADER);

        barraSuperior.add(panelBusqueda, BorderLayout.WEST);
        barraSuperior.add(lblTotalPersonas, BorderLayout.EAST);

        JPanel campos = new JPanel(new GridLayout(3, 4, 8, 8));
        campos.setOpaque(false);

        txtPerNombre = campoRedondeado("");
        txtPerApellido = campoRedondeado("");
        txtPerEdad = campoRedondeado("");
        txtPerCedula = campoRedondeado("");
        txtPerTelefono = campoRedondeado("");
        JButton btnGuardarPersona = crearBotonRedondeado("Registrar Persona", BOTON_VERDE);
        JButton btnEditarPersona = crearBotonRedondeado("Editar Persona", BOTON_NARANJA);

        campos.add(new JLabel("Nombre:"));
        campos.add(txtPerNombre);
        campos.add(new JLabel("Apellido:"));
        campos.add(txtPerApellido);
        campos.add(new JLabel("Edad (14 - 60):"));
        campos.add(txtPerEdad);
        campos.add(new JLabel("Cédula (10 dígitos):"));
        campos.add(txtPerCedula);
        campos.add(new JLabel("Teléfono (10 dígitos):"));
        campos.add(txtPerTelefono);
        campos.add(btnGuardarPersona);
        campos.add(btnEditarPersona);

        btnGuardarPersona.addActionListener(e -> {
            String n = txtPerNombre.getText().trim();
            String a = txtPerApellido.getText().trim();
            String edadStr = txtPerEdad.getText().trim();
            String c = txtPerCedula.getText().trim();
            String t = txtPerTelefono.getText().trim();

            if (!esSoloLetras(n) || !esSoloLetras(a)) {
                aviso("Nombre y apellido solo deben contener letras.");
                return;
            }
            int ed;
            try {
                ed = Integer.parseInt(edadStr);
            } catch (NumberFormatException ex) {
                aviso("Escriba una edad numérica válida entre 14 y 60.");
                return;
            }
            if (ed < 14 || ed > 60) {
                aviso("La edad debe estar entre 14 y 60 años.");
                return;
            }
            if (!esDiezDigitos(c) || !esDiezDigitos(t)) {
                aviso("Cédula y teléfono deben tener exactamente 10 dígitos.");
                return;
            }
            for (Persona p : personasRegistradas) {
                if (p.getCedula().equals(c)) {
                    aviso("Ya existe una persona registrada con esa cédula.");
                    return;
                }
            }

            personasRegistradas.add(new Persona(n, a, ed, c, t));
            txtPerNombre.setText("");
            txtPerApellido.setText("");
            txtPerEdad.setText("");
            txtPerCedula.setText("");
            txtPerTelefono.setText("");
            refrescarTodo();
        });

        btnEditarPersona.addActionListener(e -> {
            int fila = tablaPersonas.getSelectedRow();
            if (fila < 0) {
                aviso("Primero seleccione la persona que desea editar.");
                return;
            }
            String cedulaSel = String.valueOf(modeloPersonas.getValueAt(fila, 4));
            Persona p = buscarPersonaPorCedula(cedulaSel);
            if (p == null) return;
            abrirDialogoEditarPersona(p);
        });

        cardForm.add(barraSuperior, BorderLayout.NORTH);
        cardForm.add(campos, BorderLayout.CENTER);

        modeloPersonas = new DefaultTableModel(
                new String[]{"#", "Nombre", "Apellido", "Edad", "Cédula", "Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaPersonas = new JTable(modeloPersonas);
        tablaPersonas.setRowHeight(24);

        panel.add(cardForm, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPersonas), BorderLayout.CENTER);
        return panel;
    }

    private void abrirDialogoEditarPersona(Persona p) {
        JDialog dialog = new JDialog(this, "Editar Persona", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);

        JPanel cont = new JPanel(new BorderLayout(10, 10));
        cont.setBackground(CARD_AZUL);
        cont.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setOpaque(false);
        JTextField fNom = campoRedondeado(p.getNombre());
        JTextField fApe = campoRedondeado(p.getApellido());
        JTextField fEdad = campoRedondeado(String.valueOf(p.getEdad()));
        JTextField fCed = campoRedondeado(p.getCedula());
        JTextField fTel = campoRedondeado(p.getTelefono());

        form.add(new JLabel("Nombre:"));
        form.add(fNom);
        form.add(new JLabel("Apellido:"));
        form.add(fApe);
        form.add(new JLabel("Edad (14-60):"));
        form.add(fEdad);
        form.add(new JLabel("Cédula (10 dígitos):"));
        form.add(fCed);
        form.add(new JLabel("Teléfono (10 dígitos):"));
        form.add(fTel);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnGuardarSalir = crearBotonRedondeado("Guardar y Salir", BOTON_VERDE);
        JButton btnCancelar = crearBotonRedondeado("Cancelar", BOTON_GRIS);

        btnGuardarSalir.addActionListener(ev -> {
            String n = fNom.getText().trim();
            String a = fApe.getText().trim();
            String c = fCed.getText().trim();
            String t = fTel.getText().trim();
            int ed;
            try {
                ed = Integer.parseInt(fEdad.getText().trim());
            } catch (NumberFormatException ex) {
                aviso("Edad inválida.");
                return;
            }
            if (!esSoloLetras(n) || !esSoloLetras(a) || ed < 14 || ed > 60
                    || !esDiezDigitos(c) || !esDiezDigitos(t)) {
                aviso("Revise los datos: nombre/apellido con letras, edad 14-60, cédula y teléfono de 10 dígitos.");
                return;
            }
            p.setNombre(n);
            p.setApellido(a);
            p.setEdad(ed);
            p.setCedula(c);
            p.setTelefono(t);
            dialog.dispose();
            refrescarTodo();
        });

        btnCancelar.addActionListener(ev -> dialog.dispose());
        botones.add(btnGuardarSalir);
        botones.add(btnCancelar);

        cont.add(form, BorderLayout.CENTER);
        cont.add(botones, BorderLayout.SOUTH);
        dialog.setContentPane(cont);
        dialog.setVisible(true);
    }

    // =========================================================================
    // 4. EQUIPOS (REQUIERE TARIFA INICIAL ESTABLECIDA; SOLO SUB-18, SENIOR, MÁSTER)
    // =========================================================================
    private JPanel crearTabEquipos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_GENERAL);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel cardEq = crearTarjetaRedondeada(CARD_AMBAR, new Color(253, 224, 71), 16);
        cardEq.setLayout(new GridLayout(3, 3, 8, 8));
        cardEq.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(253, 224, 71), 14), "Inscripción de Equipos"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        txtEqNombre = campoRedondeado("");
        comboEqPago = new JComboBox<>(new String[]{"Seleccione pago...", "No pago", "Si pago"});
        comboEqCategoria = new JComboBox<>(new String[]{
                "Seleccione categoría...", "Sub-18", "Senior", "Máster"
        });
        btnInscribirEq = crearBotonRedondeado("Inscribir Equipo", BOTON_VERDE);
        btnEditarEq = crearBotonRedondeado("Editar Equipo", BOTON_NARANJA);

        cardEq.add(new JLabel("Nombre del Equipo:"));
        cardEq.add(new JLabel("¿Pagó Inscripción? (Obligatorio):"));
        cardEq.add(new JLabel("Categoría (Obligatoria):"));
        cardEq.add(txtEqNombre);
        cardEq.add(comboEqPago);
        cardEq.add(comboEqCategoria);
        cardEq.add(new JLabel(""));
        cardEq.add(btnInscribirEq);
        cardEq.add(btnEditarEq);

        btnInscribirEq.addActionListener(e -> {
            if (!tarifasConfiguradas) {
                aviso("Primero debe establecer la tarifa inicial en Finanzas antes de inscribir cualquier equipo.");
                return;
            }
            String nombreEq = txtEqNombre.getText().trim();
            String pago = (String) comboEqPago.getSelectedItem();
            String categoriaSel = (String) comboEqCategoria.getSelectedItem();

            if (nombreEq.isEmpty()) {
                aviso("Ingrese el nombre del equipo.");
                return;
            }
            if ("No pago".equals(pago)) {
                aviso("No pago inscripción, no puede inscribir a su equipo.");
                return;
            }
            if (!"Si pago".equals(pago)) {
                aviso("Debe seleccionar 'Si pago' para inscribir al equipo.");
                return;
            }
            if (categoriaSel == null || categoriaSel.startsWith("Seleccione")) {
                aviso("Debe elegir obligatoriamente una categoría (Sub-18, Senior o Máster).");
                return;
            }
            String categoria = normalizarCategoria(categoriaSel);

            for (Equipo eq : campeonato.getEquipos()) {
                if (eq.getNombreEquipo().equalsIgnoreCase(nombreEq)) {
                    aviso("Ya existe un equipo con ese nombre.");
                    return;
                }
            }

            Equipo nuevo = new Equipo(nombreEq, true);
            nuevo.setCategoria(categoria);
            campeonato.getEquipos().add(nuevo);
            faltasPendientesPorEquipo.putIfAbsent(nombreEq, new int[]{0, 0});
            campeonato.getFinanzas().registrarPago(nombreEq, "Inscripción",
                    campeonato.getFinanzas().getValorInscripcion());
            txtEqNombre.setText("");
            comboEqPago.setSelectedIndex(0);
            comboEqCategoria.setSelectedIndex(0);
            refrescarTodo();
        });

        btnEditarEq.addActionListener(e -> {
            if (!tarifasConfiguradas) {
                aviso("Primero debe establecer la tarifa inicial en Finanzas.");
                return;
            }
            int fila = tablaEquipos.getSelectedRow();
            if (fila < 0 || fila >= campeonato.getEquipos().size()) {
                aviso("Seleccione un equipo de la tabla y pulse 'Editar Equipo'.");
                return;
            }
            Equipo eq = campeonato.getEquipos().get(fila);
            abrirMenuEditarEquipo(eq);
        });

        modeloEquipos = new DefaultTableModel(
                new String[]{"Equipo", "Categoría", "Inscripción", "Capitán", "Jugadores", "Plantilla"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaEquipos = new JTable(modeloEquipos);
        tablaEquipos.setRowHeight(24);

        panel.add(cardEq, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaEquipos), BorderLayout.CENTER);
        return panel;
    }

    private void actualizarEstadoInscripcionEquipos() {
        if (btnInscribirEq == null) return;
        txtEqNombre.setEnabled(tarifasConfiguradas);
        comboEqPago.setEnabled(tarifasConfiguradas);
        comboEqCategoria.setEnabled(tarifasConfiguradas);
        btnInscribirEq.setEnabled(tarifasConfiguradas);
        btnEditarEq.setEnabled(tarifasConfiguradas);
    }

    private void abrirMenuEditarEquipo(Equipo eq) {
        JDialog dialog = new JDialog(this, "Editar Equipo — " + eq.getNombreEquipo(), true);
        dialog.setSize(430, 220);
        dialog.setLocationRelativeTo(this);

        JPanel cont = new JPanel(new BorderLayout(10, 10));
        cont.setBackground(CARD_AMBAR);
        cont.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 10));
        form.setOpaque(false);

        JTextField fNombre = campoRedondeado(eq.getNombreEquipo());
        JComboBox<String> fCat = new JComboBox<>(CATEGORIAS_OFICIALES);
        fCat.setSelectedItem(normalizarCategoria(eq.getCategoria()));

        form.add(new JLabel("Nombre del Equipo:"));
        form.add(fNombre);
        form.add(new JLabel("Categoría:"));
        form.add(fCat);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnGuardar = crearBotonRedondeado("Guardar Cambios", BOTON_VERDE);
        JButton btnCancelar = crearBotonRedondeado("Cancelar", BOTON_GRIS);

        btnGuardar.addActionListener(ev -> {
            String nuevoNom = fNombre.getText().trim();
            String nuevaCat = normalizarCategoria((String) fCat.getSelectedItem());
            if (nuevoNom.isEmpty()) {
                aviso("El nombre del equipo no puede estar vacío.");
                return;
            }
            for (Equipo otro : campeonato.getEquipos()) {
                if (otro != eq && otro.getNombreEquipo().equalsIgnoreCase(nuevoNom)) {
                    aviso("Ya existe otro equipo con ese nombre.");
                    return;
                }
            }

            for (Jugador j : eq.getJugadores()) {
                if (!edadValidaParaCategoria(j.getEdad(), nuevaCat)) {
                    aviso("No se puede cambiar a categoría " + nuevaCat
                            + " porque el jugador " + j.getNombre() + " " + j.getApellido()
                            + " tiene " + j.getEdad() + " años (se requiere "
                            + descripcionRangoCategoria(nuevaCat) + ").");
                    return;
                }
            }

            String antNombre = eq.getNombreEquipo();
            eq.setNombreEquipo(nuevoNom);
            eq.setCategoria(nuevaCat);

            if (!antNombre.equals(nuevoNom)) {
                if (faltasPendientesPorEquipo.containsKey(antNombre)) {
                    faltasPendientesPorEquipo.put(nuevoNom, faltasPendientesPorEquipo.remove(antNombre));
                }
                if (plantillasConfirmadas.remove(antNombre)) {
                    plantillasConfirmadas.add(nuevoNom);
                }
            }

            dialog.dispose();
            refrescarTodo();
        });

        btnCancelar.addActionListener(ev -> dialog.dispose());
        botones.add(btnGuardar);
        botones.add(btnCancelar);

        cont.add(form, BorderLayout.CENTER);
        cont.add(botones, BorderLayout.SOUTH);
        dialog.setContentPane(cont);
        dialog.setVisible(true);
    }

    // =========================================================================
    // 5. PLANTILLA
    // =========================================================================
    private JPanel crearTabPlantilla() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setBackground(BG_GENERAL);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel izq = crearTarjetaRedondeada(CARD_LILA, new Color(196, 181, 253), 16);
        izq.setLayout(new GridLayout(7, 2, 8, 8));
        izq.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(196, 181, 253), 14), "Buscar Persona y Agregar a Plantilla"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        comboEqPlantilla = new JComboBox<>();
        lblRangoEdadCategoria = new JLabel("Edad permitida: —");
        lblRangoEdadCategoria.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRangoEdadCategoria.setForeground(AZUL_HEADER);

        txtBuscarJugador = campoRedondeado("");
        comboPersonasFiltradas = new JComboBox<>();
        txtDorsalJugador = campoRedondeado("");
        btnGuardarEnPlantilla = crearBotonRedondeado("Agregar a Plantilla", BOTON_AZUL);

        izq.add(new JLabel("Equipo:"));
        izq.add(comboEqPlantilla);
        izq.add(new JLabel("Requisito de edad:"));
        izq.add(lblRangoEdadCategoria);
        izq.add(new JLabel("Buscar nombre o cédula:"));
        izq.add(txtBuscarJugador);
        izq.add(new JLabel("Coincidencias (edad válida):"));
        izq.add(comboPersonasFiltradas);
        izq.add(new JLabel("N° Camiseta (escribir 0-30):"));
        izq.add(txtDorsalJugador);
        izq.add(new JLabel(""));
        izq.add(btnGuardarEnPlantilla);

        txtBuscarJugador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarComboPersonas(); }
            public void removeUpdate(DocumentEvent e) { filtrarComboPersonas(); }
            public void changedUpdate(DocumentEvent e) { filtrarComboPersonas(); }
        });
        comboEqPlantilla.addActionListener(e -> {
            actualizarEtiquetaEdadCategoria();
            filtrarComboPersonas();
            refrescarListaPlantilla();
            actualizarEstadoBloqueoPlantilla();
        });

        btnGuardarEnPlantilla.addActionListener(e -> {
            Equipo eq = obtenerEquipoPlantilla();
            if (eq == null) {
                aviso("Seleccione un equipo.");
                return;
            }
            if (plantillasConfirmadas.contains(eq.getNombreEquipo())) {
                aviso("La inscripción de plantilla ya fue confirmada y no se pueden agregar más jugadores.");
                return;
            }
            Persona per = obtenerPersonaFiltrada();
            if (per == null) {
                aviso("Seleccione una persona válida para la categoría " + normalizarCategoria(eq.getCategoria())
                        + " (" + descripcionRangoCategoria(eq.getCategoria()) + ").");
                return;
            }

            if (!edadValidaParaCategoria(per.getEdad(), eq.getCategoria())) {
                aviso("No se puede inscribir a " + per.getNombre() + " " + per.getApellido()
                        + " (" + per.getEdad() + " años) en la categoría " + normalizarCategoria(eq.getCategoria())
                        + ". Solo se permiten edades de " + descripcionRangoCategoria(eq.getCategoria()) + ".");
                return;
            }

            for (Equipo otroEq : campeonato.getEquipos()) {
                for (Jugador j : otroEq.getJugadores()) {
                    if (j.getCedula().equals(per.getCedula())) {
                        aviso("Este jugador ya está inscrito en el equipo '" + otroEq.getNombreEquipo()
                                + "'. Solo puede pertenecer a un equipo.");
                        return;
                    }
                }
            }

            int dorsal;
            try {
                dorsal = Integer.parseInt(txtDorsalJugador.getText().trim());
            } catch (NumberFormatException ex) {
                aviso("Escriba un número de camiseta válido entre 0 y 30.");
                return;
            }
            if (dorsal < 0 || dorsal > 30) {
                aviso("El número de camiseta debe estar entre 0 y 30.");
                return;
            }

            for (Jugador j : eq.getJugadores()) {
                if (j.getNumeroCamiseta() == dorsal) {
                    aviso("El número de camiseta #" + dorsal + " ya está asignado en este equipo.");
                    return;
                }
            }

            Jugador nuevo = new Jugador(per.getNombre(), per.getApellido(), per.getEdad(),
                    per.getCedula(), per.getTelefono(), eq.getJugadores().isEmpty(), dorsal);
            if (eq.getCapitan() == null) {
                eq.setCapitan(nuevo);
            }
            eq.getJugadores().add(nuevo);
            txtBuscarJugador.setText("");
            txtDorsalJugador.setText("");
            refrescarTodo();
        });

        JPanel der = crearTarjetaRedondeada(CARD_AZUL, new Color(147, 197, 253), 16);
        der.setLayout(new BorderLayout(8, 8));
        der.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(147, 197, 253), 14), "Plantilla del Equipo"),
                new EmptyBorder(8, 10, 8, 10)
        ));

        modeloPlantilla = new DefaultListModel<>();
        listaPlantilla = new JList<>(modeloPlantilla);

        JPanel filaSuperiorBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaSuperiorBotones.setOpaque(false);
        JButton btnEdicionPlantilla = crearBotonRedondeado("Edición de plantilla", BOTON_NARANJA);
        JButton btnEliminarJugador = crearBotonRedondeado("Eliminar Jugador", BOTON_ROJO);

        JPanel filaInferiorBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaInferiorBoton.setOpaque(false);
        JButton btnConfirmarPlantilla = crearBotonRedondeado("Confirmar la inscripción de plantilla", BOTON_VERDE);

        btnEdicionPlantilla.addActionListener(e -> {
            Equipo eq = obtenerEquipoPlantilla();
            if (eq == null) {
                aviso("Seleccione un equipo.");
                return;
            }
            abrirDialogoEdicionPlantilla(eq);
        });

        btnEliminarJugador.addActionListener(e -> {
            Equipo eq = obtenerEquipoPlantilla();
            if (eq == null) {
                aviso("Seleccione un equipo.");
                return;
            }
            if (plantillasConfirmadas.contains(eq.getNombreEquipo())) {
                aviso("La inscripción de plantilla ya fue confirmada y no se puede modificar.");
                return;
            }
            int idx = listaPlantilla.getSelectedIndex();
            if (idx >= 0 && idx < eq.getJugadores().size()) {
                Jugador eliminado = eq.getJugadores().remove(idx);
                if (eq.getCapitan() == eliminado) {
                    eq.setCapitan(eq.getJugadores().isEmpty() ? null : eq.getJugadores().get(0));
                    if (eq.getCapitan() != null) eq.getCapitan().setEsCapitan(true);
                }
                refrescarTodo();
            } else {
                aviso("Seleccione un jugador de la plantilla para eliminar.");
            }
        });

        btnConfirmarPlantilla.addActionListener(e -> {
            Equipo eq = obtenerEquipoPlantilla();
            if (eq == null) {
                aviso("Seleccione un equipo.");
                return;
            }
            if (eq.getJugadores().isEmpty()) {
                aviso("Agregue al menos un jugador a la plantilla antes de confirmar.");
                return;
            }
            plantillasConfirmadas.add(eq.getNombreEquipo());
            actualizarEstadoBloqueoPlantilla();
            refrescarTodo();
            JOptionPane.showMessageDialog(this,
                    "Inscripción de plantilla confirmada para el equipo " + eq.getNombreEquipo()
                            + ". No se pueden agregar más jugadores.");
        });

        filaSuperiorBotones.add(btnEdicionPlantilla);
        filaSuperiorBotones.add(btnEliminarJugador);
        filaInferiorBoton.add(btnConfirmarPlantilla);

        JPanel pieBotones = new JPanel(new GridLayout(2, 1, 2, 2));
        pieBotones.setOpaque(false);
        pieBotones.add(filaSuperiorBotones);
        pieBotones.add(filaInferiorBoton);

        der.add(new JScrollPane(listaPlantilla), BorderLayout.CENTER);
        der.add(pieBotones, BorderLayout.SOUTH);

        panel.add(izq);
        panel.add(der);
        return panel;
    }

    private void actualizarEtiquetaEdadCategoria() {
        if (lblRangoEdadCategoria == null) return;
        Equipo eq = obtenerEquipoPlantilla();
        if (eq == null) {
            lblRangoEdadCategoria.setText("Edad permitida: —");
        } else {
            String cat = normalizarCategoria(eq.getCategoria());
            lblRangoEdadCategoria.setText(cat + " -> " + descripcionRangoCategoria(cat));
        }
    }

    private void actualizarEstadoBloqueoPlantilla() {
        if (btnGuardarEnPlantilla == null) return;
        Equipo eq = obtenerEquipoPlantilla();
        boolean confirmada = eq != null && plantillasConfirmadas.contains(eq.getNombreEquipo());
        btnGuardarEnPlantilla.setEnabled(!confirmada);
        txtBuscarJugador.setEnabled(!confirmada);
        comboPersonasFiltradas.setEnabled(!confirmada);
        txtDorsalJugador.setEnabled(!confirmada);
    }

    private void abrirDialogoEdicionPlantilla(Equipo eq) {
        JDialog dialog = new JDialog(this, "Edición de plantilla — " + eq.getNombreEquipo(), true);
        dialog.setSize(580, 400);
        dialog.setLocationRelativeTo(this);

        JPanel cont = new JPanel(new BorderLayout(10, 10));
        cont.setBackground(CARD_AZUL);
        cont.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel superior = new JPanel(new GridLayout(1, 2, 8, 8));
        superior.setOpaque(false);

        JComboBox<String> fCapitan = new JComboBox<>();
        for (Jugador j : eq.getJugadores()) {
            fCapitan.addItem("#" + j.getNumeroCamiseta() + " - " + j.getNombre() + " " + j.getApellido());
        }
        if (eq.getCapitan() != null) {
            for (int i = 0; i < eq.getJugadores().size(); i++) {
                if (eq.getJugadores().get(i).getCedula().equals(eq.getCapitan().getCedula())) {
                    fCapitan.setSelectedIndex(i);
                    break;
                }
            }
        }

        superior.add(new JLabel("Seleccionar Capitán:"));
        superior.add(fCapitan);

        DefaultTableModel modeloEdJug = new DefaultTableModel(
                new String[]{"Jugador", "N° Camiseta Actual", "Nuevo N° Camiseta"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2;
            }
        };
        for (Jugador j : eq.getJugadores()) {
            modeloEdJug.addRow(new Object[]{
                    j.getNombre() + " " + j.getApellido(),
                    j.getNumeroCamiseta(),
                    j.getNumeroCamiseta()
            });
        }
        JTable tablaEdJug = new JTable(modeloEdJug);
        tablaEdJug.setRowHeight(24);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnGuardarSalir = crearBotonRedondeado("Guardar Plantilla", BOTON_VERDE);
        JButton btnCancelar = crearBotonRedondeado("Cancelar", BOTON_GRIS);

        btnGuardarSalir.addActionListener(ev -> {
            if (tablaEdJug.isEditing()) {
                tablaEdJug.getCellEditor().stopCellEditing();
            }

            List<Integer> nuevosDorsales = new ArrayList<>();
            for (int i = 0; i < modeloEdJug.getRowCount(); i++) {
                int dorsalNuevo;
                try {
                    dorsalNuevo = Integer.parseInt(String.valueOf(modeloEdJug.getValueAt(i, 2)).trim());
                } catch (NumberFormatException ex) {
                    aviso("Número de camiseta inválido en la fila " + (i + 1));
                    return;
                }
                if (dorsalNuevo < 0 || dorsalNuevo > 30) {
                    aviso("El número de camiseta debe estar entre 0 y 30.");
                    return;
                }
                if (nuevosDorsales.contains(dorsalNuevo)) {
                    aviso("No puede haber dos jugadores con la camiseta #" + dorsalNuevo + " en el mismo equipo.");
                    return;
                }
                nuevosDorsales.add(dorsalNuevo);
            }

            for (int i = 0; i < eq.getJugadores().size(); i++) {
                eq.getJugadores().get(i).setNumeroCamiseta(nuevosDorsales.get(i));
            }

            int idxCap = fCapitan.getSelectedIndex();
            for (Jugador j : eq.getJugadores()) j.setEsCapitan(false);
            if (idxCap >= 0 && idxCap < eq.getJugadores().size()) {
                Jugador cap = eq.getJugadores().get(idxCap);
                cap.setEsCapitan(true);
                eq.setCapitan(cap);
            } else {
                eq.setCapitan(null);
            }

            dialog.dispose();
            refrescarTodo();
        });

        btnCancelar.addActionListener(ev -> dialog.dispose());
        botones.add(btnGuardarSalir);
        botones.add(btnCancelar);

        cont.add(superior, BorderLayout.NORTH);
        cont.add(new JScrollPane(tablaEdJug), BorderLayout.CENTER);
        cont.add(botones, BorderLayout.SOUTH);
        dialog.setContentPane(cont);
        dialog.setVisible(true);
    }

    // =========================================================================
    // 6. CALENDARIO (CON CALENDARIITO VISUAL Y BLOQUE 125 MIN)
    // =========================================================================
    private static class VentanaDiaConfig {
        final String dia;
        final LocalDate fechaBase;
        final LocalTime horaInicio;
        final LocalTime horaFin;

        VentanaDiaConfig(String dia, LocalDate fechaBase, LocalTime horaInicio, LocalTime horaFin) {
            this.dia = dia;
            this.fechaBase = fechaBase;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }
    }

    private JPanel crearTabCalendario() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_GENERAL);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel cardParam = crearTarjetaRedondeada(CARD_AZUL, new Color(147, 197, 253), 16);
        cardParam.setLayout(new BorderLayout(6, 6));
        cardParam.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(147, 197, 253), 14),
                        "Días de Juego (125 min de inicio a inicio) y Canchas"),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JPanel filaChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaChecks.setOpaque(false);

        contenedorConfigDias = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        contenedorConfigDias.setOpaque(false);

        for (String dia : ordenDias) {
            JCheckBox chk = new JCheckBox(dia, false);
            chk.setOpaque(false);
            checksDias.put(dia, chk);
            filaChecks.add(chk);

            LocalDate fIni = campeonato.getFechaInicio();
            JLabel lblF = new JLabel(nombreDiaEspanol(fIni.getDayOfWeek()) + " " + fIni.format(FMT_FECHA));
            lblF.setFont(new Font("SansSerif", Font.BOLD, 11));

            JButton btnCalDia = crearBotonRedondeado("📅", BOTON_AZUL);
            btnCalDia.setMargin(new Insets(2, 6, 2, 6));

            JTextField tfDesde = campoRedondeado("19");
            tfDesde.setPreferredSize(new Dimension(32, 23));
            JTextField tfHasta = campoRedondeado("22");
            tfHasta.setPreferredSize(new Dimension(32, 23));

            etiquetasFechaDia.put(dia, lblF);
            camposHoraDesdeDia.put(dia, tfDesde);
            camposHoraHastaDia.put(dia, tfHasta);

            btnCalDia.addActionListener(e -> {
                LocalDate actual = parsearFechaSeguro(lblF.getText(), campeonato.getFechaInicio());
                LocalDate elegida = abrirSelectorCalendario(actual);
                if (elegida != null) {
                    lblF.setText(nombreDiaEspanol(elegida.getDayOfWeek()) + " " + elegida.format(FMT_FECHA));
                }
            });

            JPanel miniConfig = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 2));
            miniConfig.setBackground(Color.WHITE);
            miniConfig.setBorder(bordeSuave(new Color(148, 163, 184), 8));
            miniConfig.add(btnCalDia);
            miniConfig.add(lblF);
            miniConfig.add(new JLabel("H.Ini:"));
            miniConfig.add(tfDesde);
            miniConfig.add(new JLabel("H.Fin:"));
            miniConfig.add(tfHasta);
            miniConfig.setVisible(false);
            panelesHorasDia.put(dia, miniConfig);
            contenedorConfigDias.add(miniConfig);

            chk.addActionListener(e -> {
                miniConfig.setVisible(chk.isSelected());
                contenedorConfigDias.revalidate();
                contenedorConfigDias.repaint();
            });
        }

        JPanel filaInferiorParam = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filaInferiorParam.setOpaque(false);
        spCantidadCanchas = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JButton btnGenerarCal = crearBotonRedondeado("Generar Calendario", BOTON_VERDE);

        filaInferiorParam.add(new JLabel("Cantidad de canchas:"));
        filaInferiorParam.add(spCantidadCanchas);
        filaInferiorParam.add(btnGenerarCal);

        btnGenerarCal.addActionListener(e -> {
            if (!tarifasConfiguradas) {
                aviso("Configure primero las tarifas iniciales en Finanzas.");
                return;
            }
            if (campeonato.getEquipos().size() < 2) {
                aviso("Se necesitan al menos 2 equipos inscritos.");
                return;
            }

            List<VentanaDiaConfig> ventanas = new ArrayList<>();
            for (String dia : ordenDias) {
                if (checksDias.get(dia).isSelected()) {
                    LocalDate fechaBase = parsearFechaSeguro(
                            etiquetasFechaDia.get(dia).getText(), campeonato.getFechaInicio());
                    int dHora;
                    int hHora;
                    try {
                        dHora = Integer.parseInt(camposHoraDesdeDia.get(dia).getText().trim());
                        hHora = Integer.parseInt(camposHoraHastaDia.get(dia).getText().trim());
                    } catch (NumberFormatException ex) {
                        aviso("Escriba horas numéricas (0-23) para " + dia);
                        return;
                    }
                    if (dHora < 0 || dHora > 23 || hHora < 1 || hHora > 24 || dHora >= hHora) {
                        aviso("Rango horario inválido para " + dia + " (ejemplo: H.Ini 19 a H.Fin 22).");
                        return;
                    }
                    ventanas.add(new VentanaDiaConfig(
                            dia,
                            fechaBase,
                            LocalTime.of(dHora, 0),
                            hHora == 24 ? LocalTime.of(23, 59) : LocalTime.of(hHora, 0)
                    ));
                }
            }

            if (ventanas.isEmpty()) {
                ventanas.add(new VentanaDiaConfig("Viernes", campeonato.getFechaInicio(),
                        LocalTime.of(19, 0), LocalTime.of(22, 0)));
            }

            generarCalendarioPorCategoria(ventanas, (Integer) spCantidadCanchas.getValue());
            refrescarTodo();
        });

        JPanel norteParam = new JPanel(new GridLayout(2, 1, 4, 4));
        norteParam.setOpaque(false);
        norteParam.add(filaChecks);
        norteParam.add(contenedorConfigDias);

        cardParam.add(norteParam, BorderLayout.CENTER);
        cardParam.add(filaInferiorParam, BorderLayout.SOUTH);

        modeloPartidos = new DefaultTableModel(
                new String[]{"#", "Fase", "Categoría", "Estado", "Local", "Visitante", "Cancha", "Fecha", "Hora"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable tablaPartidos = new JTable(modeloPartidos);
        tablaPartidos.setRowHeight(22);

        JPanel pie = new JPanel(new GridLayout(1, 2, 10, 10));
        pie.setOpaque(false);

        JPanel cardReprog = crearTarjetaRedondeada(CARD_AMBAR, new Color(253, 224, 71), 16);
        cardReprog.setLayout(new BorderLayout(6, 6));
        cardReprog.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(253, 224, 71), 14), "Reprogramación de Partido"),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JPanel topReprog = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        topReprog.setOpaque(false);
        comboPartidoReprog = new JComboBox<>();
        rbReprogAuto = new JRadioButton("Automática", true);
        rbReprogManual = new JRadioButton("Manual", false);
        rbReprogAuto.setOpaque(false);
        rbReprogManual.setOpaque(false);
        ButtonGroup grupoReprog = new ButtonGroup();
        grupoReprog.add(rbReprogAuto);
        grupoReprog.add(rbReprogManual);
        topReprog.add(new JLabel("Partido #:"));
        topReprog.add(comboPartidoReprog);
        topReprog.add(rbReprogAuto);
        topReprog.add(rbReprogManual);

        panelCamposManual = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panelCamposManual.setOpaque(false);
        LocalDate fIni = campeonato.getFechaInicio();
        lblReprogFechaSeleccionada = new JLabel(nombreDiaEspanol(fIni.getDayOfWeek()) + " " + fIni.format(FMT_FECHA));
        lblReprogFechaSeleccionada.setFont(new Font("SansSerif", Font.BOLD, 12));
        JButton btnCalReprog = crearBotonRedondeado("📅 Elegir Fecha", BOTON_AZUL);
        btnCalReprog.addActionListener(e -> {
            LocalDate actual = parsearFechaSeguro(lblReprogFechaSeleccionada.getText(), campeonato.getFechaInicio());
            LocalDate elegida = abrirSelectorCalendario(actual);
            if (elegida != null) {
                lblReprogFechaSeleccionada.setText(nombreDiaEspanol(elegida.getDayOfWeek()) + " " + elegida.format(FMT_FECHA));
            }
        });

        txtReprogHoraManual = campoRedondeado("19");
        txtReprogHoraManual.setPreferredSize(new Dimension(45, 26));
        panelCamposManual.add(btnCalReprog);
        panelCamposManual.add(lblReprogFechaSeleccionada);
        panelCamposManual.add(new JLabel("Hora (24h):"));
        panelCamposManual.add(txtReprogHoraManual);
        panelCamposManual.setVisible(false);

        rbReprogAuto.addActionListener(e -> {
            panelCamposManual.setVisible(false);
            cardReprog.revalidate();
            cardReprog.repaint();
        });
        rbReprogManual.addActionListener(e -> {
            panelCamposManual.setVisible(true);
            cardReprog.revalidate();
            cardReprog.repaint();
        });

        JButton btnAplicarReprog = crearBotonRedondeado("Aplicar Reprogramación", BOTON_NARANJA);
        btnAplicarReprog.addActionListener(e -> {
            if (comboPartidoReprog.getSelectedItem() == null) return;
            int num = (Integer) comboPartidoReprog.getSelectedItem();
            boolean ok;
            if (rbReprogAuto.isSelected()) {
                ok = reprogramarAutomaticoSeguro(num);
            } else {
                LocalDate fElegida = parsearFechaSeguro(lblReprogFechaSeleccionada.getText(), campeonato.getFechaInicio());
                String fechaStr = fElegida.format(FMT_FECHA);
                int h24;
                try {
                    h24 = Integer.parseInt(txtReprogHoraManual.getText().trim());
                } catch (NumberFormatException ex) {
                    aviso("Ingrese una hora válida (0-23).");
                    return;
                }
                ok = reprogramarManualSeguro(num, fechaStr, h24);
            }
            refrescarTodo();
            JOptionPane.showMessageDialog(this, ok
                    ? "Partido reprogramado respetando 125 min desde su inicio hasta el inicio del siguiente."
                    : "No se encontró espacio disponible válido (125 min) o existe cruce.");
        });

        JPanel centroReprog = new JPanel(new BorderLayout(4, 4));
        centroReprog.setOpaque(false);
        centroReprog.add(topReprog, BorderLayout.NORTH);
        centroReprog.add(panelCamposManual, BorderLayout.CENTER);

        cardReprog.add(centroReprog, BorderLayout.CENTER);
        cardReprog.add(btnAplicarReprog, BorderLayout.SOUTH);

        JPanel cardFinales = crearTarjetaRedondeada(CARD_LILA, new Color(196, 181, 253), 16);
        cardFinales.setLayout(new GridLayout(3, 2, 6, 6));
        cardFinales.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(bordeSuave(new Color(196, 181, 253), 14), "Fases Finales"),
                new EmptyBorder(6, 8, 6, 8)
        ));

        comboFaseFinal = new JComboBox<>(new String[]{"Cuartos de Final", "Semifinal", "Final"});
        JButton btnGenFinal = crearBotonRedondeado("Generar Fase Final", BOTON_AZUL);

        cardFinales.add(new JLabel("Fase:"));
        cardFinales.add(comboFaseFinal);
        cardFinales.add(new JLabel(""));
        cardFinales.add(btnGenFinal);

        btnGenFinal.addActionListener(e -> {
            if (campeonato.getCalendario().getPartidos().isEmpty()) {
                aviso("Primero genere el calendario original de fase de grupos.");
                return;
            }
            for (Partido p : campeonato.getCalendario().getPartidos()) {
                if (!p.isJugado()) {
                    aviso("Complete primero todos los encuentros de la fase de grupos.");
                    return;
                }
            }
            String fase = (String) comboFaseFinal.getSelectedItem();
            int cupos = "Cuartos de Final".equals(fase) ? 8 : ("Semifinal".equals(fase) ? 4 : 2);
            List<FilaPosicion> orden = campeonato.getTablaPosiciones().getFilasOrdenadas();
            List<String> clasificados = new ArrayList<>();
            for (int i = 0; i < Math.min(cupos, orden.size()); i++) {
                clasificados.add(orden.get(i).getNombreEquipo());
            }
            campeonato.getCalendario().generarFaseFinal(fase, clasificados, LocalDate.now().plusDays(1));
            refrescarTodo();
        });

        pie.add(cardReprog);
        pie.add(cardFinales);

        panel.add(cardParam, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPartidos), BorderLayout.CENTER);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    private void generarCalendarioPorCategoria(List<VentanaDiaConfig> ventanas, int cantCanchas) {
        List<Partido> partidos = campeonato.getCalendario().getPartidos();
        partidos.clear();
        categoriaPorPartido.clear();
        localPorPartido.clear();
        visitantePorPartido.clear();
        horaExactaPorPartido.clear();
        vocalAsignadoPorPartido.clear();
        vocalesUsadosEnRonda.clear();

        Map<String, List<Equipo>> equiposPorCategoria = new LinkedHashMap<>();
        for (String catOficial : CATEGORIAS_OFICIALES) {
            equiposPorCategoria.put(catOficial, new ArrayList<>());
        }

        for (Equipo eq : campeonato.getEquipos()) {
            String catEq = normalizarCategoria(eq.getCategoria());
            equiposPorCategoria.computeIfAbsent(catEq, k -> new ArrayList<>()).add(eq);
        }

        List<String[]> cruces = new ArrayList<>();
        Set<String> crucesRegistrados = new HashSet<>();

        for (Map.Entry<String, List<Equipo>> entry : equiposPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            List<Equipo> listaEq = entry.getValue();
            if (listaEq.size() < 2) continue;

            for (int i = 0; i < listaEq.size(); i++) {
                String eqLocal = listaEq.get(i).getNombreEquipo().trim();
                for (int j = i + 1; j < listaEq.size(); j++) {
                    String eqVisitante = listaEq.get(j).getNombreEquipo().trim();
                    if (eqLocal.equalsIgnoreCase(eqVisitante)) continue;

                    String clave = categoria.toLowerCase() + "::"
                            + (eqLocal.compareToIgnoreCase(eqVisitante) <= 0
                            ? eqLocal.toLowerCase() + "::" + eqVisitante.toLowerCase()
                            : eqVisitante.toLowerCase() + "::" + eqLocal.toLowerCase());

                    if (!crucesRegistrados.contains(clave)) {
                        crucesRegistrados.add(clave);
                        cruces.add(new String[]{eqLocal, eqVisitante, categoria});
                    }
                }
            }
        }

        if (cruces.isEmpty()) {
            aviso("Debe inscribir al menos 2 equipos en una misma categoría (Sub-18, Senior o Máster) para generar encuentros.");
            return;
        }

        LocalDate fechaInicioCamp = campeonato.getFechaInicio();
        Map<String, List<int[]>> ocupacionCancha = new HashMap<>();
        Map<String, List<int[]>> ocupacionEquipo = new HashMap<>();

        int numero = 1;
        for (String[] cruce : cruces) {
            String eqLocal = cruce[0];
            String eqVisitante = cruce[1];
            String categoria = cruce[2];

            boolean ubicado = false;
            for (int semana = 0; semana <= 52 && !ubicado; semana++) {
                for (VentanaDiaConfig v : ventanas) {
                    LocalDate fechaReal = v.fechaBase.plusWeeks(semana);
                    if (fechaReal.isBefore(fechaInicioCamp)) {
                        fechaReal = fechaInicioCamp;
                    }
                    String fechaStr = fechaReal.format(FMT_FECHA);

                    int inicioVentanaMin = v.horaInicio.getHour() * 60 + v.horaInicio.getMinute();
                    int finVentanaMin = v.horaFin.getHour() * 60 + v.horaFin.getMinute();

                    for (int inicioMin = inicioVentanaMin;
                         inicioMin + MIN_JUEGO <= finVentanaMin && !ubicado;
                         inicioMin += BLOQUE_TOTAL_MIN) {

                        int finOcupacionMin = inicioMin + BLOQUE_TOTAL_MIN;

                        for (int c = 1; c <= cantCanchas; c++) {
                            String canchaStr = "Cancha " + c;
                            List<int[]> slotsCancha = ocupacionCancha
                                    .computeIfAbsent(fechaStr + "|" + canchaStr, k -> new ArrayList<>());
                            List<int[]> slotsLocal = ocupacionEquipo
                                    .computeIfAbsent(fechaStr + "|" + eqLocal.toLowerCase(), k -> new ArrayList<>());
                            List<int[]> slotsVisita = ocupacionEquipo
                                    .computeIfAbsent(fechaStr + "|" + eqVisitante.toLowerCase(), k -> new ArrayList<>());

                            if (!haySolapamientoMin(slotsCancha, inicioMin, finOcupacionMin)
                                    && !haySolapamientoMin(slotsLocal, inicioMin, finOcupacionMin)
                                    && !haySolapamientoMin(slotsVisita, inicioMin, finOcupacionMin)) {

                                slotsCancha.add(new int[]{inicioMin, finOcupacionMin});
                                slotsLocal.add(new int[]{inicioMin, finOcupacionMin});
                                slotsVisita.add(new int[]{inicioMin, finOcupacionMin});

                                String horaFormateada = LocalTime.of(inicioMin / 60, inicioMin % 60).format(FMT_HORA);

                                Partido p = crearPartidoSeguro(
                                        numero,
                                        eqLocal,
                                        eqVisitante,
                                        categoria,
                                        canchaStr,
                                        fechaStr,
                                        inicioMin / 60,
                                        horaFormateada
                                );
                                p.setFase("Fase de Grupos");
                                p.setEstado("Programado");
                                categoriaPorPartido.put(numero, categoria);
                                localPorPartido.put(numero, eqLocal);
                                visitantePorPartido.put(numero, eqVisitante);
                                horaExactaPorPartido.put(numero, horaFormateada);
                                partidos.add(p);
                                numero++;
                                ubicado = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (!ubicado) {
                aviso("No alcanza el tiempo configurado para ubicar más partidos con bloques de 125 min de inicio a inicio.");
                break;
            }
        }
    }

    // =========================================================================
    // VOCALÍA AUTOMÁTICA (PARTIDO ANTERIOR O SIGUIENTE, SIN REPETIR EN RONDA)
    // =========================================================================
    private String obtenerOCrearVocalAutomatico(Partido actual) {
        if (actual == null) return "Vocal Automático";
        String existente = vocalAsignadoPorPartido.get(actual.getNumero());
        if (existente != null && !existente.isEmpty()) {
            return existente;
        }

        List<Partido> lista = campeonato.getCalendario().getPartidos();
        int idxActual = -1;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNumero() == actual.getNumero()) {
                idxActual = i;
                break;
            }
        }

        Set<String> equiposActuales = new HashSet<>();
        equiposActuales.add(obtenerLocalPartido(actual).toLowerCase());
        equiposActuales.add(obtenerVisitantePartido(actual).toLowerCase());

        // Candidatos de partidos adyacentes (anterior y siguiente)
        List<Jugador> candidatosAdyacentes = new ArrayList<>();
        int[] vecinos = {idxActual - 1, idxActual + 1};
        for (int idxV : vecinos) {
            if (idxV >= 0 && idxV < lista.size()) {
                Partido pVec = lista.get(idxV);
                String locV = obtenerLocalPartido(pVec);
                String visV = obtenerVisitantePartido(pVec);
                agregarJugadoresDeEquipoSiNoJuega(candidatosAdyacentes, locV, equiposActuales);
                agregarJugadoresDeEquipoSiNoJuega(candidatosAdyacentes, visV, equiposActuales);
            }
        }

        // Respaldo: otros equipos que no juegan este partido
        if (candidatosAdyacentes.isEmpty()) {
            for (Equipo eq : campeonato.getEquipos()) {
                if (!equiposActuales.contains(eq.getNombreEquipo().toLowerCase())) {
                    candidatosAdyacentes.addAll(eq.getJugadores());
                }
            }
        }

        // Si todos los candidatos ya fueron vocales en esta ronda, iniciar nueva ronda
        List<Jugador> disponiblesEnRonda = new ArrayList<>();
        for (Jugador j : candidatosAdyacentes) {
            String claveJug = j.getCedula();
            if (!vocalesUsadosEnRonda.contains(claveJug)) {
                disponiblesEnRonda.add(j);
            }
        }
        if (disponiblesEnRonda.isEmpty()) {
            vocalesUsadosEnRonda.clear();
            disponiblesEnRonda.addAll(candidatosAdyacentes);
        }

        String elegido;
        if (!disponiblesEnRonda.isEmpty()) {
            Jugador sel = disponiblesEnRonda.get((actual.getNumero() - 1) % disponiblesEnRonda.size());
            String eqPer = buscarEquipoDeJugador(sel.getCedula());
            elegido = sel.getNombre() + " " + sel.getApellido()
                    + (eqPer.isEmpty() ? "" : " (" + eqPer + ")");
            vocalesUsadosEnRonda.add(sel.getCedula());
        } else {
            elegido = "Vocal Automático (Partido #" + actual.getNumero() + ")";
        }

        vocalAsignadoPorPartido.put(actual.getNumero(), elegido);
        return elegido;
    }

    private void agregarJugadoresDeEquipoSiNoJuega(List<Jugador> destino, String nombreEquipo, Set<String> jugando) {
        if (nombreEquipo == null || jugando.contains(nombreEquipo.toLowerCase())) return;
        for (Equipo eq : campeonato.getEquipos()) {
            if (eq.getNombreEquipo().equalsIgnoreCase(nombreEquipo)) {
                destino.addAll(eq.getJugadores());
            }
        }
    }

    private String buscarEquipoDeJugador(String cedula) {
        for (Equipo eq : campeonato.getEquipos()) {
            for (Jugador j : eq.getJugadores()) {
                if (j.getCedula().equals(cedula)) return eq.getNombreEquipo();
            }
        }
        return "";
    }

    private Partido crearPartidoSeguro(int numero, String local, String visitante, String categoria,
                                       String cancha, String fecha, int horaEntera, String horaFormateada) {
        Partido p = new Partido(numero, local, visitante, categoria, cancha, fecha, horaEntera);
        invocarSetterString(p, new String[]{"setLocal", "setEquipoLocal"}, local);
        invocarSetterString(p, new String[]{"setVisitante", "setEquipoVisitante"}, visitante);
        invocarSetterString(p, new String[]{"setCategoria", "setNombreCategoria"}, categoria);
        invocarSetterString(p, new String[]{"setHoraFormato24", "setHoraTexto", "setHoraString"}, horaFormateada);
        return p;
    }

    private void invocarSetterString(Object obj, String[] metodos, String valor) {
        if (obj == null || valor == null) return;
        for (String nombre : metodos) {
            try {
                Method m = obj.getClass().getMethod(nombre, String.class);
                m.invoke(obj, valor);
                return;
            } catch (Exception ignored) {
            }
        }
    }

    private String obtenerLocalPartido(Partido p) {
        if (p == null) return "";
        String guardada = localPorPartido.get(p.getNumero());
        return (guardada != null && !guardada.trim().isEmpty()) ? guardada.trim() : p.getLocal();
    }

    private String obtenerVisitantePartido(Partido p) {
        if (p == null) return "";
        String guardada = visitantePorPartido.get(p.getNumero());
        return (guardada != null && !guardada.trim().isEmpty()) ? guardada.trim() : p.getVisitante();
    }

    private String obtenerCategoriaPartido(Partido p) {
        if (p == null) return "";
        String guardada = categoriaPorPartido.get(p.getNumero());
        if (guardada != null && !guardada.trim().isEmpty()) {
            return normalizarCategoria(guardada);
        }
        String loc = obtenerLocalPartido(p);
        for (Equipo eq : campeonato.getEquipos()) {
            if (eq.getNombreEquipo().equalsIgnoreCase(loc)) {
                return normalizarCategoria(eq.getCategoria());
            }
        }
        return "";
    }

    private String obtenerHoraMostrarPartido(Partido p) {
        if (p == null) return "";
        String exacta = horaExactaPorPartido.get(p.getNumero());
        if (exacta != null && !exacta.trim().isEmpty()) {
            return exacta.trim();
        }
        int h24 = obtenerHora24(p);
        if (h24 >= 0 && h24 <= 23) {
            return LocalTime.of(h24, 0).format(FMT_HORA);
        }
        return p.getHoraFormato24();
    }

    private boolean haySolapamientoMin(List<int[]> intervalos, int inicioNuevo, int finNuevo) {
        for (int[] iv : intervalos) {
            if (inicioNuevo < iv[1] && finNuevo > iv[0]) {
                return true;
            }
        }
        return false;
    }

    private boolean reprogramarAutomaticoSeguro(int numeroPartido) {
        Partido objetivo = campeonato.getCalendario().buscarPartidoPorNumero(numeroPartido);
        if (objetivo == null) return false;

        int canchas = Math.max(1, (Integer) spCantidadCanchas.getValue());
        List<Integer> horasDisponibles = new ArrayList<>();
        for (String d : ordenDias) {
            JCheckBox chk = checksDias.get(d);
            if (chk != null && chk.isSelected()) {
                try {
                    int h1 = Integer.parseInt(camposHoraDesdeDia.get(d).getText().trim());
                    int h2 = Integer.parseInt(camposHoraHastaDia.get(d).getText().trim());
                    agregarRangoHorasConBloque125(horasDisponibles, h1, h2);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (horasDisponibles.isEmpty()) {
            agregarRangoHorasConBloque125(horasDisponibles, 19, 22);
        }

        LocalDate inicioCampeonato = campeonato.getFechaInicio();
        LocalDate fechaActualPartido;
        try {
            fechaActualPartido = LocalDate.parse(objetivo.getFecha(), FMT_FECHA);
        } catch (DateTimeParseException ex) {
            fechaActualPartido = inicioCampeonato;
        }

        LocalDate piso = fechaActualPartido.isBefore(inicioCampeonato)
                ? inicioCampeonato
                : fechaActualPartido.plusDays(1);

        for (int offsetDias = 0; offsetDias <= 180; offsetDias++) {
            LocalDate candidata = piso.plusDays(offsetDias);
            if (candidata.isBefore(inicioCampeonato)) continue;
            String fechaCandidata = candidata.format(FMT_FECHA);
            for (int hora : horasDisponibles) {
                for (int c = 1; c <= canchas; c++) {
                    String canchaCandidata = "Cancha " + c;
                    if (!existeCruceEnHorario(objetivo, fechaCandidata, hora, canchaCandidata)) {
                        objetivo.setFecha(fechaCandidata);
                        objetivo.setHora(hora);
                        objetivo.setCancha(canchaCandidata);
                        objetivo.setEstado("Reprogramado (Auto)");
                        String horaTxt = LocalTime.of(hora, 0).format(FMT_HORA);
                        horaExactaPorPartido.put(objetivo.getNumero(), horaTxt);
                        invocarSetterString(objetivo, new String[]{"setHoraFormato24", "setHoraTexto"}, horaTxt);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean reprogramarManualSeguro(int numeroPartido, String fechaStr, int hora24) {
        Partido objetivo = campeonato.getCalendario().buscarPartidoPorNumero(numeroPartido);
        if (objetivo == null || hora24 < 0 || hora24 > 23) return false;
        LocalDate fechaNueva;
        try {
            fechaNueva = LocalDate.parse(fechaStr, FMT_FECHA);
        } catch (DateTimeParseException ex) {
            return false;
        }
        if (fechaNueva.isBefore(campeonato.getFechaInicio())) {
            return false;
        }
        if (existeCruceEnHorario(objetivo, fechaStr, hora24, objetivo.getCancha())) {
            return false;
        }
        objetivo.setFecha(fechaStr);
        objetivo.setHora(hora24);
        objetivo.setEstado("Reprogramado (Manual)");
        String horaTxt = LocalTime.of(hora24, 0).format(FMT_HORA);
        horaExactaPorPartido.put(objetivo.getNumero(), horaTxt);
        invocarSetterString(objetivo, new String[]{"setHoraFormato24", "setHoraTexto"}, horaTxt);
        return true;
    }

    private int obtenerHora24(Partido p) {
        if (p == null) return -1;
        String exacta = horaExactaPorPartido.get(p.getNumero());
        if (exacta != null && exacta.contains(":")) {
            try {
                return Integer.parseInt(exacta.split(":")[0].trim());
            } catch (NumberFormatException ignored) {
            }
        }
        for (String nombreMetodo : new String[]{"getHora24", "getHora"}) {
            try {
                Method m = p.getClass().getMethod(nombreMetodo);
                Object val = m.invoke(p);
                if (val instanceof Number) {
                    return ((Number) val).intValue();
                }
            } catch (Exception ignored) {
            }
        }
        String fmt = p.getHoraFormato24();
        if (fmt != null) {
            String soloDigitos = fmt.replaceAll("[^0-9].*", "");
            if (!soloDigitos.isEmpty()) {
                try {
                    return Integer.parseInt(soloDigitos);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return -1;
    }

    private int obtenerMinutoInicioPartido(Partido p) {
        String exacta = horaExactaPorPartido.get(p.getNumero());
        if (exacta != null && exacta.contains(":")) {
            try {
                String[] partes = exacta.split(":");
                int h = Integer.parseInt(partes[0].trim());
                int m = Integer.parseInt(partes[1].trim());
                return h * 60 + m;
            } catch (Exception ignored) {
            }
        }
        int h24 = obtenerHora24(p);
        return h24 >= 0 ? h24 * 60 : -1;
    }

    private boolean existeCruceEnHorario(Partido objetivo, String fecha, int hora, String cancha) {
        int inicioNuevo = hora * 60;
        int finNuevo = inicioNuevo + BLOQUE_TOTAL_MIN;
        String locObj = obtenerLocalPartido(objetivo);
        String visObj = obtenerVisitantePartido(objetivo);

        for (Partido otro : campeonato.getCalendario().getPartidos()) {
            if (otro.getNumero() == objetivo.getNumero()) continue;
            if (!otro.getFecha().equals(fecha)) continue;
            int inicioOtro = obtenerMinutoInicioPartido(otro);
            if (inicioOtro < 0) continue;

            int finOtro = inicioOtro + BLOQUE_TOTAL_MIN;
            boolean chocaTiempo = inicioNuevo < finOtro && finNuevo > inicioOtro;

            if (chocaTiempo) {
                if (otro.getCancha().equalsIgnoreCase(cancha)) return true;
                String locOtro = obtenerLocalPartido(otro);
                String visOtro = obtenerVisitantePartido(otro);
                boolean comparteEquipo = locOtro.equalsIgnoreCase(locObj)
                        || visOtro.equalsIgnoreCase(locObj)
                        || locOtro.equalsIgnoreCase(visObj)
                        || visOtro.equalsIgnoreCase(visObj);
                if (comparteEquipo) return true;
            }
        }
        return false;
    }

    // =========================================================================
    // 7. ACTA Y CONSULTA
    // =========================================================================
    private JPanel crearTabActaConsultas() {
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Acta de Partido", crearSubTabActa());
        subTabs.addTab("Consulta de Partidos", crearSubTabConsulta());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_GENERAL);
        wrapper.add(subTabs, BorderLayout.CENTER);
        return wrapper;
    }

    private boolean isActaCerrada(Partido p) {
        return p != null && p.getActa() != null && p.getActa().isCerrada();
    }

    private JPanel crearSubTabActa() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_GENERAL);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = crearTarjetaRedondeada(CARD_AZUL, new Color(147, 197, 253), 14);
        top.setLayout(new GridLayout(3, 1, 4, 4));
        top.setBorder(new EmptyBorder(6, 10, 6, 10));

        JPanel filaInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaInfo.setOpaque(false);
        comboPartidoActa = new JComboBox<>();
        lblActaFechaHora = new JLabel("FECHA: — | HORA INICIO: —");
        lblActaFechaHora.setFont(new Font("SansSerif", Font.BOLD, 12));

        lblActaVocalAuto = new JLabel("VOCAL AUTO: —");
        lblActaVocalAuto.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblActaVocalAuto.setForeground(AZUL_HEADER);

        txtMinJugadoresActa = campoRedondeado(String.valueOf(minJugadoresInicio));
        txtMinJugadoresActa.setPreferredSize(new Dimension(38, 24));
        btnAplicarMinJugadores = crearBotonRedondeado("Fijar Mínimo", BOTON_AZUL);

        filaInfo.add(new JLabel("PARTIDO #:"));
        filaInfo.add(comboPartidoActa);
        filaInfo.add(lblActaFechaHora);
        filaInfo.add(lblActaVocalAuto);
        filaInfo.add(new JLabel("Mín:"));
        filaInfo.add(txtMinJugadoresActa);
        filaInfo.add(btnAplicarMinJugadores);

        JPanel filaCrono = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaCrono.setOpaque(false);
        lblRelojPartido = new JLabel("Cronómetro: 00:00 (Min 0/90)");
        lblRelojPartido.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblRelojPartido.setForeground(AZUL_HEADER);
        btnInicioReanudar = crearBotonRedondeado("Inicio / Reanudar", BOTON_VERDE);
        lblConteoCancha = new JLabel("En cancha -> Local: 0 | Visita: 0");
        lblConteoCancha.setFont(new Font("SansSerif", Font.BOLD, 12));

        lblEstadoActaBloqueo = new JLabel("Acta: Abierta");
        lblEstadoActaBloqueo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEstadoActaBloqueo.setForeground(BOTON_VERDE);

        btnInicioReanudar.addActionListener(e -> {
            Partido p = obtenerPartidoActa();
            if (isActaCerrada(p)) {
                aviso("El acta de este partido ya está cerrada y no se puede modificar.");
                return;
            }
            relojTimer.start();
        });

        filaCrono.add(lblRelojPartido);
        filaCrono.add(btnInicioReanudar);
        filaCrono.add(lblConteoCancha);
        filaCrono.add(lblEstadoActaBloqueo);

        JPanel filaAsistencia = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaAsistencia.setOpaque(false);
        comboAsistencia = new JComboBox<>(new String[]{
                "Ambos equipos completos",
                "Local incompleto (Pierde Local)",
                "Visita incompleta (Pierde Visita)",
                "Ninguno / Ambos incompletos (Ambos pierden)"
        });
        btnAplicarAsist = crearBotonRedondeado("Aplicar Asistencia / Iniciar Partido", BOTON_NARANJA);
        btnCerrarActa = crearBotonRedondeado("Cerrar Acta", BOTON_AZUL);

        filaAsistencia.add(new JLabel("Asistencia:"));
        filaAsistencia.add(comboAsistencia);
        filaAsistencia.add(btnAplicarAsist);
        filaAsistencia.add(btnCerrarActa);

        top.add(filaInfo);
        top.add(filaCrono);
        top.add(filaAsistencia);

        btnAplicarMinJugadores.addActionListener(e -> {
            Partido p = obtenerPartidoActa();
            if (isActaCerrada(p)) {
                aviso("El acta ya está cerrada y no permite cambios.");
                return;
            }
            try {
                int nuevoMin = Integer.parseInt(txtMinJugadoresActa.getText().trim());
                if (nuevoMin < 1 || nuevoMin > 11) {
                    aviso("Ingrese un mínimo de jugadores entre 1 y 11.");
                    return;
                }
                minJugadoresInicio = nuevoMin;
                if (p != null) {
                    p.setActa(new ActaPartido(p, minJugadoresInicio));
                    actualizarConteoCancha(p);
                }
                JOptionPane.showMessageDialog(this, "Mínimo de jugadores por equipo fijado en: " + minJugadoresInicio);
            } catch (NumberFormatException ex) {
                aviso("Ingrese un número entero válido para el mínimo de jugadores.");
            }
        });

        comboPartidoActa.addActionListener(e -> cargarDatosActa());

        btnAplicarAsist.addActionListener(e -> {
            Partido p = obtenerPartidoActa();
            if (p == null) return;
            if (isActaCerrada(p)) {
                aviso("El acta ya está cerrada; ya no se puede modificar ningún resultado.");
                return;
            }
            String loc = obtenerLocalPartido(p);
            String vis = obtenerVisitantePartido(p);
            String op = (String) comboAsistencia.getSelectedItem();
            p.setJugado(true);
            if ("Local incompleto (Pierde Local)".equals(op)) {
                p.setEstado("Perdido: " + loc);
                p.setGolesLocal(0);
                p.setGolesVisitante(3);
            } else if ("Visita incompleta (Pierde Visita)".equals(op)) {
                p.setEstado("Perdido: " + vis);
                p.setGolesLocal(3);
                p.setGolesVisitante(0);
            } else if ("Ninguno / Ambos incompletos (Ambos pierden)".equals(op)) {
                p.setEstado("Ninguno / Ambos pierden");
                p.setGolesLocal(0);
                p.setGolesVisitante(0);
            } else {
                if (!validarMinimoEnCancha(p)) return;
                p.setEstado("En juego");
                relojTimer.start();
            }
            campeonato.getTablaPosiciones().recalcularTodo(
                    campeonato.getEquipos(), campeonato.getCalendario().getPartidos());
            refrescarTodo();
        });

        btnCerrarActa.addActionListener(e -> {
            Partido p = obtenerPartidoActa();
            if (p == null || p.getActa() == null) return;
            if (isActaCerrada(p)) {
                aviso("Esta acta ya fue cerrada anteriormente.");
                return;
            }
            if (!validarMinimoEnCancha(p)) return;
            p.setJugado(true);
            p.setEstado("Finalizado");
            p.getActa().setCerrada(true);
            relojTimer.stop();
            aplicarCobroAlCerrarPartido(p);
            campeonato.getTablaPosiciones().recalcularTodo(
                    campeonato.getEquipos(), campeonato.getCalendario().getPartidos());
            refrescarTodo();
            JOptionPane.showMessageDialog(this,
                    "Acta cerrada: ya no se puede modificar ningún resultado y se aplicó el cobro del siguiente partido.");
        });

        JPanel barraCancha = crearTarjetaRedondeada(CARD_VERDE, new Color(134, 239, 172), 14);
        barraCancha.setLayout(new GridLayout(1, 3, 6, 4));
        barraCancha.setBorder(new TitledBorder("Jugadores Habilitados -> En Cancha"));
        barraCancha.setPreferredSize(new Dimension(100, 90));

        modeloHabilitados = new DefaultListModel<>();
        listaHabilitados = new JList<>(modeloHabilitados);
        modeloEnCancha = new DefaultListModel<>();
        listaEnCancha = new JList<>(modeloEnCancha);
        btnAlistar = crearBotonRedondeado("Pasar a Cancha ->", BOTON_AZUL);

        btnAlistar.addActionListener(e -> {
            Partido p = obtenerPartidoActa();
            if (isActaCerrada(p)) {
                aviso("El acta ya está cerrada y no permite cambios.");
                return;
            }
            String sel = listaHabilitados.getSelectedValue();
            if (p != null && sel != null && !modeloEnCancha.contains(sel)) {
                modeloEnCancha.addElement(sel);
                p.getJugadoresEnCancha().add(sel);
                actualizarConteoCancha(p);
            }
        });

        barraCancha.add(new JScrollPane(listaHabilitados));
        barraCancha.add(btnAlistar);
        barraCancha.add(new JScrollPane(listaEnCancha));

        JPanel columnasEquipos = new JPanel(new GridLayout(1, 2, 10, 10));
        columnasEquipos.setOpaque(false);

        modeloPlanillaLocal = crearModeloPlanillaVocalia();
        modeloPlanillaVisita = crearModeloPlanillaVocalia();

        lblTituloEquipoLocal = new JLabel("EQUIPO LOCAL");
        comboJugadorLocalPlanilla = new JComboBox<>();
        spAmarillaLocal = new JSpinner(new SpinnerNumberModel(0, 0, 2, 1));
        spRojaLocal = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
        spMinutoLocal = new JSpinner(new SpinnerNumberModel(10, 0, 90, 1));
        btnGolLocal = crearBotonRedondeado("+Gol", BOTON_VERDE);
        btnAnotarLocal = crearBotonRedondeado("Anotar", BOTON_AZUL);

        lblTituloEquipoVisita = new JLabel("EQUIPO VISITANTE");
        comboJugadorVisitaPlanilla = new JComboBox<>();
        spAmarillaVisita = new JSpinner(new SpinnerNumberModel(0, 0, 2, 1));
        spRojaVisita = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
        spMinutoVisita = new JSpinner(new SpinnerNumberModel(10, 0, 90, 1));
        btnGolVisita = crearBotonRedondeado("+Gol", BOTON_VERDE);
        btnAnotarVisita = crearBotonRedondeado("Anotar", BOTON_AZUL);

        JPanel colLocal = crearColumnaPlanilla(true, lblTituloEquipoLocal, comboJugadorLocalPlanilla,
                spAmarillaLocal, spRojaLocal, spMinutoLocal, btnGolLocal, btnAnotarLocal, modeloPlanillaLocal);
        tablaPlanillaLocal = (JTable) ((JScrollPane) colLocal.getComponent(1)).getViewport().getView();

        JPanel colVisita = crearColumnaPlanilla(false, lblTituloEquipoVisita, comboJugadorVisitaPlanilla,
                spAmarillaVisita, spRojaVisita, spMinutoVisita, btnGolVisita, btnAnotarVisita, modeloPlanillaVisita);
        tablaPlanillaVisita = (JTable) ((JScrollPane) colVisita.getComponent(1)).getViewport().getView();

        columnasEquipos.add(colLocal);
        columnasEquipos.add(colVisita);

        JPanel centroActa = new JPanel(new BorderLayout(6, 6));
        centroActa.setOpaque(false);
        centroActa.add(barraCancha, BorderLayout.NORTH);
        centroActa.add(columnasEquipos, BorderLayout.CENTER);

        areaResumenActa = new JTextArea();
        areaResumenActa.setEditable(false);
        areaResumenActa.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollResumen = new JScrollPane(areaResumenActa);
        scrollResumen.setPreferredSize(new Dimension(100, 95));

        panel.add(top, BorderLayout.NORTH);
        panel.add(centroActa, BorderLayout.CENTER);
        panel.add(scrollResumen, BorderLayout.SOUTH);
        return panel;
    }

    private DefaultTableModel crearModeloPlanillaVocalia() {
        return new DefaultTableModel(
                new String[]{"N°", "NOMBRE", "GOLES", "AMARILLA", "ROJA", "MINUTO"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    private JPanel crearColumnaPlanilla(boolean esLocal, JLabel lblEquipo, JComboBox<String> comboJug,
                                        JSpinner spAma, JSpinner spRoj, JSpinner spMin,
                                        JButton btnGol, JButton btnAnotar, DefaultTableModel modelo) {
        JPanel col = crearTarjetaRedondeada(esLocal ? CARD_AZUL : CARD_LILA,
                esLocal ? new Color(147, 197, 253) : new Color(196, 181, 253), 14);
        col.setLayout(new BorderLayout(5, 5));
        col.setBorder(new EmptyBorder(6, 8, 6, 8));

        lblEquipo.setFont(new Font("SansSerif", Font.BOLD, 13));

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getColumnModel().getColumn(0).setMaxWidth(38);
        tabla.getColumnModel().getColumn(2).setMaxWidth(54);
        tabla.getColumnModel().getColumn(3).setMaxWidth(68);
        tabla.getColumnModel().getColumn(4).setMaxWidth(54);
        tabla.getColumnModel().getColumn(5).setMaxWidth(64);

        // Sincronizar selección de fila con el cuadrito interactivo
        tabla.getSelectionModel().addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int fila = tabla.getSelectedRow();
            if (fila >= 0 && fila < modelo.getRowCount()) {
                int dorsal = Integer.parseInt(String.valueOf(modelo.getValueAt(fila, 0)).trim());
                for (int i = 0; i < comboJug.getItemCount(); i++) {
                    String item = comboJug.getItemAt(i);
                    if (item.contains("| #" + dorsal + " ")) {
                        comboJug.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        JPanel barraRegistro = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        barraRegistro.setOpaque(false);
        comboJug.setPreferredSize(new Dimension(145, 24));
        spAma.setPreferredSize(new Dimension(42, 24));
        spRoj.setPreferredSize(new Dimension(40, 24));
        spMin.setPreferredSize(new Dimension(48, 24));

        barraRegistro.add(comboJug);
        barraRegistro.add(new JLabel("Ama(0-2):"));
        barraRegistro.add(spAma);
        barraRegistro.add(new JLabel("Roja(0-1):"));
        barraRegistro.add(spRoj);
        barraRegistro.add(new JLabel("Min(0-90):"));
        barraRegistro.add(spMin);
        barraRegistro.add(btnGol);
        barraRegistro.add(btnAnotar);

        btnGol.addActionListener(e -> {
            Partido p = obtenerPartidoActa();
            if (p == null || p.getActa() == null) return;
            if (isActaCerrada(p)) {
                aviso("El acta ya está cerrada; ya no se puede modificar ningún resultado.");
                return;
            }
            Jugador j = buscarJugadorPorEtiqueta((String) comboJug.getSelectedItem());
            if (j == null) {
                aviso("Seleccione un jugador.");
                return;
            }
            int min = (Integer) spMin.getValue();
            String eqNom = esLocal ? obtenerLocalPartido(p) : obtenerVisitantePartido(p);
            p.getActa().registrarGol(j, eqNom, min);
            if (esLocal) p.setGolesLocal(p.getGolesLocal() + 1);
            else p.setGolesVisitante(p.getGolesVisitante() + 1);
            p.setJugado(true);
            campeonato.getTablaPosiciones().recalcularTodo(
                    campeonato.getEquipos(), campeonato.getCalendario().getPartidos());
            cargarDatosActa();
            refrescarTablaPosiciones();
        });

        btnAnotar.addActionListener(e -> {
            Partido p = obtenerPartidoActa();
            if (p == null || p.getActa() == null) return;
            if (isActaCerrada(p)) {
                aviso("El acta ya está cerrada; ya no se puede modificar ningún resultado.");
                return;
            }

            Jugador j = buscarJugadorPorEtiqueta((String) comboJug.getSelectedItem());
            if (j == null) {
                aviso("Seleccione un jugador en el cuadrito.");
                return;
            }

            int amarIngresadas = (Integer) spAma.getValue();
            int rojasIngresadas = (Integer) spRoj.getValue();
            int min = (Integer) spMin.getValue();

            if (amarIngresadas == 0 && rojasIngresadas == 0) {
                aviso("Elija 1 o 2 en Ama(0-2), o 1 en Roja(0-1) para anotar.");
                return;
            }

            String eqNom = esLocal ? obtenerLocalPartido(p) : obtenerVisitantePartido(p);
            int amarPrevias = p.getActa().contarAmarillasJugador(j.getCedula());
            int totalAmarillasJugador = amarPrevias + amarIngresadas;
            boolean expulsadoPorDobleAmarilla = totalAmarillasJugador >= 2;
            boolean generaRoja = (rojasIngresadas >= 1) || expulsadoPorDobleAmarilla;

            int amarParaCobro = Math.min(2, totalAmarillasJugador) - amarPrevias;
            if (amarParaCobro < 0) amarParaCobro = 0;
            int rojasParaCobro = generaRoja ? 1 : 0;

            if (amarIngresadas > 0) {
                for (int k = 0; k < amarIngresadas; k++) {
                    p.getActa().registrarTarjeta(new Tarjeta(
                            j, eqNom, "AMARILLA", "Amonestación", min, 0, false));
                }
            }

            if (generaRoja) {
                String motivo = expulsadoPorDobleAmarilla
                        ? "Expulsión por doble amarilla"
                        : "Expulsión por tarjeta roja";
                String tipo = expulsadoPorDobleAmarilla ? "DOBLE_AMARILLA_ROJA" : "ROJA_DIRECTA";
                p.getActa().registrarTarjeta(new Tarjeta(
                        j, eqNom, tipo, motivo, min, 1, true));
            }

            if (expulsadoPorDobleAmarilla) {
                JOptionPane.showMessageDialog(this,
                        "El jugador " + j.getNombre() + " " + j.getApellido()
                                + " tiene 2 amarillas: ROJA DIRECTA y EXPULSADO.",
                        "Doble Amarilla -> Expulsión", JOptionPane.WARNING_MESSAGE);
            }

            int[] faltasEq = faltasPendientesPorEquipo.computeIfAbsent(eqNom, k -> new int[]{0, 0});
            faltasEq[0] += amarParaCobro;
            faltasEq[1] += rojasParaCobro;

            spAma.setValue(0);
            spRoj.setValue(0);
            cargarDatosActa();
            refrescarFinanzasPanel();
        });

        JPanel norteCol = new JPanel(new BorderLayout(2, 2));
        norteCol.setOpaque(false);
        norteCol.add(lblEquipo, BorderLayout.NORTH);
        norteCol.add(barraRegistro, BorderLayout.CENTER);

        col.add(norteCol, BorderLayout.NORTH);
        col.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return col;
    }

    private void actualizarEstadoControlesActa(Partido p) {
        boolean cerrada = isActaCerrada(p);
        if (lblEstadoActaBloqueo != null) {
            lblEstadoActaBloqueo.setText(cerrada ? "Acta: CERRADA (Sin cambios)" : "Acta: Abierta");
            lblEstadoActaBloqueo.setForeground(cerrada ? BOTON_ROJO : BOTON_VERDE);
        }
        if (btnAplicarAsist != null) btnAplicarAsist.setEnabled(!cerrada);
        if (btnCerrarActa != null) btnCerrarActa.setEnabled(!cerrada);
        if (btnAplicarMinJugadores != null) btnAplicarMinJugadores.setEnabled(!cerrada);
        if (btnInicioReanudar != null) btnInicioReanudar.setEnabled(!cerrada);
        if (btnAlistar != null) btnAlistar.setEnabled(!cerrada);
        if (comboAsistencia != null) comboAsistencia.setEnabled(!cerrada);
        if (txtMinJugadoresActa != null) txtMinJugadoresActa.setEnabled(!cerrada);

        if (comboJugadorLocalPlanilla != null) comboJugadorLocalPlanilla.setEnabled(!cerrada);
        if (spAmarillaLocal != null) spAmarillaLocal.setEnabled(!cerrada);
        if (spRojaLocal != null) spRojaLocal.setEnabled(!cerrada);
        if (spMinutoLocal != null) spMinutoLocal.setEnabled(!cerrada);
        if (btnGolLocal != null) btnGolLocal.setEnabled(!cerrada);
        if (btnAnotarLocal != null) btnAnotarLocal.setEnabled(!cerrada);

        if (comboJugadorVisitaPlanilla != null) comboJugadorVisitaPlanilla.setEnabled(!cerrada);
        if (spAmarillaVisita != null) spAmarillaVisita.setEnabled(!cerrada);
        if (spRojaVisita != null) spRojaVisita.setEnabled(!cerrada);
        if (spMinutoVisita != null) spMinutoVisita.setEnabled(!cerrada);
        if (btnGolVisita != null) btnGolVisita.setEnabled(!cerrada);
        if (btnAnotarVisita != null) btnAnotarVisita.setEnabled(!cerrada);
    }

    // =========================================================================
    // CONSULTA DE PARTIDOS
    // =========================================================================
    private JPanel crearSubTabConsulta() {
        JPanel panel = crearTarjetaRedondeada(Color.WHITE, new Color(203, 213, 225), 16);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barra.setOpaque(false);

        JLabel lblTituloConsulta = new JLabel("RESULTADOS Y ENCUENTROS");
        lblTituloConsulta.setForeground(AZUL_HEADER);
        lblTituloConsulta.setFont(new Font("SansSerif", Font.BOLD, 15));

        JLabel lblEq = new JLabel("Equipo:");
        lblEq.setForeground(new Color(30, 41, 59));
        lblEq.setFont(new Font("SansSerif", Font.BOLD, 12));

        comboConsultaEquipo = new JComboBox<>();
        JButton btnVerEquipo = crearBotonRedondeado("Filtrar Equipo", BOTON_AZUL);
        JButton btnVerJugados = crearBotonRedondeado("Ver Resultados Jugados", BOTON_VERDE);
        JButton btnVerTodos = crearBotonRedondeado("Ver Todos", BOTON_GRIS);

        barra.add(lblTituloConsulta);
        barra.add(lblEq);
        barra.add(comboConsultaEquipo);
        barra.add(btnVerEquipo);
        barra.add(btnVerJugados);
        barra.add(btnVerTodos);

        contenedorTarjetasConsulta = new JPanel();
        contenedorTarjetasConsulta.setLayout(new BoxLayout(contenedorTarjetasConsulta, BoxLayout.Y_AXIS));
        contenedorTarjetasConsulta.setBackground(Color.WHITE);

        ConsultaPartido consulta = new ConsultaPartido(campeonato.getCalendario().getPartidos());

        btnVerEquipo.addActionListener(e -> {
            String eq = (String) comboConsultaEquipo.getSelectedItem();
            renderizarTarjetasPartidos(consulta.consultarPorEquipo(eq));
        });
        btnVerJugados.addActionListener(e -> renderizarTarjetasPartidos(consulta.consultarPartidosJugados()));
        btnVerTodos.addActionListener(e -> renderizarTarjetasPartidos(campeonato.getCalendario().getPartidos()));

        JScrollPane scrollTarjetas = new JScrollPane(contenedorTarjetasConsulta);
        scrollTarjetas.setBorder(null);
        scrollTarjetas.getViewport().setBackground(Color.WHITE);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(scrollTarjetas, BorderLayout.CENTER);
        return panel;
    }

    private void renderizarTarjetasPartidos(List<Partido> lista) {
        contenedorTarjetasConsulta.removeAll();
        if (lista == null || lista.isEmpty()) {
            JLabel vacio = new JLabel("Sin partidos para mostrar.");
            vacio.setForeground(new Color(100, 116, 139));
            vacio.setBorder(new EmptyBorder(10, 10, 10, 10));
            contenedorTarjetasConsulta.add(vacio);
        } else {
            Map<String, List<Partido>> porFecha = new LinkedHashMap<>();
            for (Partido p : lista) {
                porFecha.computeIfAbsent(p.getFecha(), k -> new ArrayList<>()).add(p);
            }

            for (Map.Entry<String, List<Partido>> entry : porFecha.entrySet()) {
                JPanel bloqueFecha = new JPanel(new BorderLayout(4, 4));
                bloqueFecha.setBackground(Color.WHITE);
                bloqueFecha.setBorder(BorderFactory.createCompoundBorder(
                        bordeSuave(new Color(148, 163, 184), 12),
                        new EmptyBorder(8, 10, 8, 10)
                ));
                bloqueFecha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60 + entry.getValue().size() * 44));

                JLabel badgeFecha = new JLabel("  " + entry.getKey() + "  ");
                badgeFecha.setOpaque(true);
                badgeFecha.setBackground(BOTON_VERDE);
                badgeFecha.setForeground(Color.WHITE);
                badgeFecha.setFont(new Font("SansSerif", Font.BOLD, 12));

                JPanel wrapBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                wrapBadge.setOpaque(false);
                wrapBadge.add(badgeFecha);

                JPanel filasPartidos = new JPanel(new GridLayout(entry.getValue().size(), 1, 4, 4));
                filasPartidos.setOpaque(false);

                for (Partido p : entry.getValue()) {
                    String loc = obtenerLocalPartido(p);
                    String vis = obtenerVisitantePartido(p);

                    JPanel fila = new JPanel(new GridLayout(1, 3, 8, 0));
                    fila.setBackground(new Color(248, 250, 252));
                    fila.setBorder(new EmptyBorder(6, 12, 6, 12));

                    JLabel lblLoc = new JLabel(loc.toUpperCase(), SwingConstants.LEFT);
                    lblLoc.setForeground(new Color(15, 23, 42));
                    lblLoc.setFont(new Font("SansSerif", Font.BOLD, 13));

                    String textoMarcador = p.isJugado()
                            ? p.getGolesLocal() + "  -  " + p.getGolesVisitante()
                            : "vs";
                    JLabel lblScore = new JLabel(textoMarcador, SwingConstants.CENTER);
                    lblScore.setForeground(AZUL_HEADER);
                    lblScore.setFont(new Font("SansSerif", Font.BOLD, 14));

                    JLabel lblVis = new JLabel(vis.toUpperCase(), SwingConstants.RIGHT);
                    lblVis.setForeground(new Color(15, 23, 42));
                    lblVis.setFont(new Font("SansSerif", Font.BOLD, 13));

                    fila.add(lblLoc);
                    fila.add(lblScore);
                    fila.add(lblVis);
                    filasPartidos.add(fila);
                }

                bloqueFecha.add(wrapBadge, BorderLayout.NORTH);
                bloqueFecha.add(filasPartidos, BorderLayout.CENTER);
                contenedorTarjetasConsulta.add(bloqueFecha);
                contenedorTarjetasConsulta.add(Box.createVerticalStrut(8));
            }
        }
        contenedorTarjetasConsulta.revalidate();
        contenedorTarjetasConsulta.repaint();
    }

    // =========================================================================
    // VALIDACIONES, COMPONENTES Y SINCRONIZACIÓN
    // =========================================================================
    private boolean validarMinimoEnCancha(Partido p) {
        String loc = obtenerLocalPartido(p);
        String vis = obtenerVisitantePartido(p);
        int contLocal = 0;
        int contVisita = 0;
        for (String item : p.getJugadoresEnCancha()) {
            if (item.contains("(" + loc + ")")) contLocal++;
            if (item.contains("(" + vis + ")")) contVisita++;
        }
        actualizarConteoCancha(p);
        if (contLocal < minJugadoresInicio || contVisita < minJugadoresInicio) {
            aviso("Se requieren al menos " + minJugadoresInicio + " jugadores en cancha por equipo."
                    + " Actuales -> " + loc + ": " + contLocal
                    + " | " + vis + ": " + contVisita);
            return false;
        }
        return true;
    }

    private void actualizarConteoCancha(Partido p) {
        if (p == null) {
            lblConteoCancha.setText("En cancha -> Local: 0 | Visita: 0 (Mín: " + minJugadoresInicio + ")");
            return;
        }
        String loc = obtenerLocalPartido(p);
        String vis = obtenerVisitantePartido(p);
        int contLocal = 0;
        int contVisita = 0;
        for (String item : p.getJugadoresEnCancha()) {
            if (item.contains("(" + loc + ")")) contLocal++;
            if (item.contains("(" + vis + ")")) contVisita++;
        }
        lblConteoCancha.setText("En cancha -> " + loc + ": " + contLocal
                + " | " + vis + ": " + contVisita + " (Mín: " + minJugadoresInicio + ")");
    }

    private JPanel crearTarjetaRedondeada(Color fondo, Color borde, int radio) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radio, radio));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(bordeSuave(borde, radio));
        return p;
    }

    private JButton crearBotonRedondeado(String texto, Color fondo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isRollover() ? fondo.darker() : fondo;
                g2.setColor(base);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 11, 6, 11));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { btn.repaint(); }
        });
        return btn;
    }

    private JTextField campoRedondeado(String texto) {
        JTextField tf = new JTextField(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createCompoundBorder(
                bordeSuave(new Color(148, 163, 184), 12),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private AbstractBorder bordeSuave(Color color, int radio) {
        return new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, width - 1, height - 1, radio, radio));
                g2.dispose();
            }
        };
    }

    private void agregarRangoHorasConBloque125(List<Integer> destino, int desdeHora, int hastaHora) {
        if (desdeHora < 0 || hastaHora < 0 || desdeHora > 23) return;
        int inicioMin = desdeHora * 60;
        int finMin = (hastaHora >= 24 ? 24 : hastaHora) * 60;
        for (int m = inicioMin; m + MIN_JUEGO <= finMin; m += BLOQUE_TOTAL_MIN) {
            int h = m / 60;
            if (!destino.contains(h)) {
                destino.add(h);
            }
        }
        if (destino.isEmpty() && !destino.contains(desdeHora)) {
            destino.add(desdeHora);
        }
    }

    private void refrescarTablaPersonas() {
        String filtro = txtBuscarPersonaLista == null ? "" : txtBuscarPersonaLista.getText().trim().toLowerCase();
        List<Persona> ordenadas = new ArrayList<>(personasRegistradas);
        ordenadas.sort(Comparator
                .comparing((Persona p) -> p.getNombre().toLowerCase())
                .thenComparing(p -> p.getApellido().toLowerCase()));

        modeloPersonas.setRowCount(0);
        int contadorVisible = 0;
        for (Persona p : ordenadas) {
            String coincide = (p.getNombre() + " " + p.getApellido() + " " + p.getCedula()).toLowerCase();
            if (filtro.isEmpty() || coincide.contains(filtro)) {
                contadorVisible++;
                modeloPersonas.addRow(new Object[]{
                        contadorVisible, p.getNombre(), p.getApellido(), p.getEdad(), p.getCedula(), p.getTelefono()
                });
            }
        }
    }

    private Persona buscarPersonaPorCedula(String cedula) {
        for (Persona p : personasRegistradas) {
            if (p.getCedula().equals(cedula)) return p;
        }
        return null;
    }

    private Equipo obtenerEquipoPlantilla() {
        int idx = comboEqPlantilla.getSelectedIndex();
        if (idx < 0 || idx >= campeonato.getEquipos().size()) return null;
        return campeonato.getEquipos().get(idx);
    }

    private void filtrarComboPersonas() {
        if (comboPersonasFiltradas == null || txtBuscarJugador == null) return;
        Equipo eq = obtenerEquipoPlantilla();
        String filtro = txtBuscarJugador.getText().trim().toLowerCase();
        comboPersonasFiltradas.removeAllItems();
        if (eq == null) return;

        String catEq = normalizarCategoria(eq.getCategoria());
        for (Persona p : personasRegistradas) {
            if (!edadValidaParaCategoria(p.getEdad(), catEq)) {
                continue;
            }
            String texto = p.getCedula() + " - " + p.getNombre() + " " + p.getApellido() + " (" + p.getEdad() + " años)";
            if (filtro.isEmpty() || texto.toLowerCase().contains(filtro)) {
                comboPersonasFiltradas.addItem(texto);
            }
        }
    }

    private Persona obtenerPersonaFiltrada() {
        String sel = (String) comboPersonasFiltradas.getSelectedItem();
        if (sel == null || !sel.contains(" - ")) return null;
        String ced = sel.split(" - ")[0].trim();
        return buscarPersonaPorCedula(ced);
    }

    private void refrescarListaPlantilla() {
        modeloPlantilla.clear();
        Equipo eq = obtenerEquipoPlantilla();
        if (eq == null) return;
        for (Jugador j : eq.getJugadores()) {
            modeloPlantilla.addElement(j.toString());
        }
    }

    private Partido obtenerPartidoActa() {
        if (comboPartidoActa.getSelectedItem() == null) return null;
        int num = (Integer) comboPartidoActa.getSelectedItem();
        Partido p = campeonato.getCalendario().buscarPartidoPorNumero(num);
        if (p != null && p.getActa() == null) {
            p.setActa(new ActaPartido(p, minJugadoresInicio));
        }
        return p;
    }

    private void cargarDatosActa() {
        modeloHabilitados.clear();
        modeloEnCancha.clear();
        modeloPlanillaLocal.setRowCount(0);
        modeloPlanillaVisita.setRowCount(0);
        comboJugadorLocalPlanilla.removeAllItems();
        comboJugadorVisitaPlanilla.removeAllItems();

        Partido p = obtenerPartidoActa();
        if (p == null || p.getActa() == null) {
            lblActaFechaHora.setText("FECHA: — | HORA INICIO: —");
            lblActaVocalAuto.setText("VOCAL AUTO: —");
            lblTituloEquipoLocal.setText("EQUIPO LOCAL");
            lblTituloEquipoVisita.setText("EQUIPO VISITANTE");
            actualizarConteoCancha(null);
            actualizarEstadoControlesActa(null);
            return;
        }

        String loc = obtenerLocalPartido(p);
        String vis = obtenerVisitantePartido(p);
        String vocalAuto = obtenerOCrearVocalAutomatico(p);

        lblActaFechaHora.setText("FECHA: " + p.getFecha() + " | HORA INICIO: " + obtenerHoraMostrarPartido(p));
        lblActaVocalAuto.setText("VOCAL AUTO: " + vocalAuto);
        lblTituloEquipoLocal.setText("EQUIPO: " + loc.toUpperCase());
        lblTituloEquipoVisita.setText("EQUIPO: " + vis.toUpperCase());
        actualizarConteoCancha(p);
        actualizarEstadoControlesActa(p);

        for (Equipo eq : campeonato.getEquipos()) {
            boolean esLoc = eq.getNombreEquipo().equalsIgnoreCase(loc);
            boolean esVis = eq.getNombreEquipo().equalsIgnoreCase(vis);
            if (esLoc || esVis) {
                for (Jugador j : eq.getJugadores()) {
                    String etiqueta = j.getCedula() + " | #" + j.getNumeroCamiseta() + " "
                            + j.getNombre() + " " + j.getApellido() + " (" + eq.getNombreEquipo() + ")";
                    if (j.estaHabilitado()) {
                        modeloHabilitados.addElement(etiqueta);
                        if (esLoc) comboJugadorLocalPlanilla.addItem(etiqueta);
                        if (esVis) comboJugadorVisitaPlanilla.addItem(etiqueta);
                    }

                    int golesJ = p.getActa().contarGolesJugador(j.getCedula());
                    int amaJ = Math.min(2, p.getActa().contarAmarillasJugador(j.getCedula()));
                    int rojJ = Math.min(1, p.getActa().contarRojasJugador(j.getCedula()));
                    String minTexto = construirMinutosJugador(p.getActa(), j.getCedula());

                    Object[] fila = new Object[]{
                            j.getNumeroCamiseta(),
                            j.getNombre() + " " + j.getApellido(),
                            golesJ,
                            amaJ,
                            rojJ,
                            minTexto
                    };
                    if (esLoc) modeloPlanillaLocal.addRow(fila);
                    if (esVis) modeloPlanillaVisita.addRow(fila);
                }
            }
        }

        for (String enCancha : p.getJugadoresEnCancha()) {
            modeloEnCancha.addElement(enCancha);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Marcador: ").append(loc).append(" ").append(p.getGolesLocal())
                .append(" - ").append(p.getGolesVisitante()).append(" ").append(vis)
                .append(" | Vocal Auto: ").append(vocalAuto)
                .append(" | Mínimo: ").append(minJugadoresInicio)
                .append(" | Estado: ").append(p.getEstado()).append("\n");
        for (Gol g : p.getActa().getGoles()) sb.append("  [GOL] ").append(g).append("\n");
        for (Tarjeta t : p.getActa().getTarjetas()) sb.append("  [TARJETA] ").append(t).append("\n");
        areaResumenActa.setText(sb.toString());
    }

    private String construirMinutosJugador(ActaPartido acta, String cedula) {
        List<String> mins = new ArrayList<>();
        for (Gol g : acta.getGoles()) {
            if (g.getJugador().getCedula().equals(cedula)) {
                mins.add(g.getMinuto() + "'");
            }
        }
        for (Tarjeta t : acta.getTarjetas()) {
            if (t.getJugador().getCedula().equals(cedula)) {
                mins.add(t.getMinuto() + "'");
            }
        }
        return mins.isEmpty() ? "" : String.join(",", mins);
    }

    private Jugador buscarJugadorPorEtiqueta(String etiqueta) {
        if (etiqueta == null || !etiqueta.contains("|")) return null;
        String ced = etiqueta.split("\\|")[0].trim();
        for (Equipo eq : campeonato.getEquipos()) {
            for (Jugador j : eq.getJugadores()) {
                if (j.getCedula().equals(ced)) return j;
            }
        }
        return null;
    }

    private void refrescarTablaPosiciones() {
        modeloPosiciones.setRowCount(0);
        List<FilaPosicion> ordenadas = campeonato.getTablaPosiciones().getFilasOrdenadas();
        for (int i = 0; i < ordenadas.size(); i++) {
            FilaPosicion f = ordenadas.get(i);
            modeloPosiciones.addRow(new Object[]{
                    (i + 1) + ".", f.getNombreEquipo(), f.getPuntos(), f.getPj(),
                    f.getPg(), f.getPe(), f.getPp(), f.getGf(), f.getGc(), f.getDg()
            });
        }
    }

    private void refrescarFinanzasPanel() {
        actualizarResumenCobroEquipo(false);
        StringBuilder hist = new StringBuilder();
        for (String m : campeonato.getFinanzas().getMovimientos()) {
            hist.append("• ").append(m).append("\n");
        }
        areaHistorialFinanzas.setText(hist.toString());
    }

    private void refrescarTodo() {
        if (campeonatoGuardadoFijo) {
            bloquearCamposCampeonato();
        }
        if (tarifasConfiguradas) {
            bloquearCamposTarifas();
        }
        actualizarEstadoInscripcionEquipos();

        lblFechasCampeonato.setText("Inicio: " + campeonato.getFechaInicioFormateada()
                + " | Fin estimado: " + campeonato.getFechaFinEstimadaFormateada());

        if (tarifasConfiguradas) {
            FinanzasCampeonato f = campeonato.getFinanzas();
            lblResumenTarifas.setText(String.format(
                    "Fijo | Insc: $%.2f | Amarilla: $%.2f | Roja: $%.2f | Vocalía: $%.2f",
                    f.getValorInscripcion(), f.getMultaAmarilla(), f.getMultaRoja(), f.getValorVocalia()));
        }

        lblTotalPersonas.setText("Personas inscritas: " + personasRegistradas.size());
        refrescarTablaPersonas();

        int idxEq = comboEqPlantilla.getSelectedIndex();
        modeloEquipos.setRowCount(0);
        comboEqPlantilla.removeAllItems();
        comboEquipoCobro.removeAllItems();
        comboConsultaEquipo.removeAllItems();
        for (Equipo eq : campeonato.getEquipos()) {
            String catNormal = normalizarCategoria(eq.getCategoria());
            eq.setCategoria(catNormal);
            String cap = eq.getCapitan() != null
                    ? "#" + eq.getCapitan().getNumeroCamiseta() + " " + eq.getCapitan().getNombre()
                    : "Sin asignar";
            String estadoPlantilla = plantillasConfirmadas.contains(eq.getNombreEquipo())
                    ? "Confirmada" : "Pendiente";
            modeloEquipos.addRow(new Object[]{
                    eq.getNombreEquipo(), catNormal, eq.isInscripcionPagada() ? "Sí" : "No",
                    cap, eq.getJugadores().size(), estadoPlantilla
            });
            comboEqPlantilla.addItem(eq.getNombreEquipo());
            comboEquipoCobro.addItem(eq.getNombreEquipo());
            comboConsultaEquipo.addItem(eq.getNombreEquipo());
        }
        if (idxEq >= 0 && idxEq < comboEqPlantilla.getItemCount()) {
            comboEqPlantilla.setSelectedIndex(idxEq);
        }
        actualizarEtiquetaEdadCategoria();
        filtrarComboPersonas();
        refrescarListaPlantilla();
        actualizarEstadoBloqueoPlantilla();

        int idxReprog = comboPartidoReprog.getSelectedIndex();
        int idxActa = comboPartidoActa.getSelectedIndex();
        modeloPartidos.setRowCount(0);
        comboPartidoReprog.removeAllItems();
        comboPartidoActa.removeAllItems();
        for (Partido p : campeonato.getCalendario().getPartidos()) {
            modeloPartidos.addRow(new Object[]{
                    p.getNumero(),
                    p.getFase(),
                    obtenerCategoriaPartido(p),
                    p.getEstado(),
                    obtenerLocalPartido(p),
                    obtenerVisitantePartido(p),
                    p.getCancha(),
                    p.getFecha(),
                    obtenerHoraMostrarPartido(p)
            });
            comboPartidoReprog.addItem(p.getNumero());
            comboPartidoActa.addItem(p.getNumero());
        }
        if (idxReprog >= 0 && idxReprog < comboPartidoReprog.getItemCount()) {
            comboPartidoReprog.setSelectedIndex(idxReprog);
        }
        if (idxActa >= 0 && idxActa < comboPartidoActa.getItemCount()) {
            comboPartidoActa.setSelectedIndex(idxActa);
        }
        cargarDatosActa();
        refrescarTablaPosiciones();
        renderizarTarjetasPartidos(campeonato.getCalendario().getPartidos());
        refrescarFinanzasPanel();
    }

    private boolean esSoloLetras(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isLetter(c) && c != ' ') return false;
        }
        return true;
    }

    private boolean esDiezDigitos(String s) {
        if (s == null || s.length() != 10) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazCampeonatoApp().setVisible(true));
    }
}