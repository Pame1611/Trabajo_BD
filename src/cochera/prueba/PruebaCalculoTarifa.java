package cochera.prueba;

import cochera.modelo.Alquiler;
import cochera.modelo.Cliente;
import cochera.modelo.Tarifa;
import cochera.servicio.CalculadoraTarifa;
import java.time.LocalDateTime;

/**
 * Casos de Alquiler.calcularMonto(): minutos de permanencia x tarifa por hora / 60.
 * Incluye los ejemplos de la historia de usuario HU-09.
 * Ejecutar: java -cp bin cochera.prueba.PruebaCalculoTarifa
 */
public class PruebaCalculoTarifa {

    public static void main(String[] args) {
        Verificador v = new Verificador();
        LocalDateTime entrada = LocalDateTime.of(2026, 11, 2, 8, 0);
        Cliente cliente = new Cliente("45678912", "Cliente de prueba", "", entrada);
        Tarifa auto = new Tarifa(6, "Auto", 3.00);
        Tarifa camioneta = new Tarifa(8, "Camioneta Cerrada y Abierta", 5.00);

        System.out.println("=== HU-09: ejemplos del documento de requerimientos ===");
        Alquiler a = new Alquiler("ABC123", auto, entrada, "TK-2026-000001", cliente, null);
        v.igual(3.00, a.calcularMonto(entrada.plusMinutes(60)), "Auto S/ 3.00 por hora con 60 minutos paga S/ 3.00");
        Alquiler c = new Alquiler("XYZ789", camioneta, entrada, "TK-2026-000002", cliente, null);
        v.igual(7.50, c.calcularMonto(entrada.plusMinutes(90)), "Camioneta S/ 5.00 por hora con 90 minutos paga S/ 7.50");

        System.out.println("=== Auto a S/ 3.00 por hora (S/ 0.05 por minuto) ===");
        // {minutos, total esperado en centimos}
        int[][] casosAuto = {
            {0, 0}, {1, 5}, {10, 50}, {45, 225}, {61, 305}, {125, 625}, {12 * 60, 3600}, {25 * 60 + 10, 7550},
        };
        for (int[] caso : casosAuto) {
            v.igual(caso[1] / 100.0, a.calcularMonto(entrada.plusMinutes(caso[0])), caso[0] + " minutos");
        }

        System.out.println("=== Redondeo a centimos y casos limite ===");
        Tarifa moto = new Tarifa(3, "Moto Lineal", 1.50);
        Alquiler m = new Alquiler("MTR456", moto, entrada, "TK-2026-000003", cliente, null);
        v.igual(0.03, m.calcularMonto(entrada.plusMinutes(1)), "Moto 1 min = 1.50/60 = 0.025 -> S/ 0.03");
        v.igual(0.00, m.calcularMonto(entrada.minusMinutes(5)), "Una hora de salida anterior no da montos negativos");
        v.igual(m.calcularMonto(entrada.plusMinutes(37)), CalculadoraTarifa.montoPorMinutos(37, 1.50),
                "CalculadoraTarifa usa la misma regla que Alquiler.calcularMonto()");
        v.igual(5.00, a.calcularMonto(entrada.plusMinutes(100).plusSeconds(59)), "Los segundos sueltos no se cobran");

        v.resumen();
        System.exit(v.getFallos() == 0 ? 0 : 1);
    }
}
