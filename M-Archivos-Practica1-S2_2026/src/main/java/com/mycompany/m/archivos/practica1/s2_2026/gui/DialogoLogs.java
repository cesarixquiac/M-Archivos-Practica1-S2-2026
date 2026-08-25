package com.mycompany.m.archivos.practica1.s2_2026.gui;

import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorLogs;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoLog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Diálogo modal para visualización de Logs de Auditoría (filtros + exportación).
 */
public class DialogoLogs extends JDialog {

    private final ControladorLogs controlador;

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JComboBox<String> cmbModulo;
    private JComboBox<String> cmbOperacion;
    private JComboBox<String> cmbEntidad;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JTextField txtUsuario;
    private JTextField txtIdEntidad;

    public DialogoLogs(java.awt.Frame parent, ControladorLogs controlador) {
        super(parent, "Auditoría - Logs del Sistema", true);
        this.controlador = controlador;
        setSize(1100, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        inicializarComponentes();
        cargarCombos();
        cargarLogs();
    }

    private void inicializarComponentes() {
        // ===== PANEL SUPERIOR: FILTROS =====
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Auditoría"));

        pnlFiltros.add(new JLabel("Módulo:"));
        cmbModulo = new JComboBox<>();
        cmbModulo.setPreferredSize(new java.awt.Dimension(120, 25));
        pnlFiltros.add(cmbModulo);

        pnlFiltros.add(new JLabel("Operación:"));
        cmbOperacion = new JComboBox<>();
        cmbOperacion.setPreferredSize(new java.awt.Dimension(120, 25));
        pnlFiltros.add(cmbOperacion);

        pnlFiltros.add(new JLabel("Entidad:"));
        cmbEntidad = new JComboBox<>();
        cmbEntidad.setPreferredSize(new java.awt.Dimension(100, 25));
        pnlFiltros.add(cmbEntidad);

        pnlFiltros.add(new JLabel("ID Entidad:"));
        txtIdEntidad = new JTextField(15);
        pnlFiltros.add(txtIdEntidad);

        pnlFiltros.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField(12);
        pnlFiltros.add(txtUsuario);

        pnlFiltros.add(new JLabel("Desde:"));
        txtFechaDesde = new JTextField(10);
        txtFechaDesde.setToolTipText("DD/MM/YYYY");
        pnlFiltros.add(txtFechaDesde);

        pnlFiltros.add(new JLabel("Hasta:"));
        txtFechaHasta = new JTextField(10);
        txtFechaHasta.setToolTipText("DD/MM/YYYY");
        pnlFiltros.add(txtFechaHasta);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> cargarLogs());
        pnlFiltros.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        pnlFiltros.add(btnLimpiar);

        add(pnlFiltros, BorderLayout.NORTH);

        // ===== PANEL CENTRAL: TABLA =====
        String[] columnas = {"Timestamp", "Módulo", "Operación", "Entidad", "ID Entidad", "Detalles", "Usuario"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // Ajustar anchos
        tabla.getColumnModel().getColumn(0).setPreferredWidth(140); // Timestamp
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100); // Módulo
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100); // Operación
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);  // Entidad
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120); // ID Entidad
        tabla.getColumnModel().getColumn(5).setPreferredWidth(300); // Detalles
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100); // Usuario
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ===== PANEL INFERIOR: ACCIONES =====
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnExportarCSV = new JButton("Exportar CSV");
        btnExportarCSV.addActionListener(e -> exportarCSV());
        pnlAcciones.add(btnExportarCSV);

        JButton btnExportarTXT = new JButton("Exportar TXT");
        btnExportarTXT.addActionListener(e -> exportarTXT());
        pnlAcciones.add(btnExportarTXT);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarLogs());
        pnlAcciones.add(btnActualizar);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        pnlAcciones.add(btnCerrar);

        add(pnlAcciones, BorderLayout.SOUTH);
    }

    private void cargarCombos() {
        try {
            List<GestorArchivoLog.LogEntry> todos = controlador.verTodos();
            
            java.util.Set<String> modulos = new java.util.LinkedHashSet<>();
            modulos.add("Todos");
            java.util.Set<String> operaciones = new java.util.LinkedHashSet<>();
            operaciones.add("Todas");
            java.util.Set<String> entidades = new java.util.LinkedHashSet<>();
            entidades.add("Todas");
            java.util.Set<String> usuarios = new java.util.LinkedHashSet<>();
            usuarios.add("Todos");

            for (GestorArchivoLog.LogEntry l : todos) {
                modulos.add(l.modulo);
                operaciones.add(l.operacion);
                entidades.add(l.entidad);
                usuarios.add(l.usuario);
            }

            cmbModulo.setModel(new DefaultComboBoxModel<>(modulos.toArray(new String[0])));
            cmbOperacion.setModel(new DefaultComboBoxModel<>(operaciones.toArray(new String[0])));
            cmbEntidad.setModel(new DefaultComboBoxModel<>(entidades.toArray(new String[0])));
        } catch (IOException ignored) {}
    }

    private void cargarLogs() {
        SwingUtilities.invokeLater(() -> {
            try {
                modeloTabla.setRowCount(0);
                String modulo = (String) cmbModulo.getSelectedItem();
                String operacion = (String) cmbOperacion.getSelectedItem();
                String entidad = (String) cmbEntidad.getSelectedItem();
                String idEntidad = txtIdEntidad.getText().trim();
                String desde = txtFechaDesde.getText().trim();
                String hasta = txtFechaHasta.getText().trim();
                String usuario = txtUsuario.getText().trim();

                List<GestorArchivoLog.LogEntry> logs;
                
                if (!idEntidad.isEmpty()) {
                    logs = controlador.buscarPorIdEntidad(idEntidad);
                } else if (!desde.isEmpty() && !hasta.isEmpty()) {
                    logs = controlador.filtrarPorFecha(desde, hasta);
                } else if (modulo != null && !"Todos".equals(modulo)) {
                    logs = controlador.filtrarPorModulo(modulo);
                } else if (operacion != null && !"Todas".equals(operacion)) {
                    logs = controlador.filtrarPorOperacion(operacion);
                } else if (entidad != null && !"Todas".equals(entidad)) {
                    logs = controlador.filtrarPorEntidad(entidad);
                } else if (usuario != null && !"Todos".equals(usuario)) {
                    logs = controlador.filtrarPorUsuario(usuario);
                } else {
                    logs = controlador.verTodos();
                }

                // Aplicar filtros adicionales en cascada
                if (modulo != null && !"Todos".equals(modulo)) {
                    logs = logs.stream().filter(l -> modulo.equalsIgnoreCase(l.modulo)).toList();
                }
                if (operacion != null && !"Todas".equals(operacion)) {
                    logs = logs.stream().filter(l -> operacion.equalsIgnoreCase(l.operacion)).toList();
                }
                if (entidad != null && !"Todas".equals(entidad)) {
                    logs = logs.stream().filter(l -> entidad.equalsIgnoreCase(l.entidad)).toList();
                }
                if (usuario != null && !"Todos".equals(usuario)) {
                    logs = logs.stream().filter(l -> usuario.equalsIgnoreCase(l.usuario)).toList();
                }

                for (GestorArchivoLog.LogEntry l : logs) {
                    modeloTabla.addRow(new Object[]{
                        l.timestamp, l.modulo, l.operacion, l.entidad, 
                        l.idEntidad, l.detalles, l.usuario
                    });
                }
            } catch (IOException ex) {
                mostrarError("Error cargando logs: " + ex.getMessage());
            }
        });
    }

    private void limpiarFiltros() {
        cmbModulo.setSelectedIndex(0);
        cmbOperacion.setSelectedIndex(0);
        cmbEntidad.setSelectedIndex(0);
        txtIdEntidad.setText("");
        txtUsuario.setText("");
        txtFechaDesde.setText("");
        txtFechaHasta.setText("");
        cargarLogs();
    }

    private void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Exportar Logs a CSV");
        fc.setSelectedFile(new File("logs_auditoria.csv"));
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<GestorArchivoLog.LogEntry> logs = getLogsFiltrados();
                controlador.getGestorLog().exportarLogsCSV(logs, fc.getSelectedFile().getAbsolutePath());
                mostrarInfo("Exportado a: " + fc.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                mostrarError("Error exportando: " + ex.getMessage());
            }
        }
    }

    private void exportarTXT() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Exportar Logs a Texto");
        fc.setSelectedFile(new File("logs_auditoria.txt"));
        fc.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<GestorArchivoLog.LogEntry> logs = getLogsFiltrados();
                String[] cab = {"Timestamp", "Módulo", "Operación", "Entidad", "ID Entidad", "Detalles", "Usuario"};
                String[][] filas = new String[logs.size()][7];
                for (int i = 0; i < logs.size(); i++) {
                    GestorArchivoLog.LogEntry l = logs.get(i);
                    filas[i] = new String[]{l.timestamp, l.modulo, l.operacion, l.entidad, 
                        l.idEntidad, l.detalles, l.usuario};
                }
                controlador.getGestorLog().exportarTextoPlano("Log de Auditoría", 
                    java.util.Arrays.asList(filas), cab, fc.getSelectedFile().getAbsolutePath());
                mostrarInfo("Exportado a: " + fc.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                mostrarError("Error exportando: " + ex.getMessage());
            }
        }
    }

    private List<GestorArchivoLog.LogEntry> getLogsFiltrados() throws IOException {
        // Reutilizar la misma lógica de filtrado
        String modulo = (String) cmbModulo.getSelectedItem();
        String operacion = (String) cmbOperacion.getSelectedItem();
        String entidad = (String) cmbEntidad.getSelectedItem();
        String idEntidad = txtIdEntidad.getText().trim();
        String desde = txtFechaDesde.getText().trim();
        String hasta = txtFechaHasta.getText().trim();
        String usuario = txtUsuario.getText().trim();

        List<GestorArchivoLog.LogEntry> logs;
        
        if (!idEntidad.isEmpty()) {
            logs = controlador.buscarPorIdEntidad(idEntidad);
        } else if (!desde.isEmpty() && !hasta.isEmpty()) {
            logs = controlador.filtrarPorFecha(desde, hasta);
        } else if (modulo != null && !"Todos".equals(modulo)) {
            logs = controlador.filtrarPorModulo(modulo);
        } else if (operacion != null && !"Todas".equals(operacion)) {
            logs = controlador.filtrarPorOperacion(operacion);
        } else if (entidad != null && !"Todas".equals(entidad)) {
            logs = controlador.filtrarPorEntidad(entidad);
        } else if (usuario != null && !"Todos".equals(usuario)) {
            logs = controlador.filtrarPorUsuario(usuario);
        } else {
            logs = controlador.verTodos();
        }

        if (modulo != null && !"Todos".equals(modulo)) {
            logs = logs.stream().filter(l -> modulo.equalsIgnoreCase(l.modulo)).toList();
        }
        if (operacion != null && !"Todas".equals(operacion)) {
            logs = logs.stream().filter(l -> operacion.equalsIgnoreCase(l.operacion)).toList();
        }
        if (entidad != null && !"Todas".equals(entidad)) {
            logs = logs.stream().filter(l -> entidad.equalsIgnoreCase(l.entidad)).toList();
        }
        if (usuario != null && !"Todos".equals(usuario)) {
            logs = logs.stream().filter(l -> usuario.equalsIgnoreCase(l.usuario)).toList();
        }
        
        return logs;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}