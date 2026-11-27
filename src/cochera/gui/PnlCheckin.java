package cochera.gui;

import cochera.excepcion.CocheraException;
import cochera.impresion.TicketEntrada;
import cochera.modelo.Alquiler;
import cochera.modelo.Cliente;
import cochera.modelo.Marca;
import cochera.modelo.Modelo;
import cochera.modelo.Tarifa;
import cochera.servicio.VehiculoServicio;
import cochera.util.Dinero;
import cochera.util.Dni;
import cochera.util.FechaUtil;
import java.awt.Component;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Pestaña "Check-in" (JPanel Form): registra la entrada con el DNI del cliente, la marca y
 * el modelo del vehículo (combos) y su tipo, y genera el ticket con código de barras.
 */
public class PnlCheckin extends javax.swing.JPanel implements Refrescable {

    private static final long serialVersionUID = 1L;

    private final transient ContextoGui gui;
    private transient Alquiler ultimo;

    public PnlCheckin(ContextoGui gui) {
        this.gui = gui;
        initComponents();
        Estilo.botonPrimario(btnRegistrar);
        Estilo.botonSecundario(btnReimprimir);
        btnReimprimir.setEnabled(false);
        FiltroPlaca.instalar(txtPlaca);
        cboTipo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int i, boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, i, sel, foco);
                if (valor instanceof Tarifa) {
                    Tarifa t = (Tarifa) valor;
                    setText(t.getTipoVehiculo() + "     " + Dinero.soles(t.getPrecioHora()) + " por hora");
                }
                return this;
            }
        });
        cboTipo.addActionListener(e -> mostrarTarifa());
        cboMarca.addActionListener(e -> cargarModelos());
        cboModelo.addActionListener(e -> aplicarTipoDelModelo());
        UtilForm.alCambiar(txtDni, this::mostrarCliente);
        cargarCatalogos();
        mostrarCliente();
    }

    @Override
    public void refrescar() {
        boolean hayTurno = gui.getApp().getTurnos().obtenerTurnoAbierto() != null;
        lblAviso.setVisible(!hayTurno);
        btnRegistrar.setEnabled(hayTurno);
        txtPlaca.setEnabled(hayTurno);
        txtDni.setEnabled(hayTurno);
        cargarCatalogos();
        if (hayTurno) {
            txtDni.requestFocusInWindow();
        }
    }

    /** Recarga tipos y marcas (el administrador pudo agregar nuevos) conservando la selección. */
    private void cargarCatalogos() {
        Object tipo = cboTipo.getSelectedItem();
        cboTipo.setModel(new DefaultComboBoxModel<>(gui.getApp().getCheckin().obtenerTarifario().toArray(new Tarifa[0])));
        cboTipo.setSelectedItem(tipo != null ? tipo : gui.getApp().getBaseDatos().buscarTarifa(6)); // Auto
        Object marca = cboMarca.getSelectedItem();
        cboMarca.setModel(new DefaultComboBoxModel<>(gui.getApp().getVehiculos().listarMarcas().toArray(new Marca[0])));
        if (marca != null) {
            cboMarca.setSelectedItem(marca);
        }
        cargarModelos();
        mostrarTarifa();
    }

    /** RF-ING-02: el combo de modelos muestra solo los de la marca elegida ("Desconocido" primero). */
    private void cargarModelos() {
        Marca marca = (Marca) cboMarca.getSelectedItem();
        VehiculoServicio vehiculos = gui.getApp().getVehiculos();
        Modelo[] modelos = marca == null ? new Modelo[0] : vehiculos.modelosDe(marca).toArray(new Modelo[0]);
        cboModelo.setModel(new DefaultComboBoxModel<>(modelos));
    }

    /** Cada modelo tiene su tipo de vehículo: al elegirlo se selecciona su tipo (y su precio). */
    private void aplicarTipoDelModelo() {
        Modelo m = (Modelo) cboModelo.getSelectedItem();
        if (m != null && m.getTipoVehiculo() != null) {
            cboTipo.setSelectedItem(m.getTipoVehiculo());
        }
    }

    /** RF-ING-04: al elegir el tipo se muestra su precio. */
    private void mostrarTarifa() {
        Tarifa t = (Tarifa) cboTipo.getSelectedItem();
        lblTarifaInfo.setText(t == null ? " " : t.getTipoVehiculo() + ": " + Dinero.soles(t.getPrecioHora())
                + " por hora  ·  se cobra por minuto (" + Dinero.soles(t.getPrecioHora()) + " / 60)");
    }

    /** RF-ING-05: busca al cliente por DNI; si no existe se registrará al guardar la entrada. */
    private void mostrarCliente() {
        String dni = txtDni.getText().trim();
        txtNombre.setEnabled(false);
        if (!Dni.esValido(dni)) {
            lblCliente.setText(dni.isEmpty() ? "Escribe el DNI (8 dígitos) y presiona Enter." : "El DNI debe tener 8 dígitos.");
            lblCliente.setForeground(Estilo.TEXTO_SUAVE);
            txtNombre.setText("");
            return;
        }
        try {
            Cliente c = gui.getApp().getClientes().buscar(dni);
            if (c != null) {
                lblCliente.setText("Cliente registrado: " + c.getNombreCompleto());
                lblCliente.setForeground(Estilo.VERDE);
                txtNombre.setText(c.getNombreCompleto());
            } else {
                lblCliente.setText("Cliente nuevo: se registrará con este DNI.");
                lblCliente.setForeground(Estilo.AMBAR);
                txtNombre.setText("");
                txtNombre.setEnabled(true);
            }
        } catch (CocheraException e) {
            lblCliente.setText(e.getMessage());
            lblCliente.setForeground(Estilo.ROJO);
        }
    }

    private void registrar() {
        final Tarifa tarifa = (Tarifa) cboTipo.getSelectedItem();
        final Modelo modelo = (Modelo) cboModelo.getSelectedItem();
        final String placa = txtPlaca.getText();
        final String dni = txtDni.getText();
        final String nombre = txtNombre.isEnabled() ? txtNombre.getText() : "";
        gui.ejecutar(this, () -> {
            Alquiler t = gui.getApp().getCheckin().registrarEntrada(dni, nombre, placa,
                    modelo == null ? 0 : modelo.getId(), tarifa == null ? 0 : tarifa.getId());
            ultimo = t;
            lblUltimo.setText(t.getCodigoBarras() + "   ·   " + t.getPlacaFormateada() + "   ·   "
                    + t.getDescripcionVehiculo() + " (" + t.getTarifa().getTipoVehiculo() + ")   ·   "
                    + t.getCliente().getNombreCompleto() + "   ·   " + FechaUtil.hora(t.getHoraEntrada()));
            btnReimprimir.setEnabled(true);
            txtPlaca.setText("");
            txtDni.setText("");
            cboMarca.setSelectedIndex(0); // vuelve a "Desconocido"
            gui.imprimir(this, new TicketEntrada(t));
        });
        txtDni.requestFocusInWindow();
    }

    /**
     * Código generado por el editor visual de NetBeans a partir de PnlCheckin.form.
     * NO modificar a mano: se edita desde la vista Design.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblAviso = new javax.swing.JLabel();
        pnlFormulario = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSecCliente = new javax.swing.JLabel();
        lblSecVehiculo = new javax.swing.JLabel();
        lblDni = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lblCliente = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblPlaca = new javax.swing.JLabel();
        txtPlaca = new javax.swing.JTextField();
        lblPlacaNota = new javax.swing.JLabel();
        lblMarca = new javax.swing.JLabel();
        cboMarca = new javax.swing.JComboBox<>();
        lblModelo = new javax.swing.JLabel();
        cboModelo = new javax.swing.JComboBox<>();
        lblTipo = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        lblTarifaInfo = new javax.swing.JLabel();
        btnRegistrar = new javax.swing.JButton();
        pnlUltimo = new javax.swing.JPanel();
        lblUltimoTitulo = new javax.swing.JLabel();
        lblUltimo = new javax.swing.JLabel();
        btnReimprimir = new javax.swing.JButton();
        lblRelleno = new javax.swing.JLabel();

        setBackground(new java.awt.Color(244, 244, 245));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setLayout(new java.awt.GridBagLayout());

        lblAviso.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblAviso.setForeground(new java.awt.Color(146, 64, 14));
        lblAviso.setText("No tienes un turno de caja abierto. Ábrelo en la pestaña \"Turno de caja\".");
        lblAviso.setBackground(new java.awt.Color(254, 243, 199));
        lblAviso.setOpaque(true);
        lblAviso.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(lblAviso, gridBagConstraints);

        pnlFormulario.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormulario.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(228, 228, 231)), javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        pnlFormulario.setLayout(new java.awt.GridBagLayout());

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitulo.setText("Registrar entrada de vehículo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlFormulario.add(lblTitulo, gridBagConstraints);

        lblSecCliente.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblSecCliente.setForeground(new java.awt.Color(113, 113, 122));
        lblSecCliente.setText("CLIENTE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 0, 0, 0);
        pnlFormulario.add(lblSecCliente, gridBagConstraints);

        lblSecVehiculo.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblSecVehiculo.setForeground(new java.awt.Color(113, 113, 122));
        lblSecVehiculo.setText("VEHÍCULO");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 24, 0, 0);
        pnlFormulario.add(lblSecVehiculo, gridBagConstraints);

        lblDni.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblDni.setText("DNI del cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        pnlFormulario.add(lblDni, gridBagConstraints);

        txtDni.setColumns(9);
        txtDni.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txtDni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDniActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlFormulario.add(txtDni, gridBagConstraints);

        lblCliente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCliente.setForeground(new java.awt.Color(113, 113, 122));
        lblCliente.setText("Escribe el DNI (8 dígitos) y presiona Enter.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlFormulario.add(lblCliente, gridBagConstraints);

        lblNombre.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblNombre.setText("Nombre del cliente (solo si es nuevo)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 0, 0, 0);
        pnlFormulario.add(lblNombre, gridBagConstraints);

        txtNombre.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlFormulario.add(txtNombre, gridBagConstraints);

        lblPlaca.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPlaca.setText("Placa");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 24, 0, 0);
        pnlFormulario.add(lblPlaca, gridBagConstraints);

        txtPlaca.setColumns(9);
        txtPlaca.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        txtPlaca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPlacaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 0, 0);
        pnlFormulario.add(txtPlaca, gridBagConstraints);

        lblPlacaNota.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblPlacaNota.setForeground(new java.awt.Color(113, 113, 122));
        lblPlacaNota.setText("Se escribe sola en el formato ABC-123. Enter para registrar.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 24, 0, 0);
        pnlFormulario.add(lblPlacaNota, gridBagConstraints);

        lblMarca.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblMarca.setText("Marca");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 24, 0, 0);
        pnlFormulario.add(lblMarca, gridBagConstraints);

        cboMarca.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 0, 0);
        pnlFormulario.add(cboMarca, gridBagConstraints);

        lblModelo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblModelo.setText("Modelo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 24, 0, 0);
        pnlFormulario.add(lblModelo, gridBagConstraints);

        cboModelo.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 0, 0);
        pnlFormulario.add(cboModelo, gridBagConstraints);

        lblTipo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTipo.setText("Tipo de vehículo");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 24, 0, 0);
        pnlFormulario.add(lblTipo, gridBagConstraints);

        cboTipo.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 24, 0, 0);
        pnlFormulario.add(cboTipo, gridBagConstraints);

        lblTarifaInfo.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTarifaInfo.setForeground(new java.awt.Color(180, 83, 9));
        lblTarifaInfo.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 0, 0, 0);
        pnlFormulario.add(lblTarifaInfo, gridBagConstraints);

        btnRegistrar.setText("Registrar entrada e imprimir ticket");
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 0, 0);
        pnlFormulario.add(btnRegistrar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(pnlFormulario, gridBagConstraints);

        pnlUltimo.setBackground(new java.awt.Color(255, 255, 255));
        pnlUltimo.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(228, 228, 231)), javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        pnlUltimo.setLayout(new java.awt.GridBagLayout());

        lblUltimoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblUltimoTitulo.setForeground(new java.awt.Color(113, 113, 122));
        lblUltimoTitulo.setText("ÚLTIMO INGRESO REGISTRADO");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlUltimo.add(lblUltimoTitulo, gridBagConstraints);

        lblUltimo.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblUltimo.setText("Todavía no registras entradas en esta sesión.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlUltimo.add(lblUltimo, gridBagConstraints);

        btnReimprimir.setText("Reimprimir ticket");
        btnReimprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReimprimirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlUltimo.add(btnReimprimir, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(pnlUltimo, gridBagConstraints);

        lblRelleno.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblRelleno.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 1.0;
        add(lblRelleno, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void txtDniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDniActionPerformed
        mostrarCliente();
        if (txtNombre.isEnabled()) {
            txtNombre.requestFocusInWindow();
        } else {
            txtPlaca.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtDniActionPerformed

    private void txtPlacaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPlacaActionPerformed
        registrar();
    }//GEN-LAST:event_txtPlacaActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        registrar();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnReimprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReimprimirActionPerformed
        if (ultimo != null) {
            gui.imprimir(this, new TicketEntrada(ultimo));
        }
    }//GEN-LAST:event_btnReimprimirActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnReimprimir;
    private javax.swing.JComboBox<cochera.modelo.Marca> cboMarca;
    private javax.swing.JComboBox<cochera.modelo.Modelo> cboModelo;
    private javax.swing.JComboBox<cochera.modelo.Tarifa> cboTipo;
    private javax.swing.JLabel lblAviso;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblDni;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblModelo;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPlaca;
    private javax.swing.JLabel lblPlacaNota;
    private javax.swing.JLabel lblRelleno;
    private javax.swing.JLabel lblSecCliente;
    private javax.swing.JLabel lblSecVehiculo;
    private javax.swing.JLabel lblTarifaInfo;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUltimo;
    private javax.swing.JLabel lblUltimoTitulo;
    private javax.swing.JPanel pnlFormulario;
    private javax.swing.JPanel pnlUltimo;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPlaca;
    // End of variables declaration//GEN-END:variables
}
