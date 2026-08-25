package com.mycompany.m.archivos.practica1.s2_2026.gui;

import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorReportes;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorReportes.ReporteCitasPorEspecialidad;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorReportes.ReporteMedicoCitas;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorReportes.ReporteMedicoCitasFecha;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorReportes.ReportePacienteCitas;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Cita;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Medico;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Paciente;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoLog;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Diálogo modal para Reportes (tabs por categoría + filtros + exportación CSV/Texto).
 */
public class DialogoReportes extends JDialog {

    private final ControladorReportes controlador;

    // Tablas por pestaña
    private DefaultTableModel modeloPacientes;
    private JTable tablaPacientes;
    private DefaultTableModel modeloMedicos;
    private JTable tablaMedicos;
    private DefaultTableModel modeloCitas;
    private JTable tablaCitas;
    private DefaultTableModel modeloEspecial;
    private JTable tablaEspecial;

    // Filtros
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JComboBox<String> cmbEstadoCita;
    private JComboBox<String> cmbTipoSangre;
    private JComboBox<String> cmbEspecialidad;

    public DialogoReportes(java.awt.Frame parent, ControladorReportes controlador) {
        super(parent, "Reportes y Exportación", true);
        this.controlador = controlador;
        setSize(1100, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(5, 5));

        inicializarComponentes();
        cargarCombos();
    }

    private void inicializarComponentes() {
        // ===== PANEL SUPERIOR: FILTROS COMUNES =====
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFiltros.setBorder(BorderFactory.createTitledBorder("Filtros Generales"));

        pnlFiltros.add(new JLabel("Fecha Desde (DD/MM/YYYY):"));
        txtFechaDesde = new JTextField(10);
        pnlFiltros.add(txtFechaDesde);

        pnlFiltros.add(new JLabel("Hasta:"));
        txtFechaHasta = new JTextField(10);
        pnlFiltros.add(txtFechaHasta);

        pnlFiltros.add(new JLabel("Estado Cita:"));
        cmbEstadoCita = new JComboBox<>(new String[]{"Todos", "Programada", "Atendida", "Cancelada"});
        pnlFiltros.add(cmbEstadoCita);

        pnlFiltros.add(new JLabel("Tipo Sangre:"));
        cmbTipoSangre = new JComboBox<>();
        pnlFiltros.add(cmbTipoSangre);

        pnlFiltros.add(new JLabel("Especialidad:"));
        cmbEspecialidad = new JComboBox<>();
        pnlFiltros.add(cmbEspecialidad);

        JButton btnAplicar = new JButton("Aplicar Filtros");
        btnAplicar.addActionListener(e -> aplicarFiltrosPestañaActiva());
        pnlFiltros.add(btnAplicar);

        add(pnlFiltros, BorderLayout.NORTH);

        // ===== PESTAÑAS =====
        JTabbedPane tabs = new JTabbedPane();

        // ----- Pestaña Pacientes -----
        JPanel pnlPacientes = new JPanel(new BorderLayout());
        String[] colPac = {"CUI", "Nombres", "Fecha Nac.", "Sexo", "Teléfono", "Correo", "Tipo Sangre", "Estado"};
        modeloPacientes = new DefaultTableModel(colPac, 0) { @Override public boolean isCellEditable(int r, int c) { return false; }};
        tablaPacientes = new JTable(modeloPacientes);
        tablaPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pnlPacientes.add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);
        
        JPanel btnsPac = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnsPac.add(crearBtnExportar("Exportar Pacientes CSV", e -> exportarPacientesCSV()));
        btnsPac.add(crearBtnExportar("Exportar Pacientes TXT", e -> exportarPacientesTXT()));
        pnlPacientes.add(btnsPac, BorderLayout.SOUTH);
        tabs.addTab("Pacientes", pnlPacientes);

        // ----- Pestaña Médicos -----
        JPanel pnlMedicos = new JPanel(new BorderLayout());
        String[] colMed = {"ID", "Nombres", "Especialidad", "Teléfono", "Correo", "Horario Inicio", "Horario Fin", "Estado"};
        modeloMedicos = new DefaultTableModel(colMed, 0) { @Override public boolean isCellEditable(int r, int c) { return false; }};
        tablaMedicos = new JTable(modeloMedicos);
        tablaMedicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pnlMedicos.add(new JScrollPane(tablaMedicos), BorderLayout.CENTER);
        
        JPanel btnsMed = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnsMed.add(crearBtnExportar("Exportar Médicos CSV", e -> exportarMedicosCSV()));
        btnsMed.add(crearBtnExportar("Exportar Médicos TXT", e -> exportarMedicosTXT()));
        pnlMedicos.add(btnsMed, BorderLayout.SOUTH);
        tabs.addTab("Médicos", pnlMedicos);

        // ----- Pestaña Citas -----
        JPanel pnlCitas = new JPanel(new BorderLayout());
        String[] colCit = {"ID", "CUI Paciente", "ID Médico", "Fecha", "Hora", "Motivo", "Estado", "Observaciones"};
        modeloCitas = new DefaultTableModel(colCit, 0) { @Override public boolean isCellEditable(int r, int c) { return false; }};
        tablaCitas = new JTable(modeloCitas);
        tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pnlCitas.add(new JScrollPane(tablaCitas), BorderLayout.CENTER);
        
        JPanel btnsCit = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnsCit.add(crearBtnExportar("Exportar Citas CSV", e -> exportarCitasCSV()));
        btnsCit.add(crearBtnExportar("Exportar Citas TXT", e -> exportarCitasTXT()));
        pnlCitas.add(btnsCit, BorderLayout.SOUTH);
        tabs.addTab("Citas", pnlCitas);

        // ----- Pestaña Reportes Especiales -----
        JPanel pnlEspecial = new JPanel(new BorderLayout());
        String[] colEsp = {"Campo 1", "Campo 2", "Campo 3", "Campo 4", "Valor"};
        modeloEspecial = new DefaultTableModel(colEsp, 0) { @Override public boolean isCellEditable(int r, int c) { return false; }};
        tablaEspecial = new JTable(modeloEspecial);
        tablaEspecial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pnlEspecial.add(new JScrollPane(tablaEspecial), BorderLayout.CENTER);
        
        JPanel btnsEsp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnsEsp.add(crearBtnExportar("Exportar Especial CSV", e -> exportarEspecialCSV()));
        btnsEsp.add(crearBtnExportar("Exportar Especial TXT", e -> exportarEspecialTXT()));
        pnlEspecial.add(btnsEsp, BorderLayout.SOUTH);
        tabs.addTab("Reportes Especiales", pnlEspecial);

        // Cambio de pestaña -> recargar
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) cargarReportePacientes();
            else if (tabs.getSelectedIndex() == 1) cargarReporteMedicos();
            else if (tabs.getSelectedIndex() == 2) cargarReporteCitas();
            else if (tabs.getSelectedIndex() == 3) cargarReportesEspeciales();
        });

        add(tabs, BorderLayout.CENTER);

        // ===== PANEL INFERIOR =====
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        pnlAcciones.add(btnCerrar);
        add(pnlAcciones, BorderLayout.SOUTH);
    }

    private JButton crearBtnExportar(String texto, ActionListener action) {
        JButton btn = new JButton(texto);
        btn.addActionListener(action);
        return btn;
    }

    private void cargarCombos() {
        try {
            // Tipos de sangre
            List<Paciente> pacs = controlador.reporteCompletoPacientes();
            java.util.Set<String> sangres = new java.util.LinkedHashSet<>();
            sangres.add("Todos");
            for (Paciente p : pacs) if (p.getTipoSangre() != null && !p.getTipoSangre().isEmpty()) sangres.add(p.getTipoSangre());
            cmbTipoSangre.setModel(new DefaultComboBoxModel<>(sangres.toArray(new String[0])));

            // Especialidades
            List<Medico> meds = controlador.reporteCompletoMedicos();
            java.util.Set<String> especs = new java.util.LinkedHashSet<>();
            especs.add("Todas");
            for (Medico m : meds) if (m.getEspecialidad() != null && !m.getEspecialidad().isEmpty()) especs.add(m.getEspecialidad());
            cmbEspecialidad.setModel(new DefaultComboBoxModel<>(especs.toArray(new String[0])));
        } catch (IOException ignored) {}
    }

    private void aplicarFiltrosPestañaActiva() {
        // Se recarga al cambiar de pestaña o se puede implementar filtro en tiempo real
    }

    // ==================== CARGA DE REPORTES ====================

    private void cargarReportePacientes() {
        SwingUtilities.invokeLater(() -> {
            try {
                modeloPacientes.setRowCount(0);
                String sangre = (String) cmbTipoSangre.getSelectedItem();
                List<Paciente> lista;
                if (sangre != null && !"Todos".equals(sangre)) {
                    lista = controlador.reportePorTipoSangre(sangre);
                } else {
                    lista = controlador.reporteCompletoPacientes();
                }
                for (Paciente p : lista) {
                    modeloPacientes.addRow(new Object[]{
                        p.getCui(), p.getNombresApellidos(), p.getFechaNacimiento(),
                        p.getSexo(), p.getTelefono(), p.getCorreo(), p.getTipoSangre(),
                        p.isActivo() ? "Activo" : "Eliminado"
                    });
                }
            } catch (IOException ex) {
                mostrarError("Error: " + ex.getMessage());
            }
        });
    }

    private void cargarReporteMedicos() {
        SwingUtilities.invokeLater(() -> {
            try {
                modeloMedicos.setRowCount(0);
                String esp = (String) cmbEspecialidad.getSelectedItem();
                List<Medico> lista;
                if (esp != null && !"Todas".equals(esp)) {
                    lista = controlador.reportePorEspecialidad(esp);
                } else {
                    lista = controlador.reporteCompletoMedicos();
                }
                for (Medico m : lista) {
                    modeloMedicos.addRow(new Object[]{
                        m.getIdMedico(), m.getNombresApellidos(), m.getEspecialidad(),
                        m.getTelefono(), m.getCorreo(), m.getHorarioInicio(), m.getHorarioFin(),
                        m.isActivo() ? "Activo" : "Inactivo"
                    });
                }
            } catch (IOException ex) {
                mostrarError("Error: " + ex.getMessage());
            }
        });
    }

    private void cargarReporteCitas() {
        SwingUtilities.invokeLater(() -> {
            try {
                modeloCitas.setRowCount(0);
                String estado = (String) cmbEstadoCita.getSelectedItem();
                String desde = txtFechaDesde.getText().trim();
                String hasta = txtFechaHasta.getText().trim();
                List<Cita> lista;

                if (!desde.isEmpty() && !hasta.isEmpty()) {
                    lista = controlador.reportePorRangoFechas(desde, hasta);
                } else if (estado != null && !"Todos".equals(estado)) {
                    lista = controlador.reportePorEstado(estado);
                } else {
                    lista = controlador.reporteCompletoCitas();
                }

                for (Cita c : lista) {
                    modeloCitas.addRow(new Object[]{
                        c.getIdCita(), c.getCuiPaciente(), c.getIdMedico(),
                        c.getFecha(), c.getHoraInicio(), c.getMotivo(), c.getEstado(), c.getObservaciones()
                    });
                }
            } catch (IOException ex) {
                mostrarError("Error: " + ex.getMessage());
            }
        });
    }

    private void cargarReportesEspeciales() {
        SwingUtilities.invokeLater(() -> {
            try {
                modeloEspecial.setRowCount(0);
                modeloEspecial.setColumnIdentifiers(new String[]{"Entidad", "Nombre", "Detalle", "Especialidad/Tipo", "Total Citas"});

                // Pacientes con más citas
                for (ReportePacienteCitas r : controlador.reportePacientesConMasCitas()) {
                    modeloEspecial.addRow(new Object[]{"Paciente", r.getNombres(), "", r.getTipoSangre(), r.getTotalCitas()});
                }
                // Médicos con más citas
                for (ReporteMedicoCitas r : controlador.reporteMedicosConMasCitas()) {
                    modeloEspecial.addRow(new Object[]{"Médico", r.getNombres(), "", r.getEspecialidad(), r.getTotalCitas()});
                }
                // Citas por especialidad
                for (ReporteCitasPorEspecialidad r : controlador.reporteCitasPorEspecialidad()) {
                    modeloEspecial.addRow(new Object[]{"Especialidad", "", "", r.getEspecialidad(), r.getCantidad()});
                }
                // Pacientes sin citas
                for (Paciente p : controlador.reportePacientesSinCitas()) {
                    modeloEspecial.addRow(new Object[]{"Paciente (sin citas)", p.getNombresApellidos(), p.getCui(), p.getTipoSangre(), 0});
                }
            } catch (IOException ex) {
                mostrarError("Error: " + ex.getMessage());
            }
        });
    }

    // ==================== EXPORTACIÓN ====================

    private void exportarPacientesCSV() {
        exportarCSV("pacientes", (archivo) -> {
            try {
                List<Paciente> datos = controlador.reporteCompletoPacientes();
                controlador.exportarPacientesCSV(datos, archivo.getAbsolutePath());
                mostrarInfo("Exportado a: " + archivo.getAbsolutePath());
            } catch (IOException ex) { mostrarError("Error exportando: " + ex.getMessage()); }
        });
    }

    private void exportarPacientesTXT() {
        exportarTXT("pacientes", (archivo) -> {
            try {
                List<Paciente> datos = controlador.reporteCompletoPacientes();
                String[] cab = {"CUI", "Nombres", "Fecha Nac.", "Sexo", "Teléfono", "Correo", "Tipo Sangre", "Estado"};
                String[][] filas = new String[datos.size()][8];
                for (int i = 0; i < datos.size(); i++) {
                    Paciente p = datos.get(i);
                    filas[i] = new String[]{p.getCui(), p.getNombresApellidos(), p.getFechaNacimiento(),
                        p.getSexo(), p.getTelefono(), p.getCorreo(), p.getTipoSangre(), p.isActivo() ? "Activo" : "Eliminado"};
                }
                controlador.exportarTextoPlano("Reporte de Pacientes", java.util.Arrays.asList(filas), cab, archivo.getAbsolutePath());
                mostrarInfo("Exportado a: " + archivo.getAbsolutePath());
            } catch (IOException ex) { mostrarError("Error exportando: " + ex.getMessage()); }
        });
    }

    private void exportarMedicosCSV() {
        exportarCSV("medicos", (archivo) -> {
            try {
                List<Medico> datos = controlador.reporteCompletoMedicos();
                controlador.exportarMedicosCSV(datos, archivo.getAbsolutePath());
                mostrarInfo("Exportado a: " + archivo.getAbsolutePath());
            } catch (IOException ex) { mostrarError("Error exportando: " + ex.getMessage()); }
        });
    }

    private void exportarMedicosTXT() {
        exportarTXT("medicos", (archivo) -> {
            try {
                List<Medico> datos = controlador.reporteCompletoMedicos();
                String[] cab = {"ID", "Nombres", "Especialidad", "Teléfono", "Correo", "Horario Inicio", "Horario Fin", "Estado"};
                String[][] filas = new String[datos.size()][8];
                for (int i = 0; i < datos.size(); i++) {
                    Medico m = datos.get(i);
                    filas[i] = new String[]{m.getIdMedico(), m.getNombresApellidos(), m.getEspecialidad(),
                        m.getTelefono(), m.getCorreo(), m.getHorarioInicio(), m.getHorarioFin(), m.isActivo() ? "Activo" : "Inactivo"};
                }
                controlador.exportarTextoPlano("Reporte de Médicos", java.util.Arrays.asList(filas), cab, archivo.getAbsolutePath());
                mostrarInfo("Exportado a: " + archivo.getAbsolutePath());
            } catch (IOException ex) { mostrarError("Error exportando: " + ex.getMessage()); }
        });
    }

    private void exportarCitasCSV() {
        exportarCSV("citas", (archivo) -> {
            try {
                List<Cita> datos = controlador.reporteCompletoCitas();
                controlador.exportarCitasCSV(datos, archivo.getAbsolutePath());
                mostrarInfo("Exportado a: " + archivo.getAbsolutePath());
            } catch (IOException ex) { mostrarError("Error exportando: " + ex.getMessage()); }
        });
    }

    private void exportarCitasTXT() {
        exportarTXT("citas", (archivo) -> {
            try {
                List<Cita> datos = controlador.reporteCompletoCitas();
                String[] cab = {"ID", "CUI Paciente", "ID Médico", "Fecha", "Hora", "Motivo", "Estado", "Observaciones"};
                String[][] filas = new String[datos.size()][8];
                for (int i = 0; i < datos.size(); i++) {
                    Cita c = datos.get(i);
                    filas[i] = new String[]{c.getIdCita(), c.getCuiPaciente(), c.getIdMedico(),
                        c.getFecha(), c.getHoraInicio(), c.getMotivo(), c.getEstado(), c.getObservaciones()};
                }
                controlador.exportarTextoPlano("Reporte de Citas", java.util.Arrays.asList(filas), cab, archivo.getAbsolutePath());
                mostrarInfo("Exportado a: " + archivo.getAbsolutePath());
            } catch (IOException ex) { mostrarError("Error exportando: " + ex.getMessage()); }
        });
    }

    private void exportarEspecialCSV() {
        exportarCSV("reporte_especial", (archivo) -> {
            try {
                // Usar exportación genérica
                String[] cab = {"Entidad", "Nombre", "Detalle", "Especialidad/Tipo", "Total Citas"};
                String[][] filas = new String[modeloEspecial.getRowCount()][5];
                for (int i = 0; i < modeloEspecial.getRowCount(); i++) {
                    for (int j = 0; j < 5; j++) {
                        filas[i][j] = String.valueOf(modeloEspecial.getValueAt(i, j));
                    }
                }
                controlador.exportarTextoPlano("Reportes Especiales", java.util.Arrays.asList(filas), cab, archivo.getAbsolutePath().replace(".csv", ".txt"));
                mostrarInfo("Exportado a: " + archivo.getAbsolutePath().replace(".csv", ".txt"));
            } catch (IOException ex) { mostrarError("Error exportando: " + ex.getMessage()); }
        });
    }

    private void exportarEspecialTXT() {
        exportarTXT("reporte_especial", (archivo) -> {
            try {
                String[] cab = {"Entidad", "Nombre", "Detalle", "Especialidad/Tipo", "Total Citas"};
                String[][] filas = new String[modeloEspecial.getRowCount()][5];
                for (int i = 0; i < modeloEspecial.getRowCount(); i++) {
                    for (int j = 0; j < 5; j++) {
                        filas[i][j] = String.valueOf(modeloEspecial.getValueAt(i, j));
                    }
                }
                controlador.exportarTextoPlano("Reportes Especiales", java.util.Arrays.asList(filas), cab, archivo.getAbsolutePath());
                mostrarInfo("Exportado a: " + archivo.getAbsolutePath());
            } catch (IOException ex) { mostrarError("Error exportando: " + ex.getMessage()); }
        });
    }

    private void exportarCSV(String nombreDefecto, ExportAction action) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar como CSV");
        fc.setSelectedFile(new File(nombreDefecto + ".csv"));
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                action.exportar(fc.getSelectedFile());
            } catch (IOException ex) {
                mostrarError("Error exportando: " + ex.getMessage());
            }
        }
    }

    private void exportarTXT(String nombreDefecto, ExportAction action) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar como Texto");
        fc.setSelectedFile(new File(nombreDefecto + ".txt"));
        fc.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                action.exportar(fc.getSelectedFile());
            } catch (IOException ex) {
                mostrarError("Error exportando: " + ex.getMessage());
            }
        }
    }

    @FunctionalInterface
    private interface ExportAction {
        void exportar(File archivo) throws IOException;
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}