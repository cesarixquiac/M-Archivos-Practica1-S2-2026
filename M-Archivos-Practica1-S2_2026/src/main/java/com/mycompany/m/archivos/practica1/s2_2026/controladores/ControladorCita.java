package com.mycompany.m.archivos.practica1.s2_2026.controladores;

import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoCita;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoLog;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoMedico;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoPaciente;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Cita;
import java.io.IOException;
import java.util.List;

/**
 * Controlador de lógica de negocio para Citas.
 * Valida integridad referencial (paciente/médico existen, médico activo),
 * anti-solapamiento, y coordina persistencia + auditoría.
 */
public class ControladorCita {

    private final GestorArchivoCita gestorCita;
    private final GestorArchivoPaciente gestorPaciente;
    private final GestorArchivoMedico gestorMedico;
    private final GestorArchivoLog gestorLog;
    private final String usuarioActual;

    public ControladorCita(GestorArchivoCita gestorCita,
                           GestorArchivoPaciente gestorPaciente,
                           GestorArchivoMedico gestorMedico,
                           GestorArchivoLog gestorLog,
                           String usuarioActual) {
        this.gestorCita = gestorCita;
        this.gestorPaciente = gestorPaciente;
        this.gestorMedico = gestorMedico;
        this.gestorLog = gestorLog;
        this.usuarioActual = usuarioActual;
    }

    // ==================== CREAR (PROGRAMAR) ====================
    
    /**
     * Programa una nueva cita.
     * Validaciones:
     * 1. Paciente existe y está activo
     * 2. Médico existe y está activo
     * 3. No hay solapamiento con otras citas del mismo médico en misma fecha/hora
     * 4. Formato fecha/hora válido
     */
    public void programarCita(String cuiPaciente, String idMedico, String fecha,
                              String horaInicio, String motivo, String observaciones) 
            throws IOException, IllegalArgumentException {
        
        // 1. Validar paciente existe y está activo
        validarPacienteActivo(cuiPaciente);
        
        // 2. Validar médico existe y está activo
        validarMedicoActivo(idMedico);
        
        // 3. Validar solapamiento (duración fija 30 min por ahora)
        if (gestorCita.existeSolapamiento(idMedico, fecha, horaInicio, null)) {
            throw new IllegalArgumentException(
                "El médico ya tiene una cita programada a esa hora en esa fecha (solapamiento de 30 min)");
        }
        
        // 4. Crear y guardar
        Cita cita = new Cita(cuiPaciente, idMedico, fecha, horaInicio, motivo, observaciones);
        gestorCita.crear(cita);
        
        // Auditoría
        gestorLog.registrar("Citas", "CREAR", "Cita", cita.getIdCita(),
                "Cita programada: Paciente=" + cuiPaciente + ", Medico=" + idMedico 
                + ", Fecha=" + fecha + " " + horaInicio + ", Motivo=" + motivo, 
                usuarioActual);
    }

    // ==================== BUSCAR ====================
    
    public Cita buscarPorId(String idCita) throws IOException {
        Cita c = gestorCita.buscarPorId(idCita);
        if (c != null) {
            gestorLog.registrar("Citas", "BUSCAR", "Cita", idCita,
                    "Búsqueda por ID", usuarioActual);
        }
        return c;
    }

    public List<Cita> buscarPorPaciente(String cuiPaciente) throws IOException {
        validarPacienteExiste(cuiPaciente); // Solo valida existencia, no estado
        List<Cita> resultados = gestorCita.buscarPorPaciente(cuiPaciente);
        gestorLog.registrar("Citas", "BUSCAR", "Cita", "",
                "Búsqueda por paciente: " + cuiPaciente + " - " + resultados.size() + " citas", usuarioActual);
        return resultados;
    }

    public List<Cita> buscarPorMedico(String idMedico) throws IOException {
        validarMedicoExiste(idMedico);
        List<Cita> resultados = gestorCita.buscarPorMedico(idMedico);
        gestorLog.registrar("Citas", "BUSCAR", "Cita", "",
                "Búsqueda por médico: " + idMedico + " - " + resultados.size() + " citas", usuarioActual);
        return resultados;
    }

    public List<Cita> buscarPorFecha(String fecha) throws IOException {
        List<Cita> resultados = gestorCita.buscarPorFecha(fecha);
        gestorLog.registrar("Citas", "BUSCAR", "Cita", "",
                "Búsqueda por fecha: " + fecha + " - " + resultados.size() + " citas", usuarioActual);
        return resultados;
    }

    public List<Cita> buscarPorRangoFechas(String fechaInicio, String fechaFin) throws IOException {
        List<Cita> resultados = gestorCita.buscarPorRangoFechas(fechaInicio, fechaFin);
        gestorLog.registrar("Citas", "BUSCAR", "Cita", "",
                "Búsqueda por rango: " + fechaInicio + " a " + fechaFin 
                + " - " + resultados.size() + " citas", usuarioActual);
        return resultados;
    }

    public List<Cita> buscarPorEstado(String estado) throws IOException {
        List<Cita> resultados = gestorCita.buscarPorEstado(estado);
        gestorLog.registrar("Citas", "BUSCAR", "Cita", "",
                "Búsqueda por estado: " + estado + " - " + resultados.size() + " citas", usuarioActual);
        return resultados;
    }

    public List<Cita> listarActivas() throws IOException {
        List<Cita> lista = gestorCita.listarActivas();
        gestorLog.registrar("Citas", "LISTAR", "Cita", "",
                "Listado citas activas - " + lista.size() + " citas", usuarioActual);
        return lista;
    }

    public List<Cita> listarTodas() throws IOException {
        List<Cita> lista = gestorCita.listarTodas();
        gestorLog.registrar("Citas", "LISTAR", "Cita", "",
                "Listado completo - " + lista.size() + " citas", usuarioActual);
        return lista;
    }

    // ==================== ACCIONES DE ESTADO ====================
    
    /**
     * Cancela una cita (cambia estado a "Cancelada").
     */
    public boolean cancelarCita(String idCita) throws IOException {
        Cita cita = gestorCita.buscarPorId(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada con ID: " + idCita);
        }
        
        boolean ok = gestorCita.cancelar(idCita);
        if (ok) {
            gestorLog.registrar("Citas", "CANCELAR", "Cita", idCita,
                    "Cita cancelada: Paciente=" + cita.getCuiPaciente() 
                    + ", Fecha=" + cita.getFecha() + " " + cita.getHoraInicio(), 
                    usuarioActual);
        }
        return ok;
    }

    /**
     * Marca una cita como atendida.
     */
    public boolean marcarAtendida(String idCita) throws IOException {
        Cita cita = gestorCita.buscarPorId(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada con ID: " + idCita);
        }
        
        boolean ok = gestorCita.marcarAtendida(idCita);
        if (ok) {
            gestorLog.registrar("Citas", "ATENDER", "Cita", idCita,
                    "Cita marcada como atendida: Paciente=" + cita.getCuiPaciente(), 
                    usuarioActual);
        }
        return ok;
    }

    /**
     * Reprograma una cita (cambia fecha/hora/médico).
     * Revalida integridad y solapamiento.
     */
    public boolean reprogramarCita(String idCita, String nuevaFecha, String nuevaHora, 
                                   String nuevoIdMedico, String nuevoMotivo, 
                                   String nuevasObservaciones) 
            throws IOException, IllegalArgumentException {
        
        Cita cita = gestorCita.buscarPorId(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada con ID: " + idCita);
        }
        
        // Si cambia de médico, validar el nuevo
        String idMedicoFinal = (nuevoIdMedico != null && !nuevoIdMedico.isEmpty()) ? nuevoIdMedico : cita.getIdMedico();
        if (!idMedicoFinal.equals(cita.getIdMedico())) {
            validarMedicoActivo(idMedicoFinal);
        }
        
        // Validar solapamiento excluyendo la cita actual
        String fechaFinal = (nuevaFecha != null && !nuevaFecha.isEmpty()) ? nuevaFecha : cita.getFecha();
        String horaFinal = (nuevaHora != null && !nuevaHora.isEmpty()) ? nuevaHora : cita.getHoraInicio();
        
        if (gestorCita.existeSolapamiento(idMedicoFinal, fechaFinal, horaFinal, idCita)) {
            throw new IllegalArgumentException("Solapamiento con otra cita del médico en esa fecha/hora");
        }
        
        // Actualizar campos
        if (nuevaFecha != null && !nuevaFecha.isEmpty()) cita.setFecha(nuevaFecha);
        if (nuevaHora != null && !nuevaHora.isEmpty()) cita.setHoraInicio(nuevaHora);
        if (nuevoIdMedico != null && !nuevoIdMedico.isEmpty()) cita.setIdMedico(nuevoIdMedico);
        if (nuevoMotivo != null && !nuevoMotivo.isEmpty()) cita.setMotivo(nuevoMotivo);
        if (nuevasObservaciones != null) cita.setObservaciones(nuevasObservaciones);
        cita.setEstado(Cita.EST_PROGRAMADA); // Volver a programada al reprogramar
        
        boolean ok = gestorCita.actualizar(cita);
        if (ok) {
            gestorLog.registrar("Citas", "REPROGRAMAR", "Cita", idCita,
                    "Cita reprogramada: Fecha=" + fechaFinal + " " + horaFinal 
                    + ", Medico=" + idMedicoFinal, usuarioActual);
        }
        return ok;
    }

    /**
     * Actualiza solo motivo u observaciones (sin cambiar fecha/hora/médico).
     */
    public boolean actualizarDetalles(String idCita, String motivo, String observaciones) 
            throws IOException {
        Cita cita = gestorCita.buscarPorId(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada con ID: " + idCita);
        }
        
        if (motivo != null && !motivo.isEmpty()) cita.setMotivo(motivo);
        if (observaciones != null) cita.setObservaciones(observaciones);
        
        boolean ok = gestorCita.actualizar(cita);
        if (ok) {
            gestorLog.registrar("Citas", "ACTUALIZAR", "Cita", idCita,
                    "Detalles actualizados: motivo/observaciones", usuarioActual);
        }
        return ok;
    }

    // ==================== ELIMINAR ====================
    
    /**
     * Elimina lógicamente una cita.
     */
    public boolean eliminarCita(String idCita) throws IOException {
        Cita cita = gestorCita.buscarPorId(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("Cita no encontrada con ID: " + idCita);
        }
        
        boolean ok = gestorCita.eliminarLogico(idCita);
        if (ok) {
            gestorLog.registrar("Citas", "ELIMINAR", "Cita", idCita,
                    "Eliminación lógica: Paciente=" + cita.getCuiPaciente(), usuarioActual);
        }
        return ok;
    }

    // ==================== VALIDACIONES DE INTEGRIDAD REFERENCIAL ====================
    
    private void validarPacienteActivo(String cuiPaciente) throws IOException, IllegalArgumentException {
        if (!gestorPaciente.existeCui(cuiPaciente)) {
            throw new IllegalArgumentException("Paciente no existe con CUI: " + cuiPaciente);
        }
        if (gestorPaciente.buscarPorCui(cuiPaciente) == null) {
            throw new IllegalArgumentException("Paciente inactivo/eliminado con CUI: " + cuiPaciente);
        }
    }

    private void validarPacienteExiste(String cuiPaciente) throws IOException, IllegalArgumentException {
        if (!gestorPaciente.existeCui(cuiPaciente)) {
            throw new IllegalArgumentException("Paciente no existe con CUI: " + cuiPaciente);
        }
    }

    private void validarMedicoActivo(String idMedico) throws IOException, IllegalArgumentException {
        if (!gestorMedico.existeId(idMedico)) {
            throw new IllegalArgumentException("Médico no existe con ID: " + idMedico);
        }
        if (gestorMedico.buscarPorId(idMedico) == null) {
            throw new IllegalArgumentException("Médico inactivo con ID: " + idMedico);
        }
    }

    private void validarMedicoExiste(String idMedico) throws IOException, IllegalArgumentException {
        if (!gestorMedico.existeId(idMedico)) {
            throw new IllegalArgumentException("Médico no existe con ID: " + idMedico);
        }
    }

    // ==================== ESTADÍSTICAS ====================
    
    public int contarActivas() throws IOException {
        return gestorCita.contarActivas();
    }

    public int contarPorEstado(String estado) throws IOException {
        return gestorCita.contarPorEstado(estado);
    }

    public boolean existeId(String idCita) throws IOException {
        return gestorCita.existeId(idCita);
    }
}