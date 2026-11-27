package cochera.gui;

import cochera.excepcion.CocheraException;
import cochera.modelo.AlquilerMensual;
import cochera.servicio.AlquilerMensualServicio;
import cochera.util.Dinero;
import cochera.util.FechaUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestaña "Alquiler mensual" (JPanel Form): alquileres por mes con fecha de inicio y de fin,
 * subtotal, 10% de descuento por vehículo y total, filtrados por el DNI del cliente.
 * El alquiler se registra en Check-in eligiendo la modalidad "Por mes".
 */
public class PnlAlquilerMensual extends javax.swing.JPanel implements Refrescable {

    private static final long serialVersionUID = 1L;

    private final transient ContextoGui gui;
    private final ModeloTabla<AlquilerMensual> modelo;

    public PnlAlquilerMensual(ContextoGui gui) {
        this.gui = gui;
        initComponents();
        final AlquilerMensualServicio srv = gui.getApp().getMensuales();
        modelo = new ModeloTabla<AlquilerMensual>()
                .columna("Código", AlquilerMensual::getCodigoBarras)
                .columna("Cliente", a -> a.getCliente().getNombreCompleto() + " (" + a.getCliente().getDni() + ")")
                .columna("Placa", AlquilerMensual::getPlacaFormateada)
                .columna("Vehículo", a -> a.getDescripcionVehiculo() + " · " + a.getTarifa().getTipoVehiculo())
                .columna("Inicio", a -> FechaUtil.fecha(a.getFechaInicio()))
                .columna("Fin", a -> FechaUtil.fecha(a.getFechaFin()))
                .columna("Subtotal", a -> Dinero.soles(a.calcularSubtotal(srv.momentoDeCalculo(a))))
                .columna("Descuento 10%", a -> "-" + Dinero.soles(a.calcularDescuento(srv.momentoDeCalculo(a))))
                .columna("Total", a -> Dinero.soles(a.calcularMonto(srv.momentoDeCalculo(a))))
                .columna("Estado", a -> a.estaEnCurso() ? "VIGENTE" : a.getEstado().name());
        tblAlquileres.setModel(modelo);
        Estilo.tabla(tblAlquileres);
        Estilo.anchos(tblAlquileres, 120, 200, 75, 210, 85, 85, 90, 105, 85, 80);
        Estilo.derecha(tblAlquileres, 6, 7, 8);
        Estilo.botonPrimario(btnBuscar);
        Estilo.botonSecundario(btnTodos);
    }

    @Override
    public void refrescar() {
        cargar(txtDni.getText());
    }

    private void cargar(String dni) {
        try {
            AlquilerMensualServicio srv = gui.getApp().getMensuales();
            List<AlquilerMensual> lista = srv.listar(dni);
            modelo.setFilas(lista);
            AlquilerMensualServicio.Resumen r = srv.resumir(lista);
            String quien = dni.trim().isEmpty() || lista.isEmpty() ? "Todos los clientes"
                    : lista.get(0).getCliente().getNombreCompleto();
            lblConteo.setText(lista.size() + " alquiler(es) mensual(es)");
            lblResumen.setText(quien + ":  " + r.getVehiculos() + " vehículo(s)  ·  Subtotal " + Dinero.soles(r.getSubtotal())
                    + "  -  Descuento " + Dinero.soles(r.getDescuento()));
            lblTotal.setText(Dinero.soles(r.getTotal()));
        } catch (CocheraException e) {
            modelo.setFilas(new ArrayList<AlquilerMensual>());
            lblConteo.setText(e.getMessage());
            lblResumen.setText(" ");
            lblTotal.setText(Dinero.soles(0));
        }
    }

    /**
     * Código generado por el editor visual de NetBeans a partir de PnlAlquilerMensual.form.
     * NO modificar a mano: se edita desde la vista Design.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlFiltro = new javax.swing.JPanel();
        lblBuscar = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnTodos = new javax.swing.JButton();
        lblConteo = new javax.swing.JLabel();
        scrAlquileres = new javax.swing.JScrollPane();
        tblAlquileres = new javax.swing.JTable();
        pnlResumen = new javax.swing.JPanel();
        lblResumenTitulo = new javax.swing.JLabel();
        lblResumen = new javax.swing.JLabel();
        lblRegla = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(244, 244, 245));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setLayout(new java.awt.BorderLayout(0, 12));

        pnlFiltro.setBackground(new java.awt.Color(255, 255, 255));
        pnlFiltro.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(228, 228, 231)), javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        pnlFiltro.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 6));

        lblBuscar.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblBuscar.setText("DNI del cliente:");
        pnlFiltro.add(lblBuscar);

        txtDni.setColumns(10);
        txtDni.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDniActionPerformed(evt);
            }
        });
        pnlFiltro.add(txtDni);

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        pnlFiltro.add(btnBuscar);

        btnTodos.setText("Ver todos");
        btnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTodosActionPerformed(evt);
            }
        });
        pnlFiltro.add(btnTodos);

        lblConteo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblConteo.setForeground(new java.awt.Color(113, 113, 122));
        lblConteo.setText(" ");
        pnlFiltro.add(lblConteo);

        add(pnlFiltro, java.awt.BorderLayout.NORTH);

        scrAlquileres.setViewportView(tblAlquileres);

        add(scrAlquileres, java.awt.BorderLayout.CENTER);

        pnlResumen.setBackground(new java.awt.Color(255, 255, 255));
        pnlResumen.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(228, 228, 231)), javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        pnlResumen.setLayout(new java.awt.GridBagLayout());

        lblResumenTitulo.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblResumenTitulo.setForeground(new java.awt.Color(113, 113, 122));
        lblResumenTitulo.setText("RESUMEN (10% DE DESCUENTO POR CADA VEHÍCULO)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlResumen.add(lblResumenTitulo, gridBagConstraints);

        lblResumen.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        lblResumen.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlResumen.add(lblResumen, gridBagConstraints);

        lblRegla.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblRegla.setForeground(new java.awt.Color(113, 113, 122));
        lblRegla.setText("Monto mensual = tarifa mensual del tipo x 0.90. Ej.: tarifa de S/ 200.00, paga S/ 180.00; 2 autos de S/ 200.00 pagan S/ 360.00.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlResumen.add(lblRegla, gridBagConstraints);

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(180, 83, 9));
        lblTotal.setText("S/ 0.00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlResumen.add(lblTotal, gridBagConstraints);

        add(pnlResumen, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void txtDniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDniActionPerformed
        cargar(txtDni.getText());
    }//GEN-LAST:event_txtDniActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        cargar(txtDni.getText());
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTodosActionPerformed
        txtDni.setText("");
        cargar("");
    }//GEN-LAST:event_btnTodosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnTodos;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblConteo;
    private javax.swing.JLabel lblRegla;
    private javax.swing.JLabel lblResumen;
    private javax.swing.JLabel lblResumenTitulo;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlFiltro;
    private javax.swing.JPanel pnlResumen;
    private javax.swing.JScrollPane scrAlquileres;
    private javax.swing.JTable tblAlquileres;
    private javax.swing.JTextField txtDni;
    // End of variables declaration//GEN-END:variables
}
