package cochera.modelo;

/**
 * Marca de vehículo (tabla marca). Una marca tiene varios modelos (marca 1 ── N modelo).
 * La marca "Desconocido" se usa cuando el cliente no sabe o no se ve la marca.
 */
public class Marca {

    public static final String DESCONOCIDO = "Desconocido";

    private final int id;
    private final String nombre;

    public Marca(int id, String nombre) {
        this.id = id;
        this.nombre = nombre.trim();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esDesconocida() {
        return DESCONOCIDO.equalsIgnoreCase(nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
