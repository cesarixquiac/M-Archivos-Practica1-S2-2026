package com.mycompany.m.archivos.practica1.s2_2026.controladores;

import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoCita;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoLog;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoMedico;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoPaciente;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Cita;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Medico;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Paciente;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para generación de reportes y exportación a CSV/texto.
 * Implementa todos los reportes requeridos por la práctica.
 */
public class ControladorReportes {

    private final GestorArchivoPaciente gestorPaciente;
    private final GestorArchivoMedico gestorMedico;
    private final GestorArchivoCita gestorCita;
    private final GestorArchivoLog gestorLog;
    private final String usuarioActual;

    public ControladorReportes(GestorArchivoPaciente gestorPaciente,
                               GestorArchivoMedico gestorMedico,
                               GestorArchivoCita gestorCita,
                               GestorArchivoLog gestorLog,
                               String usuarioActual) {
        this.gestorPaciente = gestorPaciente;
        this.gestorMedico = gestorMedico;
        this.gestorCita = gestorCita;
        this.gestorLog = gestorLog;
        this.usuarioActual = usuarioActual;
    }

    // ==================== REPORTES DE PACIENTES ====================
    
    public List<Paciente> reporteCompletoPacientes() throws IOException {
        List<Paciente> lista = gestorPaciente.listarTodos();
        logReporte("Pacientes", "COMPLETO", lista.size());
        return lista;
    }

    public List<Paciente> reportePorTipoSangre(String tipoSangre) throws IOException {
        List<Paciente> lista = gestorPaciente.buscarPorTipoSangre(tipoSangre);
        logReporte("Pacientes", "POR_TIPO_SANGRE:" + tipoSangre, lista.size());
        return lista;
    }

    /**
     * Pacientes con mayor cantidad de citas (ordenado descendente).
     */
    public List<ReportePacienteCitas> reportePacientesConMasCitas() throws IOException {
        List<Paciente> pacientes = gestorPaciente.listarTodos();
        List<Cita> citas = gestorCita.listarActivas();
        
        // Contar citas por paciente
        Map<String, Long> citasPorPaciente = citas.stream()
            .collect(Collectors.groupingBy(Cita::getCuiPaciente, Collectors.counting()));
        
        List<ReportePacienteCitas> reporte = pacientes.stream()
            .map(p -> new ReportePacienteCitas(
                p.getCui(), p.getNombresApellidos(), p.getTipoSangre(),
                citasPorPaciente.getOrDefault(p.getCui(), 0L)
            ))
            .sorted(Comparator.comparingLong(ReportePacienteCitas::getTotalCitas).reversed())
            .collect(Collectors.toList());
        
        logReporte("Pacientes", "CON_MAS_CITAS", reporte.size());
        return reporte;
    }

    /**
     * Pacientes que nunca han tenido una cita.
     */
    public List<Paciente> reportePacientesSinCitas() throws IOException {
        List<Paciente> pacientes = gestorPaciente.listarTodos();
        List<Cita> citas = gestorCita.listarTodas(); // Incluir eliminadas para historial
        
        Set<String> cuisConCitas = citas.stream()
            .map(Cita::getCuiPaciente)
            .collect(Collectors.toSet());
        
        List<Paciente> sinCitas = pacientes.stream()
            .filter(p -> !cuisConCitas.contains(p.getCui()))
            .collect(Collectors.toList());
        
        logReporte("Pacientes", "SIN_CITAS", sinCitas.size());
        return sinCitas;
    }

    // ==================== REPORTES DE MÉDICOS ====================
    
    public List<Medico> reporteCompletoMedicos() throws IOException {
        List<Medico> lista = gestorMedico.listarTodos();
        logReporte("Medicos", "COMPLETO", lista.size());
        return lista;
    }

    public List<Medico> reportePorEspecialidad(String especialidad) throws IOException {
        List<Medico> lista = gestorMedico.buscarPorEspecialidad(especialidad);
        logReporte("Medicos", "POR_ESPECIALIDAD:" + especialidad, lista.size());
        return lista;
    }

    /**
     * Médicos con mayor cantidad de citas (ordenado descendente).
     */
    public List<ReporteMedicoCitas> reporteMedicosConMasCitas() throws IOException {
        List<Medico> medicos = gestorMedico.listarTodos();
        List<Cita> citas = gestorCita.listarActivas();
        
        Map<String, Long> citasPorMedico = citas.stream()
            .collect(Collectors.groupingBy(Cita::getIdMedico, Collectors.counting()));
        
        List<ReporteMedicoCitas> reporte = medicos.stream()
            .map(m -> new ReporteMedicoCitas(
                m.getIdMedico(), m.getNombresApellidos(), m.getEspecialidad(),
                citasPorMedico.getOrDefault(m.getIdMedico(), 0L)
            ))
            .sorted(Comparator.comparingLong(ReporteMedicoCitas::getTotalCitas).reversed())
            .collect(Collectors.toList());
        
        logReporte("Medicos", "CON_MAS_CITAS", reporte.size());
        return reporte;
    }

    /**
     * Médicos con citas programadas para una fecha específica.
     */
    public List<ReporteMedicoCitasFecha> reporteMedicosCitasPorFecha(String fecha) throws IOException {
        List<Cita> citasFecha = gestorCita.buscarPorFecha(fecha);
        List<Medico> medicos = gestorMedico.listarActivos();
        
        Map<String, List<Cita>> citasPorMedico = citasFecha.stream()
            .collect(Collectors.groupingBy(Cita::getIdMedico));
        
        List<ReporteMedicoCitasFecha> reporte = medicos.stream()
            .filter(m -> citasPorMedico.containsKey(m.getIdMedico()))
            .map(m -> new ReporteMedicoCitasFecha(
                m.getIdMedico(), m.getNombresApellidos(), m.getEspecialidad(),
                citasPorMedico.get(m.getIdMedico())
            ))
            .collect(Collectors.toList());
        
        logReporte("Medicos", "CITAS_POR_FECHA:" + fecha, reporte.size());
        return reporte;
    }

    // ==================== REPORTES DE CITAS ====================
    
    public List<Cita> reporteCompletoCitas() throws IOException {
        List<Cita> lista = gestorCita.listarActivas();
        logReporte("Citas", "COMPLETO", lista.size());
        return lista;
    }

    public List<Cita> reportePorRangoFechas(String fechaInicio, String fechaFin) throws IOException {
        List<Cita> lista = gestorCita.buscarPorRangoFechas(fechaInicio, fechaFin);
        logReporte("Citas", "RANGO_FECHAS:" + fechaInicio + "_" + fechaFin, lista.size());
        return lista;
    }

    public List<Cita> reportePorMedico(String idMedico) throws IOException {
        List<Cita> lista = gestorCita.buscarPorMedico(idMedico);
        logReporte("Citas", "POR_MEDICO:" + idMedico, lista.size());
        return lista;
    }

    public List<Cita> reportePorPaciente(String cuiPaciente) throws IOException {
        List<Cita> lista = gestorCita.buscarPorPaciente(cuiPaciente);
        logReporte("Citas", "POR_PACIENTE:" + cuiPaciente, lista.size());
        return lista;
    }

    public List<Cita> reportePorEstado(String estado) throws IOException {
        List<Cita> lista = gestorCita.buscarPorEstado(estado);
        logReporte("Citas", "POR_ESTADO:" + estado, lista.size());
        return lista;
    }

    /**
     * Cantidad de citas por especialidad.
     */
    public List<ReporteCitasPorEspecialidad> reporteCitasPorEspecialidad() throws IOException {
        List<Cita> citas = gestorCita.listarActivas();
        List<Medico> medicos = gestorMedico.listarActivos();
        
        Map<String, String> medicoEspecialidad = medicos.stream()
            .collect(Collectors.toMap(Medico::getIdMedico, Medico::getEspecialidad));
        
        Map<String, Long> conteo = citas.stream()
            .map(c -> medicoEspecialidad.getOrDefault(c.getIdMedico(), "Sin especialidad"))
            .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        
        List<ReporteCitasPorEspecialidad> reporte = conteo.entrySet().stream()
            .map(e -> new ReporteCitasPorEspecialidad(e.getKey(), e.getValue()))
            .sorted(Comparator.comparingLong(ReporteCitasPorEspecialidad::getCantidad).reversed())
            .collect(Collectors.toList());
        
        logReporte("Citas", "POR_ESPECIALIDAD", reporte.size());
        return reporte;
    }

    // ==================== REPORTE DE LOGS ====================
    
    public List<GestorArchivoLog.LogEntry> reporteLogs(String modulo, String operacion, 
                                                        String entidad, String fechaInicio, 
                                                        String fechaFin, String usuario) 
            throws IOException {
        
        List<GestorArchivoLog.LogEntry> logs = gestorLog.listarTodos();
        
        if (modulo != null && !modulo.isEmpty()) {
            logs = logs.stream().filter(l -> l.modulo.equalsIgnoreCase(modulo)).collect(Collectors.toList());
        }
        if (operacion != null && !operacion.isEmpty()) {
            logs = logs.stream().filter(l -> l.operacion.equalsIgnoreCase(operacion)).collect(Collectors.toList());
        }
        if (entidad != null && !entidad.isEmpty()) {
            logs = logs.stream().filter(l -> l.entidad.equalsIgnoreCase(entidad)).collect(Collectors.toList());
        }
        if (fechaInicio != null && !fechaInicio.isEmpty()) {
            logs = logs.stream().filter(l -> compararFechaHora(l.timestamp, fechaInicio + " 00:00:00") >= 0).collect(Collectors.toList());
        }
        if (fechaFin != null && !fechaFin.isEmpty()) {
            logs = logs.stream().filter(l -> compararFechaHora(l.timestamp, fechaFin + " 23:59:59") <= 0).collect(Collectors.toList());
        }
        if (usuario != null && !usuario.isEmpty()) {
            logs = logs.stream().filter(l -> l.usuario.equalsIgnoreCase(usuario)).collect(Collectors.toList());
        }
        
        logReporte("Logs", "FILTRADO", logs.size());
        return logs;
    }

    // ==================== EXPORTACIÓN CSV / TEXTO ====================
    
    /**
     * Exporta lista de pacientes a CSV.
     */
    public void exportarPacientesCSV(List<Paciente> pacientes, String rutaArchivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            // Cabecera
            bw.write("CUI,NombresApellidos,FechaNacimiento,Sexo,Telefono,Correo,TipoSangre,Activo");
            bw.newLine();
            
            for (Paciente p : pacientes) {
                bw.write(String.join(",",
                    escapeCsv(p.getCui()),
                    escapeCsv(p.getNombresApellidos()),
                    escapeCsv(p.getFechaNacimiento()),
                    escapeCsv(p.getSexo()),
                    escapeCsv(p.getTelefono()),
                    escapeCsv(p.getCorreo()),
                    escapeCsv(p.getTipoSangre()),
                    p.isActivo() ? "Si" : "No"
                ));
                bw.newLine();
            }
        }
        gestorLog.registrar("Reportes", "EXPORTAR_CSV", "Paciente", "",
                "Exportados " + pacientes.size() + " pacientes a " + rutaArchivo, usuarioActual);
    }

    /**
     * Exporta lista de médicos a CSV.
     */
    public void exportarMedicosCSV(List<Medico> medicos, String rutaArchivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            bw.write("ID,NombresApellidos,Especialidad,Telefono,Correo,HorarioInicio,HorarioFin,Activo");
            bw.newLine();
            
            for (Medico m : medicos) {
                bw.write(String.join(",",
                    escapeCsv(m.getIdMedico()),
                    escapeCsv(m.getNombresApellidos()),
                    escapeCsv(m.getEspecialidad()),
                    escapeCsv(m.getTelefono()),
                    escapeCsv(m.getCorreo()),
                    escapeCsv(m.getHorarioInicio()),
                    escapeCsv(m.getHorarioFin()),
                    m.isActivo() ? "Si" : "No"
                ));
                bw.newLine();
            }
        }
        gestorLog.registrar("Reportes", "EXPORTAR_CSV", "Medico", "",
                "Exportados " + medicos.size() + " médicos a " + rutaArchivo, usuarioActual);
    }

    /**
     * Exporta lista de citas a CSV.
     */
    public void exportarCitasCSV(List<Cita> citas, String rutaArchivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            bw.write("ID,CUI_Paciente,ID_Medico,Fecha,HoraInicio,Motivo,Estado,Observaciones,Activa");
            bw.newLine();
            
            for (Cita c : citas) {
                bw.write(String.join(",",
                    escapeCsv(c.getIdCita()),
                    escapeCsv(c.getCuiPaciente()),
                    escapeCsv(c.getIdMedico()),
                    escapeCsv(c.getFecha()),
                    escapeCsv(c.getHoraInicio()),
                    escapeCsv(c.getMotivo()),
                    escapeCsv(c.getEstado()),
                    escapeCsv(c.getObservaciones()),
                    c.isActiva() ? "Si" : "No"
                ));
                bw.newLine();
            }
        }
        gestorLog.registrar("Reportes", "EXPORTAR_CSV", "Cita", "",
                "Exportadas " + citas.size() + " citas a " + rutaArchivo, usuarioActual);
    }

    /**
     * Exporta logs a CSV.
     */
    public void exportarLogsCSV(List<GestorArchivoLog.LogEntry> logs, String rutaArchivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            bw.write("Timestamp,Modulo,Operacion,Entidad,ID_Entidad,Detalles,Usuario");
            bw.newLine();
            
            for (GestorArchivoLog.LogEntry l : logs) {
                bw.write(String.join(",",
                    escapeCsv(l.timestamp),
                    escapeCsv(l.modulo),
                    escapeCsv(l.operacion),
                    escapeCsv(l.entidad),
                    escapeCsv(l.idEntidad),
                    escapeCsv(l.detalles),
                    escapeCsv(l.usuario)
                ));
                bw.newLine();
            }
        }
        gestorLog.registrar("Reportes", "EXPORTAR_CSV", "Log", "",
                "Exportados " + logs.size() + " logs a " + rutaArchivo, usuarioActual);
    }

    /**
     * Exporta reporte genérico a texto plano (formato tabular simple).
     */
    public void exportarTextoPlano(String titulo, List<String[]> filas, String[] cabeceras, String rutaArchivo) 
            throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            bw.write("=== " + titulo + " ===");
            bw.newLine();
            bw.newLine();
            
            // Cabeceras
            bw.write(String.join(" | ", cabeceras));
            bw.newLine();
            bw.write(String.join("-+-", Collections.nCopies(cabeceras.length, "---")));
            bw.newLine();
            
            // Filas
            for (String[] fila : filas) {
                bw.write(String.join(" | ", fila));
                bw.newLine();
            }
        }
        gestorLog.registrar("Reportes", "EXPORTAR_TXT", "Reporte", "",
                "Exportado reporte '" + titulo + "' a " + rutaArchivo, usuarioActual);
    }

    // ==================== HELPERS ====================
    
    private String escapeCsv(String valor) {
        if (valor == null) return "";
        String v = valor.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            v = "\"" + v + "\"";
        }
        return v;
    }

    private int compararFechaHora(String f1, String f2) {
        try {
            String[] p1 = f1.split(" ")[0].split("/");
            String[] p2 = f2.split(" ")[0].split("/");
            String[] t1 = f1.split(" ")[1].split(":");
            String[] t2 = f2.split(" ")[1].split(":");
            int y1 = Integer.parseInt(p1[2]), m1 = Integer.parseInt(p1[1]), d1 = Integer.parseInt(p1[0]);
            int y2 = Integer.parseInt(p2[2]), m2 = Integer.parseInt(p2[1]), d2 = Integer.parseInt(p2[0]);
            int h1 = Integer.parseInt(t1[0]), min1 = Integer.parseInt(t1[1]), s1 = Integer.parseInt(t1[2]);
            int h2 = Integer.parseInt(t2[0]), min2 = Integer.parseInt(t2[1]), s2 = Integer.parseInt(t2[2]);
            if (y1 != y2) return Integer.compare(y1, y2);
            if (m1 != m2) return Integer.compare(m1, m2);
            if (d1 != d2) return Integer.compare(d1, d2);
            if (h1 != h2) return Integer.compare(h1, h2);
            if (min1 != min2) return Integer.compare(min1, min2);
            return Integer.compare(s1, s2);
        } catch (Exception e) {
            return 0;
        }
    }

    private void logReporte(String modulo, String tipo, int cantidad) throws IOException {
        gestorLog.registrar("Reportes", "GENERAR", modulo, "",
                "Reporte " + tipo + " - " + cantidad + " registros", usuarioActual);
    }

    // ==================== CLASES DTO PARA REPORTES COMPLEJOS ====================
    
    public static class ReportePacienteCitas {
        private final String cui;
        private final String nombres;
        private final String tipoSangre;
        private final long totalCitas;

        public ReportePacienteCitas(String cui, String nombres, String tipoSangre, long totalCitas) {
            this.cui = cui; this.nombres = nombres; this.tipoSangre = tipoSangre; this.totalCitas = totalCitas;
        }
        public String getCui() { return cui; }
        public String getNombres() { return nombres; }
        public String getTipoSangre() { return tipoSangre; }
        public long getTotalCitas() { return totalCitas; }
    }

    public static class ReporteMedicoCitas {
        private final String idMedico;
        private final String nombres;
        private final String especialidad;
        private final long totalCitas;

        public ReporteMedicoCitas(String idMedico, String nombres, String especialidad, long totalCitas) {
            this.idMedico = idMedico; this.nombres = nombres; this.especialidad = especialidad; this.totalCitas = totalCitas;
        }
        public String getIdMedico() { return idMedico; }
        public String getNombres() { return nombres; }
        public String getEspecialidad() { return especialidad; }
        public long getTotalCitas() { return totalCitas; }
    }

    public static class ReporteMedicoCitasFecha {
        private final String idMedico;
        private final String nombres;
        private final String especialidad;
        private final List<Cita> citas;

        public ReporteMedicoCitasFecha(String idMedico, String nombres, String especialidad, List<Cita> citas) {
            this.idMedico = idMedico; this.nombres = nombres; this.especialidad = especialidad; this.citas = citas;
        }
        public String getIdMedico() { return idMedico; }
        public String getNombres() { return nombres; }
        public String getEspecialidad() { return especialidad; }
        public List<Cita> getCitas() { return citas; }
        public int getTotalCitas() { return citas.size(); }
    }

    public static class ReporteCitasPorEspecialidad {
        private final String especialidad;
        private final long cantidad;

        public ReporteCitasPorEspecialidad(String especialidad, long cantidad) {
            this.especialidad = especialidad; this.cantidad = cantidad;
        }
        public String getEspecialidad() { return especialidad; }
        public long getCantidad() { return cantidad; }
    }
}