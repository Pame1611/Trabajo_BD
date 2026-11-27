package cochera.prueba;

import cochera.ContextoAplicacion;
import cochera.datos.DatosIniciales;
import cochera.dto.CierreGeneral;
import cochera.dto.CotizacionSalida;
import cochera.dto.FilaExport;
import cochera.dto.KpiHoy;
import cochera.dto.ResultadoCobro;
import cochera.excepcion.AccesoDenegadoException;
import cochera.excepcion.ValidacionException;
import cochera.modelo.Alquiler;
import cochera.modelo.EstadoTicket;
import cochera.modelo.MetodoPago;
import cochera.modelo.Pago;
import cochera.modelo.TurnoCaja;
import cochera.modelo.Venta;
import cochera.servicio.AdminServicio;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Recorre el flujo completo usando solo los servicios (sin menús):
 * login con DNI, turnos, entrada con cliente, salida con traslape de turnos, vuelto,
 * ventas por cliente, arqueo y reportes del admin.
 * Ejecutar: java -cp bin cochera.prueba.PruebaFlujoOperativo
 */
public class PruebaFlujoOperativo {

    private static final String CAJERO1 = DatosIniciales.DNI_CAJERO1;
    private static final String CAJERO2 = DatosIniciales.DNI_CAJERO2;
    private static final String ADMIN = DatosIniciales.DNI_ADMIN;
    private static final String JUAN = "45678912";
    private static final String ROSA = "47851236";

    public static void main(String[] args) throws Exception {
        final Verificador v = new Verificador();
        final ContextoAplicacion ctx = new ContextoAplicacion(false);

        System.out.println("=== Acceso con DNI (ValidarAcceso) y turnos ===");
        v.lanza(ValidacionException.class, () -> ctx.getAuth().validarAcceso("", ""), "Campos vacios rechazados");
        v.lanza(ValidacionException.class, () -> ctx.getAuth().validarAcceso("2222", "cajero123"),
                "DNI que no tiene 8 digitos rechazado");
        v.lanza(AccesoDenegadoException.class, () -> ctx.getAuth().validarAcceso(CAJERO1, "malaclave"),
                "Clave incorrecta rechazada");
        v.verdadero(ctx.getAuth().nombrePorDni(CAJERO1).equals("Carlos Ramírez"), "Al escribir el DNI se ve el nombre");
        ctx.getAuth().validarAcceso(ADMIN, "admin123");
        v.lanza(AccesoDenegadoException.class, () -> ctx.getTurnos().abrirTurno(), "El ADMIN no puede abrir turno");
        ctx.getAuth().cerrarSesion();

        ctx.getAuth().validarAcceso(CAJERO1, "cajero123");
        v.lanza(AccesoDenegadoException.class, () -> ctx.getCheckin().registrarEntrada(JUAN, "Juan", "ABC123", 0, 6),
                "Sin turno abierto no hay entrada");
        TurnoCaja turno1 = ctx.getTurnos().abrirTurno();
        v.lanza(ValidacionException.class, () -> ctx.getTurnos().abrirTurno(), "No se abren dos turnos a la vez");
        v.lanza(AccesoDenegadoException.class, () -> ctx.getAdmin().obtenerKpiHoy(), "El cajero no ve el panel admin");

        System.out.println("=== Entrada con DNI del cliente ===");
        v.lanza(ValidacionException.class, () -> ctx.getCheckin().registrarEntrada("123", "", "ABC123", 0, 6),
                "DNI de cliente invalido rechazado");
        Alquiler abc = ctx.getCheckin().registrarEntrada(JUAN, "Juan Pérez Rojas", "abc-123", 0, 6);
        v.verdadero(abc.getPlaca().equals("ABC123") && abc.getCodigoBarras().matches("TK-\\d{4}-000001"),
                "Placa normalizada y codigo " + abc.getCodigoBarras());
        v.verdadero(ctx.getClientes().buscar(JUAN) != null && abc.getCliente().getDni().equals(JUAN),
                "El cliente nuevo se registra y queda en el alquiler");
        v.verdadero(abc.getEmpleado().getDni().equals(CAJERO1), "El alquiler guarda el DNI del empleado");
        v.verdadero(abc.getHoraSalida() == null, "La hora de salida queda vacia hasta la salida");
        v.lanza(ValidacionException.class, () -> ctx.getCheckin().registrarEntrada(JUAN, "", "ABC123", 0, 6),
                "Placa duplicada EN_CURSO rechazada");
        v.lanza(ValidacionException.class, () -> ctx.getCheckin().registrarEntrada(JUAN, "", "A1", 0, 6), "Placa demasiado corta");
        v.lanza(ValidacionException.class, () -> ctx.getCheckin().registrarEntrada(JUAN, "", "QWE111", 0, 99),
                "Tipo de vehiculo invalido");

        System.out.println("=== Paso del tiempo: 13 horas ===");
        ctx.getReloj().adelantarMinutos(13 * 60);
        CotizacionSalida cot = ctx.getCheckout().buscar("abc123");
        v.igual(39.00, cot.getTarifa().getTarifaTotal(), "Auto 13 h = 780 min x 3.00 / 60");
        v.verdadero(ctx.getCheckout().sugerirPlacas("AB").contains("ABC-123"), "Autocompletado de placas");
        v.verdadero(ctx.getCheckout().alquileresEnCursoDeCliente(JUAN).contains(abc), "Busqueda de alquileres por DNI");

        System.out.println("=== Traslape de turnos: cajero1 registra, cajero2 cobra ===");
        Alquiler xyz = ctx.getCheckin().registrarEntrada(ROSA, "Rosa Díaz Vega", "XYZ789", 0, 6);
        ctx.getReloj().adelantarMinutos(40);
        ctx.getAuth().cerrarSesion();
        ctx.getAuth().validarAcceso(CAJERO2, "cajero123");
        TurnoCaja turno2 = ctx.getTurnos().abrirTurno();
        ResultadoCobro cobroXyz = ctx.getCheckout().registrarPago(
                ctx.getCheckout().buscar(xyz.getCodigoBarras()).getTicket(), MetodoPago.YAPE, null);
        v.igual(2.00, cobroXyz.getPago().getMontoTotal(), "Auto 40 min = S/ 2.00");
        v.verdadero(cobroXyz.getPago().getTurno() == turno2, "El pago queda en el turno de quien COBRA (cajero2)");
        v.lanza(ValidacionException.class, () -> ctx.getCheckout().buscar(xyz.getCodigoBarras()),
                "Ticket ya cobrado no se cobra dos veces");
        v.lanza(ValidacionException.class, () -> ctx.getCheckout().alquileresEnCursoDeCliente(ROSA),
                "Rosa ya no tiene vehiculos dentro");
        ctx.getAuth().cerrarSesion();

        System.out.println("=== Cobro en efectivo con vuelto ===");
        ctx.getAuth().validarAcceso(CAJERO1, "cajero123");
        v.lanza(ValidacionException.class,
                () -> ctx.getCheckout().registrarPago(abc, MetodoPago.EFECTIVO, 5.0), "Monto recibido insuficiente");
        v.verdadero(abc.estaEnCurso(), "Tras el error el ticket sigue EN_CURSO");
        ResultadoCobro cobroAbc = ctx.getCheckout().registrarPago(abc, MetodoPago.EFECTIVO, 50.0);
        v.igual(41.00, cobroAbc.getPago().getMontoTotal(), "Auto 13 h 40 min = 820 min x 3.00 / 60");
        v.igual(9.00, cobroAbc.getVuelto(), "Vuelto de S/ 50.00 sobre S/ 41.00");
        v.verdadero(abc.getEstado() == EstadoTicket.PAGADO && abc.getHoraSalida() != null, "Ticket PAGADO con hora de salida");

        System.out.println("=== Ventas por cliente (cliente 1 - N venta) ===");
        List<Venta> ventasJuan = ctx.getVentas().ventasDeCliente(JUAN);
        v.verdadero(ventasJuan.size() == 1 && ventasJuan.get(0).getAlquiler() == abc, "La venta queda vinculada a Juan");
        v.igual(41.00, ventasJuan.get(0).getMonto(), "Monto de la venta de Juan");
        v.lanza(IllegalArgumentException.class, () -> new Venta(null, abc, cobroAbc.getPago()),
                "No se puede crear una venta sin cliente");
        v.lanza(ValidacionException.class, () -> ctx.getClientes().registrar(JUAN, "Otro", ""),
                "No se repite el DNI de un cliente");

        System.out.println("=== Arqueo y cierre de turno ===");
        v.igual(-1.00, ctx.getTurnos().previsualizarDiferencia(turno1, 40.0), "Vista previa: falta S/ 1.00");
        v.lanza(ValidacionException.class, () -> ctx.getTurnos().cerrarTurno(-1), "Efectivo negativo rechazado");
        ctx.getTurnos().cerrarTurno(41.0);
        v.igual(0.00, turno1.getDiferenciaCaja(), "Caja cuadrada al declarar S/ 41.00");
        v.lanza(AccesoDenegadoException.class,
                () -> ctx.getCobros().cambiarMetodoPago(cobroAbc.getPago(), MetodoPago.PLIN),
                "Con el turno cerrado el cajero ya no corrige pagos");
        ctx.getAuth().cerrarSesion();

        System.out.println("=== Concurrencia: 2 cajeros registran 40 entradas a la vez ===");
        ctx.getAuth().validarAcceso(CAJERO2, "cajero123");
        final List<String> codigos = Collections.synchronizedList(new ArrayList<String>());
        Thread[] hilos = new Thread[2];
        for (int h = 0; h < hilos.length; h++) {
            final int base = h * 20;
            hilos[h] = new Thread(() -> {
                for (int i = 0; i < 20; i++) {
                    try {
                        String dni = String.format("%08d", 40000000 + base + i);
                        codigos.add(ctx.getCheckin().registrarEntrada(dni, "", String.format("P%05d", base + i), 0, 1)
                                .getCodigoBarras());
                    } catch (Exception e) {
                        codigos.add("ERROR " + e.getMessage());
                    }
                }
            });
            hilos[h].start();
        }
        for (Thread t : hilos) {
            t.join();
        }
        Set<String> unicos = new HashSet<>(codigos);
        int errores = 0;
        for (String c : codigos) {
            if (c.startsWith("ERROR")) {
                errores++;
                System.out.println("     " + c);
            }
        }
        v.verdadero(errores == 0, "Ninguna entrada fallo (" + errores + " errores)");
        v.verdadero(codigos.size() == 40 && unicos.size() == 40, "40 codigos de ticket, todos distintos");
        ctx.getAuth().cerrarSesion();

        System.out.println("=== Panel del administrador ===");
        ctx.getAuth().validarAcceso(ADMIN, "admin123");
        AdminServicio admin = ctx.getAdmin();
        KpiHoy kpi = admin.obtenerKpiHoy();
        v.igual(43.00, kpi.getIngresosHoy(), "Ingresos de hoy (41 efectivo + 2 Yape)");
        v.verdadero(kpi.getAutosDentro() == 40, "Autos dentro = 40 (obtenido " + kpi.getAutosDentro() + ")");
        v.verdadero(admin.obtenerTurnosAbiertos().size() == 1, "Un trabajador en turno (cajero2)");
        v.verdadero(admin.obtenerTurnosCerrados(null, null, 1).getTotal() == 1, "Un turno cerrado en auditoria");

        LocalDateTime horaPago = cobroAbc.getPago().getCreadoEn();
        LocalDate fechaCorte = horaPago.getHour() >= 14 ? horaPago.toLocalDate().plusDays(1) : horaPago.toLocalDate();
        CierreGeneral cierre = admin.obtenerCierreGeneral(fechaCorte);
        v.igual(43.00, cierre.getResumen().getTotal(), "Cierre 2 PM del " + fechaCorte + " recauda S/ 43.00");
        v.verdadero(cierre.getTurnos().size() == 2, "El cierre incluye los 2 turnos que cobraron");

        List<FilaExport> filas = admin.obtenerFilasExport(null, null, null, "Maria Torres");
        v.verdadero(filas.size() == 1 && filas.get(0).getPlaca().equals("XYZ-789"), "Filtro de exportacion por empleado");
        File carpeta = Files.createTempDirectory("cochera").toFile();
        File csv = new cochera.servicio.ExportadorCSV(carpeta).exportarVentas(admin.obtenerFilasExport(null, null, null, null));
        v.verdadero(csv.exists() && Files.readAllLines(csv.toPath()).size() == 5, "CSV generado: " + csv.getName());

        System.out.println("=== CRUD de clientes (administrador) ===");
        ctx.getClientes().registrar("70000001", "Cliente Temporal", "999888777");
        ctx.getClientes().actualizar("70000001", "Cliente Corregido", "");
        v.verdadero(ctx.getClientes().buscar("70000001").getNombreCompleto().equals("Cliente Corregido"),
                "Actualizar el nombre de un cliente");
        ctx.getClientes().eliminar("70000001");
        v.verdadero(ctx.getClientes().buscar("70000001") == null, "Eliminar un cliente sin ventas");
        v.lanza(ValidacionException.class, () -> ctx.getClientes().eliminar(JUAN),
                "No se elimina un cliente con ventas (llave foranea)");

        Pago pagoAbc = cobroAbc.getPago();
        ctx.getCobros().cambiarMetodoPago(pagoAbc, MetodoPago.PLIN);
        v.verdadero(pagoAbc.getMetodoPago() == MetodoPago.PLIN, "El admin corrige el metodo de pago");
        admin.anularTicket(abc);
        v.verdadero(abc.getEstado() == EstadoTicket.ANULADO, "El admin anula un ticket");
        v.lanza(ValidacionException.class, () -> admin.anularTicket(abc), "No se anula dos veces");
        v.lanza(ValidacionException.class, () -> admin.actualizarTarifa(6, -1), "Tarifa negativa rechazada");
        v.igual(4.50, admin.actualizarTarifa(6, 4.5).getPrecioHora(), "Tarifa de Auto actualizada a S/ 4.50 por hora");

        v.resumen();
        System.exit(v.getFallos() == 0 ? 0 : 1);
    }
}
