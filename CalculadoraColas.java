package optimizacion;
import java.util.Scanner;
public class CalculadoraColas {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("## Analisis de Colas para Taqueria Austin ##");
        System.out.println("");
        System.out.print("Ingrese la tasa de llegada (lambda) en clientes por hora: ");
        double lambda = scanner.nextDouble();
        System.out.print("Ingrese la tasa de servicio por cada taquero (mu) en clientes por hora: ");
        double mu = scanner.nextDouble();
        System.out.print("Ingrese el numero de taqueros o servidores (C): ");
        int c = scanner.nextInt();
        System.out.print("Ingrese la capacidad maxima del sistema (K), 0 para infinita: ");
        int k = scanner.nextInt();
        scanner.close();
        if (mu <= lambda && c < 1) {
            System.out.println("\n[ERROR] Datos invalidos. Mu debe ser mayor que Lambda si C=1.");
            return;
        }
        if (c == 1 && k > 0) {
            calcularMM1K(lambda, mu, k);
        } else if (c == 1 && k == 0) {
            calcularMM1(lambda, mu);
        } else if (c > 1 && k == 0) {
            calcularMMC(lambda, mu, c);
        } else {
            System.out.println("\n La combinacion M/M/C/K es demasiado compleja para este nivel.");
            System.out.println("Ejecutando solo M/M/C (Multiples servidores, cola infinita).");
            calcularMMC(lambda, mu, c);
        }
    }
