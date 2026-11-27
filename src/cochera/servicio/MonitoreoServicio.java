package cochera.servicio;

import cochera.datos.BaseDatos;
import cochera.excepcion.AccesoDenegadoException;
import cochera.excepcion.CocheraException;
import cochera.modelo.Alquiler;
import cochera.modelo.AlquilerMensual;
import cochera.modelo.Tarifa;
import cochera.modelo.Usuario;
import cochera.util.FechaUtil;
import cochera.util.Placa;
import cochera.util.Reloj;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Inventario en vivo de vehículos dentro de la cochera (app/autos-dentro).
 */
public class MonitoreoServicio {

    private final BaseDatos bd;
    private final Reloj reloj;
    private final AutenticacionServicio auth;
    private final TurnoServicio turnos;
    private final CalculadoraTarifa calculadora;

    public MonitoreoServicio(BaseDatos bd, Reloj reloj, AutenticacionServicio auth, TurnoServicio turnos,
                             CalculadoraTarifa calculadora) {
        this.bd = bd;
        this.reloj = reloj;
        this.auth = auth;
        this.turnos = turnos;
        this.calculadora = calculadora;
    }

    /** ADMIN ve siempre; el EMPLEADO necesita turno abierto. Ordenado del ingreso más reciente al más antiguo. */
    public List<Alquiler> listarAutosDentro() throws CocheraException {
        Usuario u = auth.requerirSesion();
        if (!u.esAdministrador() && turnos.obtenerTurnoAbierto() == null) {
            throw new AccesoDenegadoException("Debes abrir tu turno para ver los autos dentro.");
        }
        List<Alquiler> lista = bd.listarTicketsEnCurso();
        lista.sort(Comparator.comparing(Alquiler::getHoraEntrada).reversed());
        return lista;
    }

    /** Filtra por texto de placa y/o por tipo de vehículo (null = todos). */
    public List<Alquiler> filtrar(List<Alquiler> lista, String textoPlaca, Tarifa tipo) {
        String f = Placa.normalizar(textoPlaca);
        List<Alquiler> resultado = new ArrayList<>();
        for (Alquiler t : lista) {
            boolean coincidePlaca = f.isEmpty() || t.getPlaca().contains(f);
            boolean coincideTipo = tipo == null || t.getTarifa().getId() == tipo.getId();
            if (coincidePlaca && coincideTipo) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public String tiempoTranscurrido(Alquiler t) {
        return FechaUtil.tiempoTranscurrido(t.getHoraEntrada(), reloj.ahora());
    }

    /** Por hora: alerta a las 10 h y 12 h. Por mes: alerta 3 días antes del fin y al vencer. */
    public NivelAlerta nivelAlerta(Alquiler t) {
        if (t instanceof AlquilerMensual) {
            return NivelAlerta.segunFin(reloj.ahora(), ((AlquilerMensual) t).getFechaFin());
        }
        double horas = Duration.between(t.getHoraEntrada(), reloj.ahora()).getSeconds() / 3600.0;
        return NivelAlerta.segunHoras(horas);
    }

    /** Lo que pagaría si saliera en este momento. */
    public double montoEstimado(Alquiler t) {
        return calculadora.calcular(t, reloj.ahora()).getTarifaTotal();
    }
}
