package cochera.prueba;

import cochera.ContextoAplicacion;
import cochera.datos.DatosIniciales;
import cochera.dto.ResultadoCobro;
import cochera.excepcion.ValidacionException;
import cochera.impresion.TicketSalida;
import cochera.modelo.Alquiler;
import cochera.modelo.AlquilerMensual;
import cochera.modelo.Cliente;
import cochera.modelo.MetodoPago;
import cochera.modelo.Tarifa;
import cochera.modelo.TipoAlquiler;
import cochera.servicio.AlquilerMensualServicio;
import cochera.servicio.NivelAlerta;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RP-03: alquiler por mes con 10% de descuento por vehículo.
 * AlquilerMensual hereda de Alquiler y redefine calcularMonto() (polimorfismo).
 * Ejecutar: java -cp bin cochera.prueba.PruebaAlquilerMensual
 */
public class PruebaAlquilerMensual {

    public static void main(String[] args) throws Exception {
        final Verificador v = new Verificador();
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 2, 8, 0);
        Cliente cliente = new Cliente("46587912", "Pedro Salazar Ruiz", "", inicio);
        Tarifa auto = new Tarifa(6, "Auto", 3.00, 200.00);

        System.out.println("=== Regla del 10% (ejemplos del PC_S13) ===");
        Alquiler mensual = new AlquilerMensual("AMX111", auto, inicio, "TK-2026-000001", cliente, null, null);
        v.igual(180.00, mensual.calcularMonto(inicio.plusDays(10)), "Tarifa mensual S/ 200.00 paga S/ 180.00");
        v.igual(200.00, mensual.calcularSubtotal(inicio.plusDays(10)), "El comprobante muestra el subtotal S/ 200.00");
        v.igual(20.00, mensual.calcularDescuento(inicio.plusDays(10)), "Y el descuento de S/ 20.00");
        v.verdadero(mensual.getTipoAlquiler() == TipoAlquiler.MENSUAL, "La modalidad es MENSUAL");
        AlquilerMensual m = (AlquilerMensual) mensual;
        v.verdadero(m.getFechaFin().equals(inicio.plusDays(30)), "Fecha de fin = inicio + 30 dias");
        v.igual(360.00, m.calcularMonto(inicio.plusDays(31)), "Pasado el mes se cobra otro mes: 2 x 180");

        System.out.println("=== Polimorfismo: el cobro por hora no cambia ===");
        Alquiler porHora = new Alquiler("ABC123", auto, inicio, "TK-2026-000002", cliente, null, null);
        v.igual(3.00, porHora.calcularMonto(inicio.plusMinutes(60)), "Auto por hora: 60 min = S/ 3.00");
        v.igual(0.00, porHora.calcularDescuento(inicio.plusMinutes(60)), "El alquiler por hora no tiene descuento");
        Alquiler[] lista = {porHora, mensual};
        double total = 0;
        for (Alquiler a : lista) {
            total += a.calcularMonto(inicio.plusMinutes(60)); // misma llamada, dos comportamientos
        }
        v.igual(183.00, total, "Recorrer Alquiler[] llama a la version de cada clase (3 + 180)");

        System.out.println("=== Alertas del alquiler mensual ===");
        v.verdadero(NivelAlerta.segunFin(inicio.plusDays(5), m.getFechaFin()) == NivelAlerta.MENSUAL_VIGENTE, "Vigente");
        v.verdadero(NivelAlerta.segunFin(inicio.plusDays(28), m.getFechaFin()) == NivelAlerta.MENSUAL_POR_VENCER,
                "Por vencer en los ultimos 3 dias");
        v.verdadero(NivelAlerta.segunFin(inicio.plusDays(30), m.getFechaFin()) == NivelAlerta.MENSUAL_VENCIDO, "Vencido");

        System.out.println("=== Flujo completo: 2 autos del mismo cliente por mes ===");
        final ContextoAplicacion ctx = new ContextoAplicacion(false);
        ctx.getAuth().validarAcceso(DatosIniciales.DNI_CAJERO1, DatosIniciales.CLAVE_DEMO_CAJERO);
        ctx.getTurnos().abrirTurno();
        Alquiler a1 = ctx.getCheckin().registrarEntrada("46587912", "Pedro Salazar Ruiz", "AMX111", 0, 6, TipoAlquiler.MENSUAL);
        Alquiler a2 = ctx.getCheckin().registrarEntrada("46587912", "", "AMX222", 0, 6, TipoAlquiler.MENSUAL);
        v.verdadero(a1 instanceof AlquilerMensual && a2 instanceof AlquilerMensual, "Se crean objetos AlquilerMensual");
        AlquilerMensualServicio srv = ctx.getMensuales();
        List<AlquilerMensual> dePedro = srv.listar("46587912");
        AlquilerMensualServicio.Resumen r = srv.resumir(dePedro);
        v.verdadero(dePedro.size() == 2, "El cliente tiene 2 alquileres mensuales");
        v.igual(360.00, r.getTotal(), "2 autos de S/ 200.00 pagan S/ 360.00 (10% por cada vehiculo)");
        v.igual(40.00, r.getDescuento(), "Descuento total S/ 40.00");

        ctx.getReloj().adelantarMinutos(5 * 24 * 60);
        ResultadoCobro cobro = ctx.getCheckout().registrarPago(a1, MetodoPago.EFECTIVO, 200.0);
        v.igual(180.00, cobro.getPago().getMontoTotal(), "Al salir se cobra el monto mensual con descuento");
        v.igual(20.00, cobro.getVuelto(), "Vuelto de S/ 200.00");
        v.igual(20.00, cobro.getTarifa().getDescuento(), "El detalle del cobro trae el descuento");
        String ticket = new TicketSalida(cobro.getPago()).generar();
        v.verdadero(ticket.contains("Desc. 10%") && ticket.contains("Subtotal"), "El ticket de salida muestra subtotal y descuento");
        v.verdadero(ctx.getVentas().ventasDeCliente("46587912").size() == 1, "La venta queda en el cliente");

        ctx.getAuth().cerrarSesion();
        ctx.getAuth().validarAcceso(DatosIniciales.DNI_ADMIN, DatosIniciales.CLAVE_DEMO_ADMIN);
        Tarifa sinMensual = ctx.getVehiculos().agregarTipoVehiculo("Cuatrimoto", 2.50);
        ctx.getAuth().cerrarSesion();
        ctx.getAuth().validarAcceso(DatosIniciales.DNI_CAJERO1, DatosIniciales.CLAVE_DEMO_CAJERO);
        v.lanza(ValidacionException.class, () -> ctx.getCheckin().registrarEntrada("46587912", "", "CTM333", 0,
                sinMensual.getId(), TipoAlquiler.MENSUAL), "Un tipo sin tarifa mensual no se alquila por mes");

        v.resumen();
        System.exit(v.getFallos() == 0 ? 0 : 1);
    }
}
