package com.mycompany.m.archivos.practica1.s2_2026.gui;

import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorCita;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorMedico;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorPaciente;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Cita;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Medico;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Paciente;
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
 * Diálogo modal para gestión de Citas (CRUD + filtros + acciones: cancelar/atender/reprogramar).
 */
public class DialogoCitas extends JDialog {

    private final ControladorCita controlador;
    private final ControladorPaciente ctrlPaciente;
    private final ControladorMedico ctrlMedico;

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JComboBox<String> cmbFiltroEstado;
    private JComboBox<String> cmbFiltroMedico;
    private JTextField txtBuscarPaciente;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;

    // Formulario
    private JTextField txtId;
    private JTextField txtCuiPaciente;
    private JTextField txtPacienteNombre; // Solo lectura
    private JComboBox<String> cmbMedico;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtMotivo;
    private JTextField txtObservaciones;
    private JComboBox<String> cmbEstado;

    private Cita citaSeleccionada = null;
    private boolean modoEdicion = false;

    public DialogoCitas(java.awt.Frame parent, ControladorCita controlador, 
                        ControladorPaciente ctrlPaciente, ControladorMedico ctrlMedico) {
        super(parent, "Gestión de Citas Médicas", true);
        this.controlador = controlador;
        this.ctrlPaciente = ctrlPaciente;
        this.ctrlMedico = ctrlMedico;
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        inicializarComponentes();
        cargarTabla();
        cargarMedicosEnCombo();
    }

    private void inicializarComponentes() {
        // ===== PANEL SUPERIOR: FILTROS =====
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));

        pnlFiltros.add(new JLabel("Paciente (CUI/Nombre):"));
        txtBuscarPaciente = new JTextField(12);
        pnlFiltros.add(txtBuscarPaciente);

        pnlFiltros.add(new JLabel("Médico:"));
        cmbFiltroMedico = new JComboBox<>();
        cmbFiltroMedico.setPreferredSize(new java.awt.Dimension(150, 25));
        pnlFiltros.add(cmbFiltroMedico);

        pnlFiltros.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Programada", "Atendida", "Cancelada"});
        pnlFiltros.add(cmbFiltroEstado);

        pnlFiltros.add(new JLabel("Desde:"));
        txtFechaDesde = new JTextField(10);
        txtFechaDesde.setToolTipText("DD/MM/YYYY");
        pnlFiltros.add(txtFechaDesde);

        pnlFiltros.add(new JLabel("Hasta:"));
        txtFechaHasta = new JTextField(10);
        txtFechaHasta.setToolTipText("DD/MM/YYYY");
        pnlFiltros.add(txtFechaHasta);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrar());
        pnlFiltros.add(btnFiltrar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        pnlFiltros.add(btnLimpiar);

        add(pnlFiltros, BorderLayout.NORTH);

        // ===== PANEL CENTRAL: TABLA =====
        String[] columnas = {"ID", "Paciente (CUI)", "Médico", "Fecha", "Hora", "Motivo", "Estado", "Obs."};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                cargarEnFormulario(tabla.getSelectedRow());
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ===== PANEL DERECHO: FORMULARIO =====
        JPanel pnlFormulario = new JPanel(new BorderLayout(5, 5));
        pnlFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la Cita"));
        pnlFormulario.setPreferredSize(new java.awt.Dimension(380, 0));

        JPanel pnlCampos = new JPanel(new GridLayout(10, 2, 5, 5));
        pnlCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlCampos.add(new JLabel("ID (UUID):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        pnlCampos.add(txtId);

        pnlCampos.add(new JLabel("CUI Paciente *:"));
        txtCuiPaciente = new JTextField();
        pnlCampos.add(txtCuiPaciente);

        pnlCampos.add(new JLabel("Nombre Paciente:"));
        txtPacienteNombre = new JTextField();
        txtPacienteNombre.setEditable(false);
        pnlCampos.add(txtPacienteNombre);

        pnlCampos.add(new JLabel("Médico *:"));
        cmbMedico = new JComboBox<>();
        pnlCampos.add(cmbMedico);

        pnlCampos.add(new JLabel("Fecha (DD/MM/YYYY) *:"));
        txtFecha = new JTextField();
        pnlCampos.add(txtFecha);

        pnlCampos.add(new JLabel("Hora (HH:MM) *:"));
        txtHora = new JTextField();
        pnlCampos.add(txtHora);

        pnlCampos.add(new JLabel("Motivo *:"));
        txtMotivo = new JTextField();
        pnlCampos.add(txtMotivo);

        pnlCampos.add(new JLabel("Observaciones:"));
        txtObservaciones = new JTextField();
        pnlCampos.add(txtObservaciones);

        pnlCampos.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{"Programada", "Atendida", "Cancelada"});
        cmbEstado.setEnabled(false); // Se cambia con botones de acción
        pnlCampos.add(cmbEstado);

        pnlFormulario.add(pnlCampos, BorderLayout.CENTER);

        // Botones formulario
        JPanel pnlBotonesForm = new JPanel(new GridLayout(7, 1, 5, 5));
        pnlBotonesForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnNuevo = new JButton("Nueva Cita");
        btnNuevo.addActionListener(e -> nuevo());
        pnlBotonesForm.add(btnNuevo);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(e -> guardar());
        pnlBotonesForm.add(btnGuardar);

        JButton btnAtender = new JButton("Marcar Atendida");
        btnAtender.addActionListener(e -> cambiarEstado(Cita.EST_ATENDIDA));
        pnlBotonesForm.add(btnAtender);

        JButton btnCancelar = new JButton("Cancelar Cita");
        btnCancelar.addActionListener(e -> cambiarEstado(Cita.EST_CANCELADA));
        pnlBotonesForm.add(btnCancelar);

        JButton btnReprogramar = new JButton("Reprogramar");
        btnReprogramar.addActionListener(e -> reprogramar());
        pnlBotonesForm.add(btnReprogramar);

        JButton btnEliminar = new JButton("Eliminar (Lógico)");
        btnEliminar.addActionListener(e -> eliminar());
        pnlBotonesForm.add(btnEliminar);

        JButton btnLimpiarForm = new JButton("Limpiar Formulario");
        btnLimpiarForm.addActionListener(e -> limpiarFormulario());
        pnlBotonesForm.add(btnLimpiarForm);

        pnlFormulario.add(pnlBotonesForm, BorderLayout.SOUTH);
        add(pnlFormulario, BorderLayout.EAST);

        // ===== PANEL INFERIOR =====
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVerPaciente = new JButton("Ver Paciente");
        btnVerPaciente.addActionListener(e -> verPaciente());
        pnlAcciones.add(btnVerPaciente);

        JButton btnVerMedico = new JButton("Ver Médico");
        btnVerMedico.addActionListener(e -> verMedico());
        pnlAcciones.add(btnVerMedico);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        pnlAcciones.add(btnCerrar);
        add(pnlAcciones, BorderLayout.SOUTH);
    }

    private void cargarMedicosEnCombo() {
        try {
            List<Medico> medicos = ctrlMedico.listarActivos();
            cmbMedico.removeAllItems();
            cmbFiltroMedico.removeAllItems();
            cmbFiltroMedico.addItem("Todos");
            for (Medico m : medicos) {
                cmbMedico.addItem(m.getNombresApellidos() + " (" + m.getEspecialidad() + ") - " + m.getIdMedico());
                cmbFiltroMedico.addItem(m.getNombresApellidos() + " (" + m.getEspecialidad() + ")");
            }
        } catch (IOException ex) {
            // Ignorar
        }
    }

    private void cargarTabla() {
        SwingUtilities.invokeLater(() -> {
            try {
                modeloTabla.setRowCount(0);
                List<Cita> lista;
                String estado = (String) cmbFiltroEstado.getSelectedItem();
                String medico = (String) cmbFiltroMedico.getSelectedItem();
                String paciente = txtBuscarPaciente.getText().trim();
                String desde = txtFechaDesde.getText().trim();
                String hasta = txtFechaHasta.getText().trim();

                if (!paciente.isEmpty()) {
                    lista = controlador.buscarPorPaciente(paciente);
                } else if (medico != null && !"Todos".equals(medico)) {
                    // Extraer nombre del médico del combo
                    String nombreMedico = medico.split(" \\(")[0];
                    List<Medico> meds = ctrlMedico.buscarPorNombre(nombreMedico);
                    if (!meds.isEmpty()) {
                        lista = controlador.buscarPorMedico(meds.get(0).getIdMedico());
                    } else {
                        lista = List.of();
                    }
                } else if (!"Todos".equals(estado)) {
                    lista = controlador.buscarPorEstado(estado);
                } else if (!desde.isEmpty() && !hasta.isEmpty()) {
                    lista = controlador.buscarPorRangoFechas(desde, hasta);
                } else {
                    lista = controlador.listarActivas();
                }

                // Filtrar por estado si se seleccionó y no fue la búsqueda principal
                if (!"Todos".equals(estado) && (paciente.isEmpty() && ("Todos".equals(medico) || medico == null))) {
                    lista = lista.stream().filter(c -> estado.equals(c.getEstado())).toList();
                }

                for (Cita c : lista) {
                    String medicoNombre = "";
                    try {
                        Medico m = ctrlMedico.buscarPorId(c.getIdMedico());
                        if (m != null) medicoNombre = m.getNombresApellidos();
                    } catch (Exception ignored) {}
                    
                    modeloTabla.addRow(new Object[]{
                        c.getIdCita().substring(0, 8) + "...",
                        c.getCuiPaciente(),
                        medicoNombre,
                        c.getFecha(),
                        c.getHoraInicio(),
                        c.getMotivo(),
                        c.getEstado(),
                        c.getObservaciones().length() > 20 ? c.getObservaciones().substring(0, 20) + "..." : c.getObservaciones()
                    });
                }
            } catch (IOException ex) {
                mostrarError("Error al cargar citas: " + ex.getMessage());
            }
        });
    }

    private void filtrar() {
        cargarTabla();
    }

    private void limpiarFiltros() {
        txtBuscarPaciente.setText("");
        cmbFiltroMedico.setSelectedIndex(0);
        cmbFiltroEstado.setSelectedIndex(0);
        txtFechaDesde.setText("");
        txtFechaHasta.setText("");
        cargarTabla();
    }

    private void cargarEnFormulario(int fila) {
        String idCorta = (String) modeloTabla.getValueAt(fila, 0);
        String idCompleto = buscarIdCompleto(idCorta);
        if (idCompleto == null) return;
        
        try {
            citaSeleccionada = controlador.buscarPorId(idCompleto);
            if (citaSeleccionada != null) {
                txtId.setText(citaSeleccionada.getIdCita());
                txtCuiPaciente.setText(citaSeleccionada.getCuiPaciente());
                txtFecha.setText(citaSeleccionada.getFecha());
                txtHora.setText(citaSeleccionada.getHoraInicio());
                txtMotivo.setText(citaSeleccionada.getMotivo());
                txtObservaciones.setText(citaSeleccionada.getObservaciones());
                cmbEstado.setSelectedItem(citaSeleccionada.getEstado());
                
                // Seleccionar médico en combo
                for (int i = 0; i < cmbMedico.getItemCount(); i++) {
                    if (cmbMedico.getItemAt(i).contains(citaSeleccionada.getIdMedico())) {
                        cmbMedico.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Cargar nombre del paciente
                try {
                    Paciente p = ctrlPaciente.buscarPorCui(citaSeleccionada.getCuiPaciente());
                    if (p != null) txtPacienteNombre.setText(p.getNombresApellidos());
                } catch (Exception ignored) {}
                
                modoEdicion = true;
            }
        } catch (IOException ex) {
            mostrarError("Error al cargar cita: " + ex.getMessage());
        }
    }

    private String buscarIdCompleto(String idCorta) {
        try {
            List<Cita> todas = controlador.listarTodas();
            for (Cita c : todas) {
                if (c.getIdCita().startsWith(idCorta.replace("...", ""))) {
                    return c.getIdCita();
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    private void nuevo() {
        limpiarFormulario();
        citaSeleccionada = null;
        modoEdicion = false;
        cmbEstado.setSelectedItem("Programada");
        cmbEstado.setEnabled(false);
        txtCuiPaciente.requestFocus();
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtCuiPaciente.setText("");
        txtPacienteNombre.setText("");
        cmbMedico.setSelectedIndex(0);
        txtFecha.setText("");
        txtHora.setText("");
        txtMotivo.setText("");
        txtObservaciones.setText("");
        cmbEstado.setSelectedIndex(0);
        cmbEstado.setEnabled(false);
        citaSeleccionada = null;
        modoEdicion = false;
        tabla.clearSelection();
    }

    private void guardar() {
        String cuiPaciente = txtCuiPaciente.getText().trim();
        String fecha = txtFecha.getText().trim();
        String hora = txtHora.getText().trim();
        String motivo = txtMotivo.getText().trim();
        String obs = txtObservaciones.getText().trim();

        if (cuiPaciente.isEmpty() || fecha.isEmpty() || hora.isEmpty() || motivo.isEmpty()) {
            mostrarError("CUI Paciente, Fecha, Hora y Motivo son obligatorios");
            return;
        }

        // Obtener ID médico seleccionado
        String itemMedico = (String) cmbMedico.getSelectedItem();
        if (itemMedico == null) {
            mostrarError("Seleccione un médico");
            return;
        }
        String idMedico = itemMedico.substring(itemMedico.lastIndexOf(" - ") + 3);

        try {
            if (modoEdicion && citaSeleccionada != null) {
                controlador.actualizarDetalles(citaSeleccionada.getIdCita(), motivo, obs);
                mostrarInfo("Cita actualizada correctamente");
            } else {
                controlador.programarCita(cuiPaciente, idMedico, fecha, hora, motivo, obs);
                mostrarInfo("Cita programada correctamente");
            }
            limpiarFormulario();
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (IOException ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    private void cambiarEstado(String nuevoEstado) {
        if (citaSeleccionada == null) {
            mostrarError("Seleccione una cita de la tabla");
            return;
        }
        try {
            boolean ok = false;
            String accion = "";
            if (Cita.EST_ATENDIDA.equals(nuevoEstado)) {
                ok = controlador.marcarAtendida(citaSeleccionada.getIdCita());
                accion = "marcar como atendida";
            } else if (Cita.EST_CANCELADA.equals(nuevoEstado)) {
                ok = controlador.cancelarCita(citaSeleccionada.getIdCita());
                accion = "cancelar";
            }
            if (ok) {
                mostrarInfo("Cita " + accion + " correctamente");
                limpiarFormulario();
                cargarTabla();
            }
        } catch (IOException ex) {
            mostrarError("Error: " + ex.getMessage());
        }
    }

    private void reprogramar() {
        if (citaSeleccionada == null) {
            mostrarError("Seleccione una cita para reprogramar");
            return;
        }
        // Habilitar edición de fecha/hora/médico
        cmbEstado.setSelectedItem("Programada");
        cmbEstado.setEnabled(true);
        modoEdicion = true;
        txtFecha.requestFocus();
    }

    private void eliminar() {
        if (citaSeleccionada == null) {
            mostrarError("Seleccione una cita de la tabla");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar lógicamente la cita del paciente " + txtPacienteNombre.getText() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                controlador.eliminarCita(citaSeleccionada.getIdCita());
                mostrarInfo("Cita eliminada (lógico)");
                limpiarFormulario();
                cargarTabla();
            } catch (IOException ex) {
                mostrarError("Error: " + ex.getMessage());
            }
        }
    }

    private void verPaciente() {
        if (citaSeleccionada == null) return;
        try {
            Paciente p = ctrlPaciente.buscarPorCui(citaSeleccionada.getCuiPaciente());
            if (p != null) {
                JOptionPane.showMessageDialog(this,
                        "CUI: " + p.getCui() + "\n"
                        + "Nombre: " + p.getNombresApellidos() + "\n"
                        + "Fecha Nac: " + p.getFechaNacimiento() + "\n"
                        + "Sexo: " + p.getSexo() + "\n"
                        + "Teléfono: " + p.getTelefono() + "\n"
                        + "Correo: " + p.getCorreo() + "\n"
                        + "Tipo Sangre: " + p.getTipoSangre(),
                        "Datos del Paciente", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            mostrarError("Error: " + ex.getMessage());
        }
    }

    private void verMedico() {
        if (citaSeleccionada == null) return;
        try {
            Medico m = ctrlMedico.buscarPorId(citaSeleccionada.getIdMedico());
            if (m != null) {
                JOptionPane.showMessageDialog(this,
                        "ID: " + m.getIdMedico() + "\n"
                        + "Nombre: " + m.getNombresApellidos() + "\n"
                        + "Especialidad: " + m.getEspecialidad() + "\n"
                        + "Teléfono: " + m.getTelefono() + "\n"
                        + "Correo: " + m.getCorreo() + "\n"
                        + "Horario: " + m.getHorarioInicio() + " - " + m.getHorarioFin() + "\n"
                        + "Estado: " + (m.isActivo() ? "Activo" : "Inactivo"),
                        "Datos del Médico", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            mostrarError("Error: " + ex.getMessage());
        }
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}