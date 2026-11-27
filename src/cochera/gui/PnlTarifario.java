package cochera.gui;

import cochera.excepcion.ValidacionException;
import cochera.modelo.Tarifa;
import cochera.util.Dinero;

/**
 * Pestaña "Tarifario" (JPanel Form): el administrador cambia la tarifa por hora de cada tipo de vehículo.
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
                .columna("Por minuto", t -> "S/ " + String.format(java.util.Locale.US, "%.4f", t.getPrecioMinuto()));
        tblTarifas.setModel(modelo);
        Estilo.tabla(tblTarifas);
        Estilo.anchos(tblTarifas, 40, 260, 130, 110);
        Estilo.derecha(tblTarifas, 2, 3);
        Estilo.botonPrimario(btnGuardar);
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
        btnGuardar = new javax.swing.JButton();
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

        btnGuardar.setText("Guardar cambios");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        pnlEdicion.add(btnGuardar, gridBagConstraints);

        lblNota.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblNota.setForeground(new java.awt.Color(113, 113, 122));
        lblNota.setText("<html><body style=\"width:215px\">Regla de cobro: minutos de permanencia x tarifa por hora / 60. Ej.: Camioneta a S/ 5.00 por hora, 90 min = S/ 7.50.</body></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        pnlEdicion.add(lblNota, gridBagConstraints);

        lblRelleno.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRelleno.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
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
            if (precio == null) {
                throw new ValidacionException("Escribe la tarifa por hora.");
            }
            gui.getApp().getAdmin().actualizarTarifa(t.getId(), precio);
        })) {
            refrescar();
            Dialogos.info(this, "Tarifa actualizada: " + t);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JLabel lblBase;
    private javax.swing.JLabel lblNota;
    private javax.swing.JLabel lblRelleno;
    private javax.swing.JLabel lblSeleccion;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlEdicion;
    private javax.swing.JScrollPane scrTarifas;
    private javax.swing.JTable tblTarifas;
    private javax.swing.JTextField txtBase;
    // End of variables declaration//GEN-END:variables
}
