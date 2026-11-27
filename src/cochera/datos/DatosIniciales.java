package cochera.datos;

import cochera.excepcion.ValidacionException;
import cochera.modelo.Administrador;
import cochera.modelo.Alquiler;
import cochera.modelo.Cliente;
import cochera.modelo.Empleado;
import cochera.modelo.MetodoPago;
import cochera.modelo.Pago;
import cochera.modelo.Tarifa;
import cochera.modelo.TurnoCaja;
import cochera.modelo.Venta;
import cochera.util.Reloj;
import java.time.LocalDateTime;

/**
 * Carga inicial de datos: tarifario por hora, usuarios de prueba (DNI + contraseña)
 * y, opcionalmente, clientes y movimientos de demostración.
 */
public final class DatosIniciales {

    public static final String DNI_ADMIN = "11111111";
    public static final String DNI_CAJERO1 = "22222222";
    public static final String DNI_CAJERO2 = "33333333";
    public static final String CLAVE_DEMO_ADMIN = "admin123";
    public static final String CLAVE_DEMO_CAJERO = "cajero123";

    private DatosIniciales() {
    }

    public static void cargar(BaseDatos bd, Reloj reloj) {
        cargarTarifario(bd);
        LocalDateTime ahora = reloj.ahora();
        try {
            bd.insertarUsuario(new Administrador(DNI_ADMIN, CLAVE_DEMO_ADMIN, "Administrador General", ahora));
            bd.insertarUsuario(new Empleado(DNI_CAJERO1, CLAVE_DEMO_CAJERO, "Carlos Ramírez", ahora));
            bd.insertarUsuario(new Empleado(DNI_CAJERO2, CLAVE_DEMO_CAJERO, "Maria Torres", ahora));
        } catch (ValidacionException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Tarifa por hora de cada tipo de vehículo (el cobro es por minuto: tarifa / 60). */
    private static void cargarTarifario(BaseDatos bd) {
        Object[][] filas = {
            {"Bicicleta", 1.00},
            {"Triciclo", 1.50},
            {"Moto Lineal", 1.50},
            {"Carretilla", 1.50},
            {"Mototaxi", 2.00},
            {"Auto", 3.00},
            {"Furgo Pequeño", 4.00},
            {"Camioneta Cerrada y Abierta", 5.00},
            {"Minivan Pequeña", 5.00},
            {"Furgon Grande", 6.00},
            {"Minivan Grande", 6.00},
            {"Camion 1 Eje", 7.00},
            {"Micro", 7.00},
            {"Maquinaria Pequeña", 7.00},
            {"Omnibus", 8.00},
            {"Mini Bus", 8.00},
            {"Camion 2 Ejes", 9.00},
            {"Maquinaria Grande", 9.00},
            {"Volquete", 9.00},
            {"Omnibus 2 Pisos", 12.00},
            {"Trailer Sin Carreta", 12.00},
            {"Trailer Con Carreta", 15.00},
        };
        for (int i = 0; i < filas.length; i++) {
            bd.insertarTarifa(new Tarifa(i + 1, (String) filas[i][0], (Double) filas[i][1]));
        }
    }

    /**
     * Datos para la exposición: clientes, un turno cerrado de ayer con 4 ventas y
     * 4 vehículos dentro con tiempos que muestran las alertas de 10 h y 12 h.
     */
    public static void cargarDemostracion(BaseDatos bd, Reloj reloj) {
        LocalDateTime ahora = reloj.ahora();
        Empleado carlos = (Empleado) bd.buscarUsuarioPorDni(DNI_CAJERO1);
        Empleado maria = (Empleado) bd.buscarUsuarioPorDni(DNI_CAJERO2);
        try {
            Cliente juan = new Cliente("45678912", "Juan Pérez Rojas", "949123456", ahora.minusDays(3));
            Cliente rosa = new Cliente("47851236", "Rosa Díaz Vega", "", ahora.minusDays(3));
            Cliente luis = new Cliente("41236987", "Luis Gómez Paredes", "961457823", ahora.minusDays(2));
            Cliente ana = new Cliente("48521473", "Ana Castillo León", "", ahora.minusDays(1));
            Cliente pedro = new Cliente("46587912", "Pedro Salazar Ruiz", "987654321", ahora.minusDays(1));
            for (Cliente c : new Cliente[] {juan, rosa, luis, ana, pedro}) {
                bd.insertarCliente(c);
            }

            // ── Turno cerrado de ayer (María) con 4 cobros, cada uno con su venta ──
            LocalDateTime apertura = ahora.minusDays(1).withHour(7).withMinute(0).withSecond(0).withNano(0);
            TurnoCaja turnoAyer = new TurnoCaja(maria, apertura);
            bd.insertarTurno(turnoAyer);

            // {placa, id tarifa, minutos de estadía, hora de salida (desde la apertura), método, cliente}
            Object[][] historicos = {
                {"T7Y274", 6, 190, 4, MetodoPago.EFECTIVO, juan},   // Auto, 3h10
                {"AJH908", 17, 790, 6, MetodoPago.YAPE, luis},      // Camion 2 ejes (entró la noche anterior)
                {"Z1I490", 3, 130, 8, MetodoPago.EFECTIVO, rosa},   // Moto lineal, 2h10
                {"T8R822", 8, 550, 10, MetodoPago.PLIN, juan},      // Camioneta, 9h10
            };
            double efectivo = 0;
            for (Object[] h : historicos) {
                LocalDateTime salida = apertura.plusHours((Integer) h[3]);
                LocalDateTime entrada = salida.minusMinutes((Integer) h[2]);
                Tarifa tarifa = bd.buscarTarifa((Integer) h[1]);
                Alquiler t = new Alquiler((String) h[0], tarifa, entrada,
                        bd.generarCorrelativoTicket(entrada.getYear()), (Cliente) h[5], maria);
                bd.insertarTicket(t);
                double monto = t.calcularMonto(salida);
                t.registrarSalida(salida);
                Pago p = new Pago(t, turnoAyer, monto, (MetodoPago) h[4], salida);
                bd.insertarPago(p);
                bd.insertarVenta(new Venta(t.getCliente(), t, p));
                if (h[4] == MetodoPago.EFECTIVO) {
                    efectivo += monto;
                }
            }
            turnoAyer.cerrar(apertura.plusHours(12), efectivo - 1.0, efectivo); // faltó S/ 1.00

            // ── Vehículos que siguen dentro ──
            Object[][] dentro = {
                {"ABC123", 6, 120, ana},    // Auto, 2 h            -> OK
                {"XYZ789", 8, 610, rosa},   // Camioneta, 10 h 10 m -> POR VENCER
                {"BUS321", 15, 740, luis},  // Omnibus, 12 h 20 m   -> +12H
                {"MTR456", 3, 95, juan},    // Moto lineal, 1 h 35 m -> OK
            };
            for (Object[] d : dentro) {
                LocalDateTime entrada = ahora.minusMinutes((Integer) d[2]);
                Alquiler t = new Alquiler((String) d[0], bd.buscarTarifa((Integer) d[1]), entrada,
                        bd.generarCorrelativoTicket(entrada.getYear()), (Cliente) d[3], carlos);
                bd.insertarTicket(t);
            }
        } catch (ValidacionException e) {
            throw new IllegalStateException(e);
        }
    }
}
