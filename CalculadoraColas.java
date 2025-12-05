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
            System.out.println("\n[ERROR] La combinacion M/M/C/K es demasiado compleja para este nivel.");
            System.out.println("Ejecutando solo M/M/C (Multiples servidores, cola infinita).");
            calcularMMC(lambda, mu, c);
        }
    }
    public static long factorial(int n) {
        if (n < 0) return 0;
        if (n <= 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    public static void calcularMM1(double lambda, double mu) {
        if (lambda >= mu) {
            System.out.println("\n[M/M/1] El sistema es inestable (Rho >= 1).");
            return;
        }
        double rho = lambda / mu;
        double L = lambda / (mu - lambda);
        double Lq = (lambda * lambda) / (mu * (mu - lambda));
        double W = L / lambda;
        double Wq = Lq / lambda;
        double Pq = rho;
        System.out.println("\nModelo M/M/1 (Un Servidor, Cola Infinita)");
        System.out.printf("Factor de Utilizacion (rho): %.4f (%.2f%%)\n", rho, rho * 100);
        System.out.printf("L: %.4f clientes\n", L);
        System.out.printf("W: %.4f horas (%.1f minutos)\n", W, W * 60);
        System.out.printf("Lq: %.4f clientes\n", Lq);
        System.out.printf("Wq: %.4f horas (%.1f minutos)\n", Wq, Wq * 60);
        System.out.printf("Probabilidad de Esperar (Pq): %.4f (%.2f%%)\n", Pq, Pq * 100);
    }
    public static void calcularMMC(double lambda, double mu, int c) {
        double rho_servidor = lambda / (c * mu);
        if (rho_servidor >= 1) {
            System.out.println("\n[M/M/C] El sistema es inestable (Rho >= 1).");
            return;
        }
        double sumatoria = 0.0;
        for (int n = 0; n < c; n++) {
            sumatoria += (Math.pow(lambda / mu, n) / factorial(n));
        }
        double denominador = sumatoria + (Math.pow(lambda / mu, c) / (factorial(c) * (1 - rho_servidor)));
        double P0 = 1.0 / denominador;
        double P_espera = (P0 * Math.pow(lambda / mu, c) * rho_servidor) / (factorial(c) * Math.pow(1 - rho_servidor, 2));
        double Lq = P_espera;
        double Wq = Lq / lambda;
        double W = Wq + (1 / mu);
        double L = Lq + (lambda / mu);
        System.out.println("\nModelo M/M/C (C Servidores, Cola Infinita)");
        System.out.printf("Numero de Servidores (C): %d\n", c);
        System.out.printf("Utilizacion por Servidor (rho_servidor): %.4f (%.2f%%)\n", rho_servidor, rho_servidor * 100);
        System.out.printf("Probabilidad de Cero Clientes (P0): %.4f\n", P0);
        System.out.printf("Lq: %.4f clientes\n", Lq);
        System.out.printf("Wq: %.4f horas (%.1f minutos)\n", Wq, Wq * 60);
        System.out.printf("L: %.4f clientes\n", L);
        System.out.printf("W: %.4f horas (%.1f minutos)\n", W, W * 60);
        System.out.printf("Probabilidad de Esperar (Pq): %.4f (%.2f%%)\n", (Lq > 0.0001) ? Pq : 0.0, (Lq > 0.0001 ? Pq : 0.0) * 100);
    }
    public static void calcularMM1K(double lambda, double mu, int k) {
        double rho = lambda / mu;
        double P0;
        if (rho == 1.0) {
            P0 = 1.0 / (k + 1);
        } else {
            P0 = (1.0 - rho) / (1.0 - Math.pow(rho, k + 1));
        }
        double Pk = P0 * Math.pow(rho, k);
        double lambda_efectiva = lambda * (1.0 - Pk);
        double Lq;
        if (rho == 1.0) {
            Lq = (k * (k - 1.0)) / (2.0 * (k + 1.0));
        } else {
            Lq = (rho * (1.0 - Math.pow(rho, k) - k * Math.pow(rho, k) * (1.0 - rho))) / ((1.0 - rho) * (1.0 - Math.pow(rho, k + 1.0)));
        }
        double L = Lq + (lambda_efectiva / mu);
        double W = L / lambda_efectiva;
        double Wq = Lq / lambda_efectiva;
        System.out.println("\nModelo M/M/1/K (Un Servidor, Capacidad Maxima K=" + k + ")");
        System.out.printf("Probabilidad de Sistema Vacio (P0): %.4f\n", P0);
        System.out.printf("Probabilidad de Rechazo (Pwwq o Pk): %.4f (%.2f%%)\n", Pk, Pk * 100);
        System.out.printf("Tasa de Llegada Efectiva (lambda_efectiva): %.4f clientes/h\n", lambda_efectiva);
        System.out.printf("Lq: %.4f clientes\n", Lq);
        System.out.printf("Wq: %.4f horas (%.1f minutos)\n", Wq, Wq * 60);
        System.out.printf("L: %.4f clientes\n", L);
        System.out.printf("W: %.4f horas (%.1f minutos)\n", W, W * 60);
    }
}



