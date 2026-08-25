package com.mycompany.m.archivos.practica1.s2_2026.gui;

import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorPaciente;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Paciente;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Diálogo modal para gestión de Pacientes (CRUD + búsqueda).
 */
public class DialogoPacientes extends JDialog {

    private final ControladorPaciente controlador;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField txtBuscar;

    // Formulario
    private JTextField txtCui;
    private JTextField txtNombres;
    private JTextField txtFechaNac;
    private JComboBox<String> cmbSexo;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtTipoSangre;

    private Paciente pacienteSeleccionado = null;
    private boolean modoEdicion = false;

    public DialogoPacientes(java.awt.Frame parent, ControladorPaciente controlador) {
        super(parent, "Gestión de Pacientes", true);
        this.controlador = controlador;
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        inicializarComponentes();
        cargarTabla();
    }

    private void inicializarComponentes() {
        // ===== PANEL SUPERIOR: BÚSQUEDA =====
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        pnlBusqueda.add(new JLabel("Nombre / CUI:"));
        txtBuscar = new JTextField(20);
        pnlBusqueda.add(txtBuscar);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        pnlBusqueda.add(btnBuscar);
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> { txtBuscar.setText(""); cargarTabla(); });
        pnlBusqueda.add(btnLimpiar);
        add(pnlBusqueda, BorderLayout.NORTH);

        // ===== PANEL CENTRAL: TABLA =====
        String[] columnas = {"CUI", "Nombres y Apellidos", "Fecha Nac.", "Sexo", "Teléfono", "Correo", "Tipo Sangre", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                cargarEnFormulario(tabla.getSelectedRow());
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ===== PANEL DERECHO: FORMULARIO =====
        JPanel pnlFormulario = new JPanel(new BorderLayout(5, 5));
        pnlFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));
        pnlFormulario.setPreferredSize(new java.awt.Dimension(350, 0));

        // Campos
        JPanel pnlCampos = new JPanel(new GridLayout(8, 2, 5, 5));
        pnlCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlCampos.add(new JLabel("CUI (13 dígitos):"));
        txtCui = new JTextField();
        pnlCampos.add(txtCui);

        pnlCampos.add(new JLabel("Nombres y Apellidos *:"));
        txtNombres = new JTextField();
        pnlCampos.add(txtNombres);

        pnlCampos.add(new JLabel("Fecha Nac. (DD/MM/YYYY):"));
        txtFechaNac = new JTextField();
        pnlCampos.add(txtFechaNac);

        pnlCampos.add(new JLabel("Sexo *:"));
        cmbSexo = new JComboBox<>(new String[]{"Masculino", "Femenino"});
        pnlCampos.add(cmbSexo);

        pnlCampos.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        pnlCampos.add(txtTelefono);

        pnlCampos.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        pnlCampos.add(txtCorreo);

        pnlCampos.add(new JLabel("Tipo de Sangre:"));
        txtTipoSangre = new JTextField();
        pnlCampos.add(txtTipoSangre);

        pnlFormulario.add(pnlCampos, BorderLayout.CENTER);

        // Botones formulario
        JPanel pnlBotonesForm = new JPanel(new GridLayout(4, 1, 5, 5));
        pnlBotonesForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnNuevo = new JButton("Nuevo");
        btnNuevo.addActionListener(e -> nuevo());
        pnlBotonesForm.add(btnNuevo);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        pnlBotonesForm.add(btnGuardar);

        JButton btnEliminar = new JButton("Eliminar (Lógico)");
        btnEliminar.addActionListener(e -> eliminar());
        pnlBotonesForm.add(btnEliminar);

        JButton btnLimpiarForm = new JButton("Limpiar Formulario");
        btnLimpiarForm.addActionListener(e -> limpiarFormulario());
        pnlBotonesForm.add(btnLimpiarForm);

        pnlFormulario.add(pnlBotonesForm, BorderLayout.SOUTH);
        add(pnlFormulario, BorderLayout.EAST);

        // ===== PANEL INFERIOR: BOTONES ACCIÓN =====
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        pnlAcciones.add(btnCerrar);
        add(pnlAcciones, BorderLayout.SOUTH);
    }

    // ==================== OPERACIONES ====================

    private void cargarTabla() {
        SwingUtilities.invokeLater(() -> {
            try {
                modeloTabla.setRowCount(0);
                List<Paciente> lista = controlador.listarTodos();
                for (Paciente p : lista) {
                    modeloTabla.addRow(new Object[]{
                        p.getCui(),
                        p.getNombresApellidos(),
                        p.getFechaNacimiento(),
                        p.getSexo(),
                        p.getTelefono(),
                        p.getCorreo(),
                        p.getTipoSangre(),
                        p.isActivo() ? "Activo" : "Eliminado"
                    });
                }
            } catch (IOException ex) {
                mostrarError("Error al cargar pacientes: " + ex.getMessage());
            }
        });
    }

    private void buscar() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            cargarTabla();
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                modeloTabla.setRowCount(0);
                List<Paciente> resultados;
                if (termino.matches("\\d{13}")) {
                    Paciente p = controlador.buscarPorCui(termino);
                    if (p != null) resultados = List.of(p);
                    else resultados = List.of();
                } else {
                    resultados = controlador.buscarPorNombre(termino);
                }
                for (Paciente p : resultados) {
                    modeloTabla.addRow(new Object[]{
                        p.getCui(), p.getNombresApellidos(), p.getFechaNacimiento(),
                        p.getSexo(), p.getTelefono(), p.getCorreo(), p.getTipoSangre(),
                        p.isActivo() ? "Activo" : "Eliminado"
                    });
                }
            } catch (IOException ex) {
                mostrarError("Error en búsqueda: " + ex.getMessage());
            }
        });
    }

    private void cargarEnFormulario(int fila) {
        String cui = (String) modeloTabla.getValueAt(fila, 0);
        try {
            pacienteSeleccionado = controlador.buscarPorCui(cui);
            if (pacienteSeleccionado != null) {
                txtCui.setText(pacienteSeleccionado.getCui());
                txtNombres.setText(pacienteSeleccionado.getNombresApellidos());
                txtFechaNac.setText(pacienteSeleccionado.getFechaNacimiento());
                cmbSexo.setSelectedItem(pacienteSeleccionado.getSexo());
                txtTelefono.setText(pacienteSeleccionado.getTelefono());
                txtCorreo.setText(pacienteSeleccionado.getCorreo());
                txtTipoSangre.setText(pacienteSeleccionado.getTipoSangre());
                modoEdicion = true;
                txtCui.setEnabled(false); // CUI no editable en edición
            }
        } catch (IOException ex) {
            mostrarError("Error al cargar paciente: " + ex.getMessage());
        }
    }

    private void nuevo() {
        limpiarFormulario();
        pacienteSeleccionado = null;
        modoEdicion = false;
        txtCui.setEnabled(true);
        txtCui.requestFocus();
    }

    private void limpiarFormulario() {
        txtCui.setText("");
        txtNombres.setText("");
        txtFechaNac.setText("");
        cmbSexo.setSelectedIndex(0);
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtTipoSangre.setText("");
        pacienteSeleccionado = null;
        modoEdicion = false;
        txtCui.setEnabled(true);
        tabla.clearSelection();
    }

    private void guardar() {
        String cui = txtCui.getText().trim();
        String nombres = txtNombres.getText().trim();
        String fecha = txtFechaNac.getText().trim();
        String sexo = (String) cmbSexo.getSelectedItem();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String tipoSangre = txtTipoSangre.getText().trim();

        if (cui.isEmpty() || nombres.isEmpty() || fecha.isEmpty()) {
            mostrarError("CUI, Nombres y Fecha de nacimiento son obligatorios");
            return;
        }

        try {
            if (modoEdicion && pacienteSeleccionado != null) {
                controlador.actualizarPaciente(cui, nombres, fecha, sexo, telefono, correo, tipoSangre);
                mostrarInfo("Paciente actualizado correctamente");
            } else {
                controlador.crearPaciente(cui, nombres, fecha, sexo, telefono, correo, tipoSangre);
                mostrarInfo("Paciente registrado correctamente");
            }
            limpiarFormulario();
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (IOException ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (pacienteSeleccionado == null || !modoEdicion) {
            mostrarError("Seleccione un paciente de la tabla para eliminar");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar lógicamente al paciente:\n" + pacienteSeleccionado.getNombresApellidos() + " (" + pacienteSeleccionado.getCui() + ")?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                controlador.eliminarPaciente(pacienteSeleccionado.getCui());
                mostrarInfo("Paciente eliminado (lógico)");
                limpiarFormulario();
                cargarTabla();
            } catch (IOException ex) {
                mostrarError("Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}