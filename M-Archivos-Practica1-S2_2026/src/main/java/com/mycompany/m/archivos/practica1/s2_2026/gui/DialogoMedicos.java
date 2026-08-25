package com.mycompany.m.archivos.practica1.s2_2026.gui;

import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorMedico;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Medico;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
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
 * Diálogo modal para gestión de Médicos (CRUD + filtros activo/inactivo/especialidad).
 */
public class DialogoMedicos extends JDialog {

    private final ControladorMedico controlador;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JComboBox<String> cmbFiltroEstado;
    private JComboBox<String> cmbFiltroEspecialidad;
    private JTextField txtBuscarNombre;

    // Formulario
    private JTextField txtId;
    private JTextField txtNombres;
    private JTextField txtEspecialidad;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtHorarioInicio;
    private JTextField txtHorarioFin;

    private Medico medicoSeleccionado = null;
    private boolean modoEdicion = false;

    public DialogoMedicos(java.awt.Frame parent, ControladorMedico controlador) {
        super(parent, "Gestión de Médicos", true);
        this.controlador = controlador;
        setSize(950, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        inicializarComponentes();
        cargarTabla();
        cargarEspecialidades();
    }

    private void inicializarComponentes() {
        // ===== PANEL SUPERIOR: FILTROS =====
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        pnlFiltros.add(new JLabel("Nombre:"));
        txtBuscarNombre = new JTextField(15);
        pnlFiltros.add(txtBuscarNombre);

        pnlFiltros.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Activos", "Inactivos"});
        pnlFiltros.add(cmbFiltroEstado);

        pnlFiltros.add(new JLabel("Especialidad:"));
        cmbFiltroEspecialidad = new JComboBox<>();
        cmbFiltroEspecialidad.setPreferredSize(new java.awt.Dimension(150, 25));
        pnlFiltros.add(cmbFiltroEspecialidad);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrar());
        pnlFiltros.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> { 
            txtBuscarNombre.setText("");
            cmbFiltroEstado.setSelectedIndex(0);
            cmbFiltroEspecialidad.setSelectedIndex(0);
            cargarTabla();
        });
        pnlFiltros.add(btnLimpiar);

        add(pnlFiltros, BorderLayout.NORTH);

        // ===== PANEL CENTRAL: TABLA =====
        String[] columnas = {"ID (UUID)", "Nombres y Apellidos", "Especialidad", "Teléfono", "Correo", "Horario Inicio", "Horario Fin", "Estado"};
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
        pnlFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Médico"));
        pnlFormulario.setPreferredSize(new java.awt.Dimension(350, 0));

        JPanel pnlCampos = new JPanel(new GridLayout(9, 2, 5, 5));
        pnlCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlCampos.add(new JLabel("ID (UUID):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        pnlCampos.add(txtId);

        pnlCampos.add(new JLabel("Nombres y Apellidos *:"));
        txtNombres = new JTextField();
        pnlCampos.add(txtNombres);

        pnlCampos.add(new JLabel("Especialidad *:"));
        txtEspecialidad = new JTextField();
        pnlCampos.add(txtEspecialidad);

        pnlCampos.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        pnlCampos.add(txtTelefono);

        pnlCampos.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        pnlCampos.add(txtCorreo);

        pnlCampos.add(new JLabel("Horario Inicio (HH:MM):"));
        txtHorarioInicio = new JTextField();
        pnlCampos.add(txtHorarioInicio);

        pnlCampos.add(new JLabel("Horario Fin (HH:MM):"));
        txtHorarioFin = new JTextField();
        pnlCampos.add(txtHorarioFin);

        pnlFormulario.add(pnlCampos, BorderLayout.CENTER);

        // Botones formulario
        JPanel pnlBotonesForm = new JPanel(new GridLayout(5, 1, 5, 5));
        pnlBotonesForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnNuevo = new JButton("Nuevo");
        btnNuevo.addActionListener(e -> nuevo());
        pnlBotonesForm.add(btnNuevo);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        pnlBotonesForm.add(btnGuardar);

        JButton btnActivar = new JButton("Activar");
        btnActivar.addActionListener(e -> cambiarEstado(true));
        pnlBotonesForm.add(btnActivar);

        JButton btnDesactivar = new JButton("Desactivar");
        btnDesactivar.addActionListener(e -> cambiarEstado(false));
        pnlBotonesForm.add(btnDesactivar);

        JButton btnLimpiarForm = new JButton("Limpiar Formulario");
        btnLimpiarForm.addActionListener(e -> limpiarFormulario());
        pnlBotonesForm.add(btnLimpiarForm);

        pnlFormulario.add(pnlBotonesForm, BorderLayout.SOUTH);
        add(pnlFormulario, BorderLayout.EAST);

        // ===== PANEL INFERIOR =====
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        pnlAcciones.add(btnCerrar);
        add(pnlAcciones, BorderLayout.SOUTH);
    }

    private void cargarEspecialidades() {
        try {
            List<Medico> todos = controlador.listarTodos();
            java.util.Set<String> especialidades = new java.util.LinkedHashSet<>();
            especialidades.add("Todas");
            for (Medico m : todos) {
                if (m.getEspecialidad() != null && !m.getEspecialidad().isEmpty()) {
                    especialidades.add(m.getEspecialidad());
                }
            }
            cmbFiltroEspecialidad.setModel(new DefaultComboBoxModel<>(especialidades.toArray(new String[0])));
        } catch (IOException ex) {
            // Ignorar
        }
    }

    private void cargarTabla() {
        SwingUtilities.invokeLater(() -> {
            try {
                modeloTabla.setRowCount(0);
                List<Medico> lista;
                String estadoFiltro = (String) cmbFiltroEstado.getSelectedItem();
                String espFiltro = (String) cmbFiltroEspecialidad.getSelectedItem();
                String nombreFiltro = txtBuscarNombre.getText().trim();

                if (!nombreFiltro.isEmpty()) {
                    lista = controlador.buscarPorNombre(nombreFiltro);
                } else if ("Activos".equals(estadoFiltro)) {
                    lista = controlador.listarActivos();
                } else if ("Inactivos".equals(estadoFiltro)) {
                    lista = controlador.listarInactivos();
                } else {
                    lista = controlador.listarTodos();
                }

                if (espFiltro != null && !"Todas".equals(espFiltro)) {
                    lista = lista.stream().filter(m -> espFiltro.equals(m.getEspecialidad())).toList();
                }

                for (Medico m : lista) {
                    modeloTabla.addRow(new Object[]{
                        m.getIdMedico(),
                        m.getNombresApellidos(),
                        m.getEspecialidad(),
                        m.getTelefono(),
                        m.getCorreo(),
                        m.getHorarioInicio(),
                        m.getHorarioFin(),
                        m.isActivo() ? "Activo" : "Inactivo"
                    });
                }
            } catch (IOException ex) {
                mostrarError("Error al cargar médicos: " + ex.getMessage());
            }
        });
    }

    private void filtrar() {
        cargarTabla();
        cargarEspecialidades(); // Actualizar lista de especialidades
    }

    private void cargarEnFormulario(int fila) {
        String id = (String) modeloTabla.getValueAt(fila, 0);
        try {
            medicoSeleccionado = controlador.buscarPorId(id);
            if (medicoSeleccionado == null) {
                // Buscar en inactivos
                for (Medico m : controlador.listarInactivos()) {
                    if (m.getIdMedico().equals(id)) {
                        medicoSeleccionado = m;
                        break;
                    }
                }
            }
            if (medicoSeleccionado != null) {
                txtId.setText(medicoSeleccionado.getIdMedico());
                txtNombres.setText(medicoSeleccionado.getNombresApellidos());
                txtEspecialidad.setText(medicoSeleccionado.getEspecialidad());
                txtTelefono.setText(medicoSeleccionado.getTelefono());
                txtCorreo.setText(medicoSeleccionado.getCorreo());
                txtHorarioInicio.setText(medicoSeleccionado.getHorarioInicio());
                txtHorarioFin.setText(medicoSeleccionado.getHorarioFin());
                modoEdicion = true;
            }
        } catch (IOException ex) {
            mostrarError("Error al cargar médico: " + ex.getMessage());
        }
    }

    private void nuevo() {
        limpiarFormulario();
        medicoSeleccionado = null;
        modoEdicion = false;
        txtNombres.requestFocus();
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombres.setText("");
        txtEspecialidad.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtHorarioInicio.setText("");
        txtHorarioFin.setText("");
        medicoSeleccionado = null;
        modoEdicion = false;
        tabla.clearSelection();
    }

    private void guardar() {
        String nombres = txtNombres.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String hInicio = txtHorarioInicio.getText().trim();
        String hFin = txtHorarioFin.getText().trim();

        if (nombres.isEmpty() || especialidad.isEmpty()) {
            mostrarError("Nombres y Especialidad son obligatorios");
            return;
        }

        try {
            if (modoEdicion && medicoSeleccionado != null) {
                controlador.actualizarMedico(medicoSeleccionado.getIdMedico(), nombres, especialidad,
                        telefono, correo, hInicio, hFin);
                mostrarInfo("Médico actualizado correctamente");
            } else {
                controlador.crearMedico(nombres, especialidad, telefono, correo, hInicio, hFin);
                mostrarInfo("Médico registrado correctamente");
            }
            limpiarFormulario();
            cargarTabla();
            cargarEspecialidades();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (IOException ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    private void cambiarEstado(boolean activar) {
        if (medicoSeleccionado == null) {
            mostrarError("Seleccione un médico de la tabla");
            return;
        }
        String accion = activar ? "activar" : "desactivar";
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿" + accion.substring(0,1).toUpperCase() + accion.substring(1) + " al médico:\n" + medicoSeleccionado.getNombresApellidos() + "?",
                "Confirmar " + accion, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                if (activar) controlador.activarMedico(medicoSeleccionado.getIdMedico());
                else controlador.desactivarMedico(medicoSeleccionado.getIdMedico());
                mostrarInfo("Médico " + (activar ? "activado" : "desactivado") + " correctamente");
                limpiarFormulario();
                cargarTabla();
            } catch (IOException ex) {
                mostrarError("Error: " + ex.getMessage());
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