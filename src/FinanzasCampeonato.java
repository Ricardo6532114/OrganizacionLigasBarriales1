import java.util.ArrayList;
import java.util.List;

public class FinanzasCampeonato {

    private double valorInscripcion;
    private double multaAmarilla;
    private double multaRoja;
    private double valorVocalia;
    private List<String> movimientos = new ArrayList<>();

    public FinanzasCampeonato() {
        this.valorInscripcion = 0.0;
        this.multaAmarilla = 0.0;
        this.multaRoja = 0.0;
        this.valorVocalia = 0.0;
    }

    public void configurarTarifasIniciales(double valorInscripcion, double multaAmarilla,
                                           double multaRoja, double valorVocalia) {
        this.valorInscripcion = valorInscripcion;
        this.multaAmarilla = multaAmarilla;
        this.multaRoja = multaRoja;
        this.valorVocalia = valorVocalia;
        movimientos.add("Tarifas iniciales -> Inscripción: $" + valorInscripcion
                + " | Multa Amarilla: $" + multaAmarilla
                + " | Multa Roja: $" + multaRoja
                + " | Vocalía: $" + valorVocalia);
    }

    public double getValorInscripcion() {
        return valorInscripcion;
    }

    public double getMultaAmarilla() {
        return multaAmarilla;
    }

    public double getMultaRoja() {
        return multaRoja;
    }

    public double getValorVocalia() {
        return valorVocalia;
    }

    public double calcularMultaPartido(int amarillas, int rojas) {
        return (amarillas * multaAmarilla) + (rojas * multaRoja);
    }

    public double calcularCobroSiguientePartido(int amarillasAnterior, int rojasAnterior) {
        return valorVocalia + calcularMultaPartido(amarillasAnterior, rojasAnterior);
    }

    public void registrarPago(String equipo, String concepto, double monto) {
        movimientos.add("Pago - Equipo: " + equipo + " | Concepto: " + concepto + " | Monto: $" + monto);
    }

    public List<String> getMovimientos() {
        return movimientos;
    }
}