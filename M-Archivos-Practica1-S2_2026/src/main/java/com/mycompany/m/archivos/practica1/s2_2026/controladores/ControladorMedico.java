package com.mycompany.m.archivos.practica1.s2_2026.controladores;

import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoLog;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoMedico;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Medico;
import java.io.IOException;
import java.util.List;

/**
 * Controlador de lógica de negocio para Médicos.
 * Valida reglas de negocio, coordina persistencia y registra auditoría.
 */
public class ControladorMedico {

    private final GestorArchivoMedico gestorMedico;
    private final GestorArchivoLog gestorLog;
    private final String usuarioActual;

    public ControladorMedico(GestorArchivoMedico gestorMedico, 
                             GestorArchivoLog gestorLog, 
                             String usuarioActual) {
        this.gestorMedico = gestorMedico;
        this.gestorLog = gestorLog;
        this.usuarioActual = usuarioActual;
    }

    // ==================== CREAR ====================
    
    public void crearMedico(String nombresApellidos, String especialidad, String telefono,
                            String correo, String horarioInicio, String horarioFin) 
            throws IOException, IllegalArgumentException {
        
        validarDatosObligatorios(nombresApellidos, especialidad);
        
        Medico medico = new Medico(nombresApellidos, especialidad, telefono, 
                                   correo, horarioInicio, horarioFin);
        
        gestorMedico.crear(medico);
        
        gestorLog.registrar("Medicos", "CREAR", "Medico", medico.getIdMedico(),
                "Nuevo médico: " + nombresApellidos + " - " + especialidad, usuarioActual);
    }

    // ==================== BUSCAR ====================
    
    public Medico buscarPorId(String idMedico) throws IOException {
        Medico m = gestorMedico.buscarPorId(idMedico);
        if (m != null) {
            gestorLog.registrar("Medicos", "BUSCAR", "Medico", idMedico,
                    "Búsqueda por ID: " + m.getNombresApellidos(), usuarioActual);
        }
        return m;
    }

    public List<Medico> buscarPorNombre(String nombre) throws IOException {
        List<Medico> resultados = gestorMedico.buscarPorNombre(nombre);
        gestorLog.registrar("Medicos", "BUSCAR", "Medico", "",
                "Búsqueda por nombre: '" + nombre + "' - " + resultados.size() + " resultados", usuarioActual);
        return resultados;
    }

    public List<Medico> buscarPorEspecialidad(String especialidad) throws IOException {
        List<Medico> resultados = gestorMedico.buscarPorEspecialidad(especialidad);
        gestorLog.registrar("Medicos", "BUSCAR", "Medico", "",
                "Búsqueda por especialidad: " + especialidad + " - " + resultados.size() + " resultados", usuarioActual);
        return resultados;
    }

    public List<Medico> listarActivos() throws IOException {
        List<Medico> lista = gestorMedico.listarActivos();
        gestorLog.registrar("Medicos", "LISTAR", "Medico", "",
                "Listado activos - " + lista.size() + " médicos", usuarioActual);
        return lista;
    }

    public List<Medico> listarInactivos() throws IOException {
        List<Medico> lista = gestorMedico.listarInactivos();
        gestorLog.registrar("Medicos", "LISTAR", "Medico", "",
                "Listado inactivos - " + lista.size() + " médicos", usuarioActual);
        return lista;
    }

    public List<Medico> listarTodos() throws IOException {
        List<Medico> lista = gestorMedico.listarTodos();
        gestorLog.registrar("Medicos", "LISTAR", "Medico", "",
                "Listado completo - " + lista.size() + " médicos", usuarioActual);
        return lista;
    }

    // ==================== ACTUALIZAR ====================
    
    /**
     * Actualiza datos de un médico.
     * NOTA: Cambio de horario NO valida citas existentes (requiere ControladorCita).
     * Se registra en log para trazabilidad.
     */
    public boolean actualizarMedico(String idMedico, String nombresApellidos, 
                                    String especialidad, String telefono,
                                    String correo, String horarioInicio, String horarioFin) 
            throws IOException, IllegalArgumentException {
        
        Medico existente = gestorMedico.buscarPorId(idMedico);
        if (existente == null) {
            throw new IllegalArgumentException("Médico no encontrado con ID: " + idMedico);
        }
        
        validarDatosObligatorios(nombresApellidos, especialidad);
        
        // Detectar cambio de horario para log
        boolean horarioCambiado = !existente.getHorarioInicio().equals(horarioInicio) 
                                 || !existente.getHorarioFin().equals(horarioFin);
        
        Medico actualizado = new Medico(idMedico, nombresApellidos, especialidad,
                                        telefono, correo, horarioInicio, horarioFin, 
                                        existente.isActivo());
        
        boolean ok = gestorMedico.actualizar(actualizado);
        
        if (ok) {
            String detalle = "Datos actualizados: " + nombresApellidos;
            if (horarioCambiado) {
                detalle += " | HORARIO CAMBIADO: " + existente.getHorarioInicio() + "-" + existente.getHorarioFin() 
                         + " -> " + horarioInicio + "-" + horarioFin;
            }
            gestorLog.registrar("Medicos", "ACTUALIZAR", "Medico", idMedico, detalle, usuarioActual);
        }
        return ok;
    }

    // ==================== ACTIVAR / DESACTIVAR ====================
    
    public boolean activarMedico(String idMedico) throws IOException {
        Medico m = gestorMedico.buscarPorId(idMedico);
        if (m == null) {
            // Buscar en inactivos
            for (Medico med : gestorMedico.listarInactivos()) {
                if (med.getIdMedico().equals(idMedico)) {
                    m = med;
                    break;
                }
            }
        }
        if (m == null) {
            throw new IllegalArgumentException("Médico no encontrado con ID: " + idMedico);
        }
        
        boolean ok = gestorMedico.activar(idMedico);
        if (ok) {
            gestorLog.registrar("Medicos", "ACTIVAR", "Medico", idMedico,
                    "Médico activado: " + m.getNombresApellidos(), usuarioActual);
        }
        return ok;
    }

    public boolean desactivarMedico(String idMedico) throws IOException {
        Medico m = gestorMedico.buscarPorId(idMedico);
        if (m == null) {
            throw new IllegalArgumentException("Médico no encontrado con ID: " + idMedico);
        }
        
        // TODO: Validar que no tenga citas programadas futuras (requiere ControladorCita)
        // Por ahora permitimos desactivar
        
        boolean ok = gestorMedico.desactivar(idMedico);
        if (ok) {
            gestorLog.registrar("Medicos", "DESACTIVAR", "Medico", idMedico,
                    "Médico desactivado: " + m.getNombresApellidos(), usuarioActual);
        }
        return ok;
    }

    // ==================== VALIDACIONES ====================
    
    private void validarDatosObligatorios(String nombres, String especialidad) 
            throws IllegalArgumentException {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombres y apellidos son obligatorios");
        }
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("Especialidad es obligatoria");
        }
    }

    // ==================== ESTADÍSTICAS ====================
    
    public int contarActivos() throws IOException {
        return gestorMedico.contarActivos();
    }

    public boolean existeId(String idMedico) throws IOException {
        return gestorMedico.existeId(idMedico);
    }
}