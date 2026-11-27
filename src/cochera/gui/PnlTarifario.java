package cochera.gui;

import cochera.excepcion.ValidacionException;
import cochera.modelo.Tarifa;
import cochera.util.Dinero;

/**
 * Pestaña "Tarifario" (JPanel Form): el administrador cambia la tarifa por hora de cada tipo
 * de vehículo y agrega tipos nuevos.
 */
public class PnlTarifario extends javax.swing.JPanel implements Refrescable {

    private static final long serialVersionUID = 1L;

    private final transient ContextoGui gui;
    private final ModeloTabla<Tarifa> modelo;

    public PnlTarifario(ContextoGui gui) {
        this.gui = gui;
        initComponents();
        modelo = new ModeloTabla<Tarifa>()
                .columna("ID", Tarifa::getId)
                .columna("Tipo de vehículo", Tarifa::getTipoVehiculo)
                .columna("Tarifa por hora", t -> Dinero.soles(t.getPrecioHora()))
                .columna("Por minuto", t -> "S/ " + String.format(java.util.Locale.US, "%.4f", t.getPrecioMinuto()))
                .columna("Tarifa mensual", t -> Dinero.soles(t.getTarifaMensual()))
                .columna("Mensual -10%", t -> Dinero.soles(Dinero.redondear(t.getTarifaMensual() * 0.90)));
        tblTarifas.setModel(modelo);
        Estilo.tabla(tblTarifas);
        Estilo.anchos(tblTarifas, 40, 230, 110, 100, 120, 110);
        Estilo.derecha(tblTarifas, 2, 3, 4, 5);
        Estilo.botonPrimario(btnGuardar);
        Estilo.botonPrimario(btnAgregar);
        tblTarifas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccion();
            }
        });
        mostrarSeleccion();
    }

    @Override
    public void refrescar() {
        gui.ejecutar(this, () -> modelo.setFilasConservandoSeleccion(tblTarifas, gui.getApp().getAdmin().obtenerTarifario()));
    }

    private void mostrarSeleccion() {
        Tarifa t = modelo.getSeleccionado(tblTarifas);
        boolean hay = t != null;
        lblSeleccion.setText(hay ? t.getTipoVehiculo() : "Selecciona un tipo de vehículo en la tabla.");
        txtBase.setText(hay ? Dinero.decimal(t.getPrecioHora()) : "");
        txtBase.setEnabled(hay);
        txtMensual.setText(hay ? Dinero.decimal(t.getTarifaMensual()) : "");
        txtMensual.setEnabled(hay);
        btnGuardar.setEnabled(hay);
    }

    /**
     * Código generado por el editor visual de NetBeans a partir de PnlTarifario.form.
     * NO modificar a mano: se edita desde la vista Design.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrTarifas = new javax.swing.JScrollPane();
        tblTarifas = new javax.swing.JTable();
        pnlEdicion = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSeleccion = new javax.swing.JLabel();
        lblBase = new javax.swing.JLabel();
        txtBase = new javax.swing.JTextField();
        lblMensual = new javax.swing.JLabel();
        txtMensual = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        lblNuevoTitulo = new javax.swing.JLabel();
        lblNuevoNombre = new javax.swing.JLabel();
        txtNuevoNombre = new javax.swing.JTextField();
        lblNuevoPrecio = new javax.swing.JLabel();
        txtNuevoPrecio = new javax.swing.JTextField();
        lblNuevoMensual = new javax.swing.JLabel();
        txtNuevoMensual = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JButton();
        lblNota = new javax.swing.JLabel();
        lblRelleno = new javax.swing.JLabel();

        setBackground(new java.awt.Color(244, 244, 245));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setLayout(new java.awt.BorderLayout(12, 0));

        scrTarifas.setViewportView(tblTarifas);

        add(scrTarifas, java.awt.BorderLayout.CENTER);

        pnlEdicion.setBackground(new java.awt.Color(255, 255, 255));
        pnlEdicion.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(228, 228, 231)), javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        pnlEdicion.setPreferredSize(new java.awt.Dimension(330, 100));
        pnlEdicion.setLayout(new java.awt.GridBagLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblTitulo.setText("Editar tarifa");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlEdicion.add(lblTitulo, gridBagConstraints);

        lblSeleccion.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSeleccion.setForeground(new java.awt.Color(180, 83, 9));
        lblSeleccion.setText("Selecciona un tipo de vehículo en la tabla.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlEdicion.add(lblSeleccion, gridBagConstraints);

        lblBase.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblBase.setText("Tarifa por hora (S/)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 0, 0, 0);
        pnlEdicion.add(lblBase, gridBagConstraints);

        txtBase.setColumns(8);
        txtBase.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtBase, gridBagConstraints);

        lblMensual.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblMensual.setText("Tarifa mensual (S/)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblMensual, gridBagConstraints);

        txtMensual.setColumns(8);
        txtMensual.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtMensual, gridBagConstraints);

        btnGuardar.setText("Guardar cambios");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        pnlEdicion.add(btnGuardar, gridBagConstraints);

        lblNuevoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblNuevoTitulo.setText("Agregar tipo de vehículo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(26, 0, 0, 0);
        pnlEdicion.add(lblNuevoTitulo, gridBagConstraints);

        lblNuevoNombre.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNuevoNombre.setText("Nombre del tipo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblNuevoNombre, gridBagConstraints);

        txtNuevoNombre.setColumns(8);
        txtNuevoNombre.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtNuevoNombre, gridBagConstraints);

        lblNuevoPrecio.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNuevoPrecio.setText("Tarifa por hora (S/)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblNuevoPrecio, gridBagConstraints);

        txtNuevoPrecio.setColumns(8);
        txtNuevoPrecio.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtNuevoPrecio, gridBagConstraints);

        lblNuevoMensual.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNuevoMensual.setText("Tarifa mensual (S/)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblNuevoMensual, gridBagConstraints);

        txtNuevoMensual.setColumns(8);
        txtNuevoMensual.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtNuevoMensual, gridBagConstraints);

        btnAgregar.setText("Agregar tipo");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        pnlEdicion.add(btnAgregar, gridBagConstraints);

        lblNota.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblNota.setForeground(new java.awt.Color(113, 113, 122));
        lblNota.setText("<html><body style=\"width:215px\">Regla de cobro: minutos de permanencia x tarifa por hora / 60. Ej.: Camioneta a S/ 5.00 por hora, 90 min = S/ 7.50. El alquiler mensual cobra la tarifa mensual con 10% de descuento por vehículo.</body></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        pnlEdicion.add(lblNota, gridBagConstraints);

        lblRelleno.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRelleno.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.weighty = 1.0;
        pnlEdicion.add(lblRelleno, gridBagConstraints);

        add(pnlEdicion, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        final Tarifa t = modelo.getSeleccionado(tblTarifas);
        if (t == null) {
            return;
        }
        if (gui.ejecutar(this, () -> {
            Double precio = UtilForm.monto(txtBase.getText(), "tarifa por hora");
            Double mensual = UtilForm.monto(txtMensual.getText(), "tarifa mensual");
            if (precio == null || mensual == null) {
                throw new ValidacionException("Escribe la tarifa por hora y la tarifa mensual.");
            }
            gui.getApp().getAdmin().actualizarTarifa(t.getId(), precio, mensual);
        })) {
            refrescar();
            Dialogos.info(this, "Tarifa actualizada: " + t);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        final String nombre = txtNuevoNombre.getText();
        final String precioTexto = txtNuevoPrecio.getText();
        final String mensualTexto = txtNuevoMensual.getText();
        if (gui.ejecutar(this, () -> {
            Double precio = UtilForm.monto(precioTexto, "tarifa por hora");
            Double mensual = UtilForm.monto(mensualTexto, "tarifa mensual");
            if (precio == null) {
                throw new ValidacionException("Escribe la tarifa por hora del nuevo tipo.");
            }
            Tarifa nueva = gui.getApp().getVehiculos().agregarTipoVehiculo(nombre, precio, mensual == null ? 0 : mensual);
            Dialogos.info(this, "Tipo de vehículo agregado: " + nueva);
        })) {
            txtNuevoNombre.setText("");
            txtNuevoPrecio.setText("");
            txtNuevoMensual.setText("");
            refrescar();
        }
    }//GEN-LAST:event_btnAgregarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JLabel lblBase;
    private javax.swing.JLabel lblMensual;
    private javax.swing.JLabel lblNota;
    private javax.swing.JLabel lblNuevoMensual;
    private javax.swing.JLabel lblNuevoNombre;
    private javax.swing.JLabel lblNuevoPrecio;
    private javax.swing.JLabel lblNuevoTitulo;
    private javax.swing.JLabel lblRelleno;
    private javax.swing.JLabel lblSeleccion;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlEdicion;
    private javax.swing.JScrollPane scrTarifas;
    private javax.swing.JTable tblTarifas;
    private javax.swing.JTextField txtBase;
    private javax.swing.JTextField txtMensual;
    private javax.swing.JTextField txtNuevoMensual;
    private javax.swing.JTextField txtNuevoNombre;
    private javax.swing.JTextField txtNuevoPrecio;
    // End of variables declaration//GEN-END:variables
}
