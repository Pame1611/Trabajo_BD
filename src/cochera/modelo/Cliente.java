package cochera.modelo;

import java.time.LocalDateTime;

/**
 * Cliente de la cochera (tabla cliente). Se identifica por su DNI de 8 dígitos (llave primaria).
 *
 * Por la Ley 29733 de datos personales solo se guarda lo necesario para identificar
 * su alquiler: DNI, nombre y, si lo da, un teléfono.
 */
public class Cliente {

    private final String dni;
    private final LocalDateTime creadoEn;
    private String nombreCompleto;
    private String telefono;

    public Cliente(String dni, String nombreCompleto, String telefono, LocalDateTime creadoEn) {
        this.dni = dni.trim();
        this.creadoEn = creadoEn;
        actualizar(nombreCompleto, telefono);
    }

    /** El DNI no cambia (es la llave); solo se corrigen nombre y teléfono. */
    public final void actualizar(String nuevoNombre, String nuevoTelefono) {
        this.nombreCompleto = nuevoNombre == null ? "" : nuevoNombre.trim();
        this.telefono = nuevoTelefono == null ? "" : nuevoTelefono.trim();
    }

    public String getDni() {
        return dni;
    }

    public String getNombreCompleto() {
        return nombreCompleto.isEmpty() ? "Cliente " + dni : nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    @Override
    public String toString() {
        return dni + " - " + getNombreCompleto();
    }
}
