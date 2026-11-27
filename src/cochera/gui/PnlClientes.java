package cochera.gui;

import cochera.excepcion.CocheraException;
import cochera.modelo.Cliente;
import cochera.modelo.Venta;
import cochera.servicio.VentaServicio;
import cochera.util.Dinero;
import cochera.util.FechaUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestaña "Clientes" (JPanel Form): CRUD de clientes y sus ventas (relación cliente 1 ── N venta).
 * Al elegir un cliente se listan sus ventas con fecha, placa y monto.
 */
public class PnlClientes extends javax.swing.JPanel implements Refrescable {

    private static final long serialVersionUID = 1L;

    private final transient ContextoGui gui;
    private final ModeloTabla<Cliente> modeloClientes;
    private final ModeloTabla<Venta> modeloVentas;

    public PnlClientes(ContextoGui gui) {
        this.gui = gui;
        initComponents();
        final cochera.datos.BaseDatos bd = gui.getApp().getBaseDatos();
        modeloClientes = new ModeloTabla<Cliente>()
                .columna("DNI", Cliente::getDni)
                .columna("Nombre", Cliente::getNombreCompleto)
                .columna("Teléfono", Cliente::getTelefono)
                .columna("Ventas", c -> bd.ventasDeCliente(c.getDni()).size())
                .columna("Total comprado", c -> Dinero.soles(VentaServicio.total(bd.ventasDeCliente(c.getDni()))))
                .columna("Registrado", c -> FechaUtil.fechaHora(c.getCreadoEn()));
        tblClientes.setModel(modeloClientes);
        Estilo.tabla(tblClientes);
        Estilo.anchos(tblClientes, 90, 220, 110, 70, 120, 130);
        Estilo.derecha(tblClientes, 3, 4);

        modeloVentas = new ModeloTabla<Venta>()
                .columna("Fecha", v -> FechaUtil.fechaHora(v.getFecha()))
                .columna("Código", v -> v.getAlquiler().getCodigoBarras())
                .columna("Placa", v -> v.getAlquiler().getPlacaFormateada())
                .columna("Tipo de vehículo", v -> v.getAlquiler().getTarifa().getTipoVehiculo())
                .columna("Método", v -> v.getPago().getMetodoPago().getEtiqueta())
                .columna("Monto", v -> Dinero.soles(v.getMonto()));
        tblVentas.setModel(modeloVentas);
        Estilo.tabla(tblVentas);
        Estilo.anchos(tblVentas, 130, 140, 90, 200, 90, 100);
        Estilo.derecha(tblVentas, 5);

        Estilo.botonPrimario(btnGuardar);
        Estilo.botonSecundario(btnNuevo);
        Estilo.botonPeligro(btnEliminar);
        UtilForm.alCambiar(txtBuscar, this::cargarClientes);
        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccion();
            }
        });
        limpiarFormulario();
    }

    @Override
    public void refrescar() {
        cargarClientes();
    }

    private void cargarClientes() {
        try {
            List<Cliente> lista = gui.getApp().getClientes().listar(txtBuscar.getText());
            modeloClientes.setFilasConservandoSeleccion(tblClientes, lista);
            lblConteo.setText(lista.size() + " cliente(s)");
        } catch (CocheraException e) {
            modeloClientes.setFilas(new ArrayList<Cliente>());
            lblConteo.setText(e.getMessage());
        }
        mostrarVentas(modeloClientes.getSeleccionado(tblClientes));
    }

    private void mostrarSeleccion() {
        Cliente c = modeloClientes.getSeleccionado(tblClientes);
        if (c == null) {
            mostrarVentas(null);
            return;
        }
        lblModo.setText("Editando a " + c.getNombreCompleto());
        txtDni.setText(c.getDni());
        txtDni.setEditable(false);
        txtNombre.setText(c.getNombreCompleto());
        txtTelefono.setText(c.getTelefono());
        btnEliminar.setEnabled(true);
        mostrarVentas(c);
    }

    /** JOIN cliente - venta: todas las ventas del cliente elegido. */
    private void mostrarVentas(Cliente c) {
        if (c == null) {
            modeloVentas.setFilas(new ArrayList<Venta>());
            lblVentasTotal.setText("Selecciona un cliente para ver sus ventas.");
            return;
        }
        try {
            List<Venta> ventas = gui.getApp().getVentas().ventasDeCliente(c.getDni());
            modeloVentas.setFilas(ventas);
            lblVentasTotal.setText(c.getNombreCompleto() + ": " + ventas.size() + " venta(s)  ·  Total "
                    + Dinero.soles(VentaServicio.total(ventas)));
        } catch (CocheraException e) {
            lblVentasTotal.setText(e.getMessage());
        }
    }

    private void limpiarFormulario() {
        tblClientes.clearSelection();
        lblModo.setText("Nuevo cliente");
        txtDni.setText("");
        txtDni.setEditable(true);
        txtNombre.setText("");
        txtTelefono.setText("");
        btnEliminar.setEnabled(false);
        mostrarVentas(null);
        txtDni.requestFocusInWindow();
    }

    /**
     * Código generado por el editor visual de NetBeans a partir de PnlClientes.form.
     * NO modificar a mano: se edita desde la vista Design.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlListado = new javax.swing.JPanel();
        pnlFiltro = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        lblConteo = new javax.swing.JLabel();
        scrClientes = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        lblVentasTitulo = new javax.swing.JLabel();
        scrVentas = new javax.swing.JScrollPane();
        tblVentas = new javax.swing.JTable();
        lblVentasTotal = new javax.swing.JLabel();
        pnlEdicion = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblModo = new javax.swing.JLabel();
        lblDni = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        lblNota = new javax.swing.JLabel();
        lblRelleno = new javax.swing.JLabel();

        setBackground(new java.awt.Color(244, 244, 245));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setLayout(new java.awt.BorderLayout(12, 0));

        pnlListado.setBackground(new java.awt.Color(244, 244, 245));
        pnlListado.setLayout(new java.awt.GridBagLayout());

        pnlFiltro.setBackground(new java.awt.Color(255, 255, 255));
        pnlFiltro.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(228, 228, 231)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        pnlFiltro.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 6));

        lblBuscar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblBuscar.setText("Buscar (DNI o nombre):");
        pnlFiltro.add(lblBuscar);

        txtBuscar.setColumns(16);
        txtBuscar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        pnlFiltro.add(txtBuscar);

        lblConteo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblConteo.setForeground(new java.awt.Color(113, 113, 122));
        lblConteo.setText(" ");
        pnlFiltro.add(lblConteo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlListado.add(pnlFiltro, gridBagConstraints);

        scrClientes.setViewportView(tblClientes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.55;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        pnlListado.add(scrClientes, gridBagConstraints);

        lblVentasTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblVentasTitulo.setText("Ventas del cliente seleccionado (JOIN cliente - venta)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        pnlListado.add(lblVentasTitulo, gridBagConstraints);

        scrVentas.setViewportView(tblVentas);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.45;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlListado.add(scrVentas, gridBagConstraints);

        lblVentasTotal.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblVentasTotal.setForeground(new java.awt.Color(180, 83, 9));
        lblVentasTotal.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlListado.add(lblVentasTotal, gridBagConstraints);

        add(pnlListado, java.awt.BorderLayout.CENTER);

        pnlEdicion.setBackground(new java.awt.Color(255, 255, 255));
        pnlEdicion.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(228, 228, 231)), javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        pnlEdicion.setPreferredSize(new java.awt.Dimension(330, 100));
        pnlEdicion.setLayout(new java.awt.GridBagLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblTitulo.setText("Cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlEdicion.add(lblTitulo, gridBagConstraints);

        lblModo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblModo.setForeground(new java.awt.Color(180, 83, 9));
        lblModo.setText("Nuevo cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlEdicion.add(lblModo, gridBagConstraints);

        lblDni.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblDni.setText("DNI (8 dígitos)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 0, 0, 0);
        pnlEdicion.add(lblDni, gridBagConstraints);

        txtDni.setColumns(10);
        txtDni.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtDni, gridBagConstraints);

        lblNombre.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNombre.setText("Nombre completo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblNombre, gridBagConstraints);

        txtNombre.setColumns(10);
        txtNombre.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtNombre, gridBagConstraints);

        lblTelefono.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTelefono.setText("Teléfono (opcional)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblTelefono, gridBagConstraints);

        txtTelefono.setColumns(10);
        txtTelefono.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtTelefono, gridBagConstraints);

        btnGuardar.setText("Guardar");
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 0, 0);
        pnlEdicion.add(btnGuardar, gridBagConstraints);

        btnNuevo.setText("Nuevo cliente (limpiar)");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        pnlEdicion.add(btnNuevo, gridBagConstraints);

        btnEliminar.setText("Eliminar cliente");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        pnlEdicion.add(btnEliminar, gridBagConstraints);

        lblNota.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblNota.setForeground(new java.awt.Color(113, 113, 122));
        lblNota.setText("<html><body style=\"width:215px\">El DNI es la llave del cliente y no se puede cambiar. No se puede eliminar un cliente que ya tiene ventas o alquileres (llave foránea). Los clientes también se registran solos al hacer una entrada con un DNI nuevo.</body></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        pnlEdicion.add(lblNota, gridBagConstraints);

        lblRelleno.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRelleno.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.weighty = 1.0;
        pnlEdicion.add(lblRelleno, gridBagConstraints);

        add(pnlEdicion, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        cargarClientes();
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        final boolean nuevo = txtDni.isEditable();
        final String dni = txtDni.getText();
        final String nombre = txtNombre.getText();
        final String telefono = txtTelefono.getText();
        if (gui.ejecutar(this, () -> {
            Cliente c = nuevo
                    ? gui.getApp().getClientes().registrar(dni, nombre, telefono)
                    : gui.getApp().getClientes().actualizar(dni, nombre, telefono);
            Dialogos.info(this, (nuevo ? "Cliente registrado: " : "Cliente actualizado: ") + c);
        })) {
            limpiarFormulario();
            cargarClientes();
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        final Cliente c = modeloClientes.getSeleccionado(tblClientes);
        if (c == null || !Dialogos.confirmar(this, "¿Eliminar al cliente " + c + "?")) {
            return;
        }
        if (gui.ejecutar(this, () -> gui.getApp().getClientes().eliminar(c.getDni()))) {
            limpiarFormulario();
            cargarClientes();
            Dialogos.info(this, "Cliente eliminado.");
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblConteo;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblModo;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNota;
    private javax.swing.JLabel lblRelleno;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblVentasTitulo;
    private javax.swing.JLabel lblVentasTotal;
    private javax.swing.JPanel pnlEdicion;
    private javax.swing.JPanel pnlFiltro;
    private javax.swing.JPanel pnlListado;
    private javax.swing.JScrollPane scrClientes;
    private javax.swing.JScrollPane scrVentas;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTable tblVentas;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
