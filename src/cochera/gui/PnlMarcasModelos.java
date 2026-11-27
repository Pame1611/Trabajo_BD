package cochera.gui;

import cochera.modelo.Marca;
import cochera.modelo.Modelo;
import cochera.modelo.Tarifa;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;

/**
 * Pestaña "Marcas y modelos" (JPanel Form): el administrador agrega o elimina marcas y modelos.
 * Cada marca tiene varios modelos y cada modelo tiene su tipo de vehículo (del que sale la tarifa).
 */
public class PnlMarcasModelos extends javax.swing.JPanel implements Refrescable {

    private static final long serialVersionUID = 1L;

    private final transient ContextoGui gui;
    private final ModeloTabla<Marca> modeloMarcas;
    private final ModeloTabla<Modelo> modeloModelos;

    public PnlMarcasModelos(ContextoGui gui) {
        this.gui = gui;
        initComponents();
        modeloMarcas = new ModeloTabla<Marca>()
                .columna("ID", Marca::getId)
                .columna("Marca", Marca::getNombre)
                .columna("Modelos", m -> gui.getApp().getVehiculos().modelosDe(m).size());
        tblMarcas.setModel(modeloMarcas);
        Estilo.tabla(tblMarcas);
        Estilo.anchos(tblMarcas, 40, 180, 70);
        Estilo.derecha(tblMarcas, 2);

        modeloModelos = new ModeloTabla<Modelo>()
                .columna("ID", Modelo::getId)
                .columna("Modelo", Modelo::getNombre)
                .columna("Tipo de vehículo", m -> m.getTipoVehiculo() == null ? "(lo elige el empleado)"
                        : m.getTipoVehiculo().getTipoVehiculo());
        tblModelos.setModel(modeloModelos);
        Estilo.tabla(tblModelos);
        Estilo.anchos(tblModelos, 40, 160, 220);

        Estilo.botonPrimario(btnAgregarMarca);
        Estilo.botonPeligro(btnEliminarMarca);
        Estilo.botonPrimario(btnAgregarModelo);
        Estilo.botonPeligro(btnEliminarModelo);
        tblMarcas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarModelos();
            }
        });
    }

    @Override
    public void refrescar() {
        modeloMarcas.setFilasConservandoSeleccion(tblMarcas, gui.getApp().getVehiculos().listarMarcas());
        Object tipo = cboTipoModelo.getSelectedItem();
        cboTipoModelo.setModel(new DefaultComboBoxModel<>(gui.getApp().getBaseDatos().listarTarifas().toArray(new Tarifa[0])));
        if (tipo != null) {
            cboTipoModelo.setSelectedItem(tipo);
        }
        if (tblMarcas.getSelectedRow() < 0 && tblMarcas.getRowCount() > 1) {
            tblMarcas.setRowSelectionInterval(1, 1);
        }
        mostrarModelos();
    }

    private void mostrarModelos() {
        Marca m = modeloMarcas.getSeleccionado(tblMarcas);
        if (m == null) {
            modeloModelos.setFilas(new ArrayList<Modelo>());
            lblMarcaSeleccionada.setText("Selecciona una marca en la tabla.");
        } else {
            modeloModelos.setFilasConservandoSeleccion(tblModelos, gui.getApp().getVehiculos().modelosDe(m));
            lblMarcaSeleccionada.setText("Marca: " + m.getNombre());
        }
        lblModelosTitulo.setText(m == null ? "Modelos" : "Modelos de " + m.getNombre());
    }

    /**
     * Código generado por el editor visual de NetBeans a partir de PnlMarcasModelos.form.
     * NO modificar a mano: se edita desde la vista Design.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlListas = new javax.swing.JPanel();
        lblMarcasTitulo = new javax.swing.JLabel();
        scrMarcas = new javax.swing.JScrollPane();
        tblMarcas = new javax.swing.JTable();
        lblModelosTitulo = new javax.swing.JLabel();
        scrModelos = new javax.swing.JScrollPane();
        tblModelos = new javax.swing.JTable();
        pnlEdicion = new javax.swing.JPanel();
        lblTituloMarca = new javax.swing.JLabel();
        lblNombreMarca = new javax.swing.JLabel();
        txtMarca = new javax.swing.JTextField();
        btnAgregarMarca = new javax.swing.JButton();
        btnEliminarMarca = new javax.swing.JButton();
        lblTituloModelo = new javax.swing.JLabel();
        lblMarcaSeleccionada = new javax.swing.JLabel();
        lblNombreModelo = new javax.swing.JLabel();
        txtModelo = new javax.swing.JTextField();
        lblTipoModelo = new javax.swing.JLabel();
        cboTipoModelo = new javax.swing.JComboBox<>();
        btnAgregarModelo = new javax.swing.JButton();
        btnEliminarModelo = new javax.swing.JButton();
        lblNota = new javax.swing.JLabel();
        lblRelleno = new javax.swing.JLabel();

        setBackground(new java.awt.Color(244, 244, 245));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setLayout(new java.awt.BorderLayout(12, 0));

        pnlListas.setBackground(new java.awt.Color(244, 244, 245));
        pnlListas.setLayout(new java.awt.GridBagLayout());

        lblMarcasTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMarcasTitulo.setText("Marcas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlListas.add(lblMarcasTitulo, gridBagConstraints);

        scrMarcas.setViewportView(tblMarcas);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlListas.add(scrMarcas, gridBagConstraints);

        lblModelosTitulo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblModelosTitulo.setText("Modelos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        pnlListas.add(lblModelosTitulo, gridBagConstraints);

        scrModelos.setViewportView(tblModelos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        pnlListas.add(scrModelos, gridBagConstraints);

        add(pnlListas, java.awt.BorderLayout.CENTER);

        pnlEdicion.setBackground(new java.awt.Color(255, 255, 255));
        pnlEdicion.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(228, 228, 231)), javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        pnlEdicion.setPreferredSize(new java.awt.Dimension(330, 100));
        pnlEdicion.setLayout(new java.awt.GridBagLayout());

        lblTituloMarca.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblTituloMarca.setText("Nueva marca");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlEdicion.add(lblTituloMarca, gridBagConstraints);

        lblNombreMarca.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNombreMarca.setText("Nombre de la marca");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblNombreMarca, gridBagConstraints);

        txtMarca.setColumns(10);
        txtMarca.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtMarca, gridBagConstraints);

        btnAgregarMarca.setText("Agregar marca");
        btnAgregarMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMarcaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(btnAgregarMarca, gridBagConstraints);

        btnEliminarMarca.setText("Eliminar marca seleccionada");
        btnEliminarMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarMarcaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        pnlEdicion.add(btnEliminarMarca, gridBagConstraints);

        lblTituloModelo.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblTituloModelo.setText("Nuevo modelo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(26, 0, 0, 0);
        pnlEdicion.add(lblTituloModelo, gridBagConstraints);

        lblMarcaSeleccionada.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblMarcaSeleccionada.setForeground(new java.awt.Color(180, 83, 9));
        lblMarcaSeleccionada.setText("Selecciona una marca en la tabla.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlEdicion.add(lblMarcaSeleccionada, gridBagConstraints);

        lblNombreModelo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNombreModelo.setText("Nombre del modelo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblNombreModelo, gridBagConstraints);

        txtModelo.setColumns(10);
        txtModelo.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(txtModelo, gridBagConstraints);

        lblTipoModelo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblTipoModelo.setText("Tipo de vehículo del modelo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(lblTipoModelo, gridBagConstraints);

        cboTipoModelo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlEdicion.add(cboTipoModelo, gridBagConstraints);

        btnAgregarModelo.setText("Agregar modelo");
        btnAgregarModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarModeloActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlEdicion.add(btnAgregarModelo, gridBagConstraints);

        btnEliminarModelo.setText("Eliminar modelo seleccionado");
        btnEliminarModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarModeloActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        pnlEdicion.add(btnEliminarModelo, gridBagConstraints);

        lblNota.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblNota.setForeground(new java.awt.Color(113, 113, 122));
        lblNota.setText("<html><body style=\"width:215px\">Cada marca tiene un modelo \"Desconocido\" para cuando no se conoce el modelo. No se puede eliminar una marca o un modelo que ya se usó en un alquiler (llave foránea).</body></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 0, 0);
        pnlEdicion.add(lblNota, gridBagConstraints);

        lblRelleno.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRelleno.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.weighty = 1.0;
        pnlEdicion.add(lblRelleno, gridBagConstraints);

        add(pnlEdicion, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarMarcaActionPerformed
        final String nombre = txtMarca.getText();
        if (gui.ejecutar(this, () -> {
            Marca nueva = gui.getApp().getVehiculos().agregarMarca(nombre);
            refrescar();
            int fila = modeloMarcas.getFilas().indexOf(nueva);
            if (fila >= 0) {
                tblMarcas.setRowSelectionInterval(fila, fila);
            }
        })) {
            txtMarca.setText("");
        }
    }//GEN-LAST:event_btnAgregarMarcaActionPerformed

    private void btnEliminarMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarMarcaActionPerformed
        final Marca m = modeloMarcas.getSeleccionado(tblMarcas);
        if (m == null || !Dialogos.confirmar(this, "¿Eliminar la marca " + m + " y sus modelos?")) {
            return;
        }
        if (gui.ejecutar(this, () -> gui.getApp().getVehiculos().eliminarMarca(m))) {
            tblMarcas.clearSelection();
            refrescar();
        }
    }//GEN-LAST:event_btnEliminarMarcaActionPerformed

    private void btnAgregarModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarModeloActionPerformed
        final Marca m = modeloMarcas.getSeleccionado(tblMarcas);
        final String nombre = txtModelo.getText();
        final Tarifa tipo = (Tarifa) cboTipoModelo.getSelectedItem();
        if (gui.ejecutar(this, () -> gui.getApp().getVehiculos().agregarModelo(m, nombre, tipo))) {
            txtModelo.setText("");
            refrescar();
        }
    }//GEN-LAST:event_btnAgregarModeloActionPerformed

    private void btnEliminarModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarModeloActionPerformed
        final Modelo m = modeloModelos.getSeleccionado(tblModelos);
        if (m == null) {
            Dialogos.info(this, "Selecciona un modelo en la tabla de modelos.");
            return;
        }
        if (!Dialogos.confirmar(this, "¿Eliminar el modelo " + m.getDescripcion() + "?")) {
            return;
        }
        if (gui.ejecutar(this, () -> gui.getApp().getVehiculos().eliminarModelo(m))) {
            refrescar();
        }
    }//GEN-LAST:event_btnEliminarModeloActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarMarca;
    private javax.swing.JButton btnAgregarModelo;
    private javax.swing.JButton btnEliminarMarca;
    private javax.swing.JButton btnEliminarModelo;
    private javax.swing.JComboBox<cochera.modelo.Tarifa> cboTipoModelo;
    private javax.swing.JLabel lblMarcaSeleccionada;
    private javax.swing.JLabel lblMarcasTitulo;
    private javax.swing.JLabel lblModelosTitulo;
    private javax.swing.JLabel lblNombreMarca;
    private javax.swing.JLabel lblNombreModelo;
    private javax.swing.JLabel lblNota;
    private javax.swing.JLabel lblRelleno;
    private javax.swing.JLabel lblTipoModelo;
    private javax.swing.JLabel lblTituloMarca;
    private javax.swing.JLabel lblTituloModelo;
    private javax.swing.JPanel pnlEdicion;
    private javax.swing.JPanel pnlListas;
    private javax.swing.JScrollPane scrMarcas;
    private javax.swing.JScrollPane scrModelos;
    private javax.swing.JTable tblMarcas;
    private javax.swing.JTable tblModelos;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtModelo;
    // End of variables declaration//GEN-END:variables
}
