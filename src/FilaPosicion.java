public class FilaPosicion {

    private String nombreEquipo;
    private int pj;
    private int pg;
    private int pe;
    private int pp;
    private int gf;
    private int gc;
    private int puntos;

    public FilaPosicion(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public int getPj() {
        return pj;
    }

    public int getPg() {
        return pg;
    }

    public int getPe() {
        return pe;
    }

    public int getPp() {
        return pp;
    }

    public int getGf() {
        return gf;
    }

    public int getGc() {
        return gc;
    }

    public int getDg() {
        return gf - gc;
    }

    public int getPuntos() {
        return puntos;
    }

    public void registrarResultado(int golesAnotados, int golesRecibidos, String resultado) {
        pj++;
        gf += golesAnotados;
        gc += golesRecibidos;
        if ("G".equals(resultado)) {
            pg++;
            puntos += 3;
        } else if ("E".equals(resultado)) {
            pe++;
            puntos += 1;
        } else {
            pp++;
        }
    }
}