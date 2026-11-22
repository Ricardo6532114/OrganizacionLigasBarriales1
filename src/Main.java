import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int opc = 0;
        Scanner sc = new Scanner(System.in);
        SistemaCLI sis = new SistemaCLI();

        do {
            opc = sis.menu();
            switch (opc) {
                case 1:
                    sis.configurarCampeonatoYFinanzas();
                    break;
                case 2:
                    sis.crearPersona();
                    break;
                case 3:
                    sis.crearEquipo();
                    break;
                case 4:
                    sis.asignarJugadoresAEquipo();
                    break;
                case 5:
                    sis.mostrarEquiposYPersonas();
                    break;
                case 6:
                    sis.gestionarCalendarioYReprogramacion();
                    break;
                case 7:
                    sis.gestionarActaYTarjetas();
                    break;
                case 8:
                    sis.verPosicionesYConsultar();
                    break;
                case 9:
                    sis.abrirInterfazGrafica();
                    break;
                default:
                    System.out.println("No existe la opcion");
                    break;
            }
            System.out.print("Desea seleccionar otra opcion 1.Si / 2.No: ");
            opc = sc.nextInt();
        } while (opc == 1);
    }
}