package cochera.modelo;

/**
 * Modelo de vehículo (tabla modelo). Pertenece a una marca (modelo.marca_id es llave foránea)
 * y tiene su tipo de vehículo (modelo.tipo_vehiculo_id), del que sale la tarifa.
 *
 * Cada marca tiene un modelo "Desconocido" sin tipo: en ese caso el empleado elige el tipo.
 */
public class Modelo {

    private final int id;
    private final String nombre;
    private final Marca marca;
    private final Tarifa tipoVehiculo;

    public Modelo(int id, String nombre, Marca marca, Tarifa tipoVehiculo) {
        if (marca == null) {
            throw new IllegalArgumentException("Todo modelo pertenece a una marca.");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.marca = marca;
        this.tipoVehiculo = tipoVehiculo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Marca getMarca() {
        return marca;
    }

    /** Tipo de vehículo del modelo; null en los modelos "Desconocido". */
    public Tarifa getTipoVehiculo() {
        return tipoVehiculo;
    }

    public boolean esDesconocido() {
        return Marca.DESCONOCIDO.equalsIgnoreCase(nombre);
    }

    /** "Toyota Yaris", o "Desconocido" si no se conoce ni la marca ni el modelo. */
    public String getDescripcion() {
        if (marca.esDesconocida()) {
            return Marca.DESCONOCIDO;
        }
        return esDesconocido() ? marca.getNombre() + " (modelo desconocido)" : marca.getNombre() + " " + nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
