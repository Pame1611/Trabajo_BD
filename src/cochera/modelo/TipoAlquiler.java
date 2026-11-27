package cochera.modelo;

/** Modalidad del alquiler: por hora (se cobra por minuto) o por mes (10% de descuento por vehículo). */
public enum TipoAlquiler {
    POR_HORA("Por hora (se cobra por minuto)"),
    MENSUAL("Por mes (10% de descuento)");

    private final String etiqueta;

    TipoAlquiler(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
