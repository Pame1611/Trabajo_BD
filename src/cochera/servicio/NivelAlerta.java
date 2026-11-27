package cochera.servicio;

/**
 * Semáforo de tiempo en "Autos dentro": avisa cuando un vehículo se acerca o pasa las 12 h.
 */
public enum NivelAlerta {
    NORMAL("OK"),
    POR_VENCER("POR VENCER"),
    EXCEDIDO("+12H"),
    MENSUAL_VIGENTE("MENSUAL"),
    MENSUAL_POR_VENCER("POR VENCER (MES)"),
    MENSUAL_VENCIDO("+MES VENCIDO");

    /** Días antes del fin del mes en que se avisa que el alquiler mensual está por vencer. */
    public static final int DIAS_AVISO_MENSUAL = 3;

    private final String etiqueta;

    NivelAlerta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Alerta del alquiler mensual según su fecha de fin. */
    public static NivelAlerta segunFin(java.time.LocalDateTime ahora, java.time.LocalDateTime fin) {
        if (!ahora.isBefore(fin)) {
            return MENSUAL_VENCIDO;
        }
        if (!ahora.isBefore(fin.minusDays(DIAS_AVISO_MENSUAL))) {
            return MENSUAL_POR_VENCER;
        }
        return MENSUAL_VIGENTE;
    }

    public static NivelAlerta segunHoras(double horas) {
        if (horas >= 12) {
            return EXCEDIDO;
        }
        if (horas >= 10) {
            return POR_VENCER;
        }
        return NORMAL;
    }
}
