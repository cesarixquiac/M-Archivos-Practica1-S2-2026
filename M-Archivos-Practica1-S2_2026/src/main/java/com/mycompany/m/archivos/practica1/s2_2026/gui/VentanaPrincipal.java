package com.mycompany.m.archivos.practica1.s2_2026.gui;

import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoCita;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoLog;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoMedico;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoPaciente;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorCita;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorLogs;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorMedico;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorPaciente;
import com.mycompany.m.archivos.practica1.s2_2026.controladores.ControladorReportes;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ventana principal de la aplicación (JFrame).
 * Contiene menú, barra de herramientas y lanza los diálogos modales.
 */
public class VentanaPrincipal extends JFrame {

    // Gestores de persistencia (compartidos)
    private final GestorArchivoPaciente gestorPaciente;
    private final GestorArchivoMedico gestorMedico;
    private final GestorArchivoCita gestorCita;
    private final GestorArchivoLog gestorLog;

    // Controladores de negocio
    private final ControladorPaciente ctrlPaciente;
    private final ControladorMedico ctrlMedico;
    private final ControladorCita ctrlCita;
    private final ControladorReportes ctrlReportes;
    private final ControladorLogs ctrlLogs;

    // Diálogos modales (se crean bajo demanda)
    private DialogoPacientes dlgPacientes;
    private DialogoMedicos dlgMedicos;
    private DialogoCitas dlgCitas;
    private DialogoReportes dlgReportes;
    private DialogoLogs dlgLogs;

    private final String usuarioActual = "admin"; // TODO: Login real

    public VentanaPrincipal() throws IOException {
        // Inicializar gestores
        this.gestorPaciente = new GestorArchivoPaciente();
        this.gestorMedico = new GestorArchivoMedico();
        this.gestorCita = new GestorArchivoCita();
        this.gestorLog = new GestorArchivoLog();

        // Inicializar controladores (inyección de dependencias)
        this.ctrlPaciente = new ControladorPaciente(gestorPaciente, gestorLog, usuarioActual);
        this.ctrlMedico = new ControladorMedico(gestorMedico, gestorLog, usuarioActual);
        this.ctrlCita = new ControladorCita(gestorCita, gestorPaciente, gestorMedico, gestorLog, usuarioActual);
        this.ctrlReportes = new ControladorReportes(gestorPaciente, gestorMedico, gestorCita, gestorLog, usuarioActual);
        this.ctrlLogs = new ControladorLogs(gestorLog, usuarioActual);

        inicializarVentana();
        construirMenu();
        construirBarraHerramientas();
        construirBarraEstado();

        // Log de inicio
        ctrlLogs.logLogin();
    }

    private void inicializarVentana() {
        setTitle("Sistema de Gestión - Clínica Médica");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Look & Feel nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar por defecto
        }

        // Manejar cierre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cerrarAplicacion();
            }
        });
    }

    private void construirMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu mnuArchivo = new JMenu("Archivo");
        JMenuItem mniSalir = new JMenuItem("Salir");
        mniSalir.addActionListener(e -> cerrarAplicacion());
        mnuArchivo.add(mniSalir);
        menuBar.add(mnuArchivo);

        // Menú Módulos
        JMenu mnuModulos = new JMenu("Módulos");
        
        JMenuItem mniPacientes = new JMenuItem("Pacientes");
        mniPacientes.addActionListener(e -> abrirPacientes());
        mnuModulos.add(mniPacientes);

        JMenuItem mniMedicos = new JMenuItem("Médicos");
        mniMedicos.addActionListener(e -> abrirMedicos());
        mnuModulos.add(mniMedicos);

        JMenuItem mniCitas = new JMenuItem("Citas");
        mniCitas.addActionListener(e -> abrirCitas());
        mnuModulos.add(mniCitas);

        menuBar.add(mnuModulos);

        // Menú Reportes
        JMenu mnuReportes = new JMenu("Reportes");
        JMenuItem mniVerReportes = new JMenuItem("Generar Reportes");
        mniVerReportes.addActionListener(e -> abrirReportes());
        mnuReportes.add(mniVerReportes);
        menuBar.add(mnuReportes);

        // Menú Logs
        JMenu mnuLogs = new JMenu("Auditoría");
        JMenuItem mniVerLogs = new JMenuItem("Ver Logs");
        mniVerLogs.addActionListener(e -> abrirLogs());
        mnuLogs.add(mniVerLogs);
        menuBar.add(mnuLogs);

        // Menú Ayuda
        JMenu mnuAyuda = new JMenu("Ayuda");
        JMenuItem mniAcerca = new JMenuItem("Acerca de");
        mniAcerca.addActionListener(e -> mostrarAcercaDe());
        mnuAyuda.add(mniAcerca);
        menuBar.add(mnuAyuda);

        setJMenuBar(menuBar);
    }

    private void construirBarraHerramientas() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnPacientes = new JButton("Pacientes");
        btnPacientes.addActionListener(e -> abrirPacientes());
        toolBar.add(btnPacientes);

        JButton btnMedicos = new JButton("Médicos");
        btnMedicos.addActionListener(e -> abrirMedicos());
        toolBar.add(btnMedicos);

        JButton btnCitas = new JButton("Citas");
        btnCitas.addActionListener(e -> abrirCitas());
        toolBar.add(btnCitas);

        toolBar.addSeparator();

        JButton btnReportes = new JButton("Reportes");
        btnReportes.addActionListener(e -> abrirReportes());
        toolBar.add(btnReportes);

        JButton btnLogs = new JButton("Logs");
        btnLogs.addActionListener(e -> abrirLogs());
        toolBar.add(btnLogs);

        add(toolBar, BorderLayout.NORTH);
    }

    private void construirBarraEstado() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.add(new JLabel("  Usuario: " + usuarioActual + "  |  Clínica Médica v1.0  "), BorderLayout.WEST);
        statusBar.add(new JLabel("  Listo  "), BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);
    }

    // ==================== ABRIR DIÁLOGOS ====================

    private void abrirPacientes() {
        if (dlgPacientes == null) {
            dlgPacientes = new DialogoPacientes(this, ctrlPaciente);
        }
        dlgPacientes.setVisible(true);
    }

    private void abrirMedicos() {
        if (dlgMedicos == null) {
            dlgMedicos = new DialogoMedicos(this, ctrlMedico);
        }
        dlgMedicos.setVisible(true);
    }

    private void abrirCitas() {
        if (dlgCitas == null) {
            dlgCitas = new DialogoCitas(this, ctrlCita, ctrlPaciente, ctrlMedico);
        }
        dlgCitas.setVisible(true);
    }

    private void abrirReportes() {
        if (dlgReportes == null) {
            dlgReportes = new DialogoReportes(this, ctrlReportes);
        }
        dlgReportes.setVisible(true);
    }

    private void abrirLogs() {
        if (dlgLogs == null) {
            dlgLogs = new DialogoLogs(this, ctrlLogs);
        }
        dlgLogs.setVisible(true);
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Gestión de Clínica Médica\n"
                + "Versión 1.0\n"
                + "Manejo e Implementación de Archivos - 2026\n"
                + "Desarrollado con Java Swing + RandomAccessFile",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cerrarAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de salir?", "Confirmar salida",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                ctrlLogs.logLogout();
                gestorPaciente.cerrar();
                gestorMedico.cerrar();
                gestorCita.cerrar();
                gestorLog.cerrar();
            } catch (IOException ex) {
                // Ignorar al cerrar
            }
            dispose();
            System.exit(0);
        }
    }

    // ==================== MAIN ====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                VentanaPrincipal app = new VentanaPrincipal();
                app.setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error al inicializar la aplicación:\n" + e.getMessage(),
                        "Error Fatal", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}