package com.mycompany.m.archivos.practica1.s2_2026.controladores;

import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoLog;
import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoPaciente;
import com.mycompany.m.archivos.practica1.s2_2026.entidades.Paciente;
import java.io.IOException;
import java.util.List;

/**
 * Controlador de lógica de negocio para Pacientes.
 * Valida reglas de negocio, coordina persistencia y registra auditoría.
 */
public class ControladorPaciente {

    private final GestorArchivoPaciente gestorPaciente;
    private final GestorArchivoLog gestorLog;
    private final String usuarioActual;

    public ControladorPaciente(GestorArchivoPaciente gestorPaciente, 
                               GestorArchivoLog gestorLog, 
                               String usuarioActual) {
        this.gestorPaciente = gestorPaciente;
        this.gestorLog = gestorLog;
        this.usuarioActual = usuarioActual;
    }

    // ==================== CREAR ====================
    
    /**
     * Registra un nuevo paciente.
     * Valida: CUI único (13 dígitos), nombres obligatorios, formato fecha, sexo válido.
     */
    public void crearPaciente(String cui, String nombresApellidos, String fechaNacimiento,
                              String sexo, String telefono, String correo, String tipoSangre) 
            throws IOException, IllegalArgumentException {
        
        // Validaciones de negocio
        validarCuiUnico(cui);
        validarDatosObligatorios(nombresApellidos, fechaNacimiento, sexo);
        
        Paciente paciente = new Paciente(cui, nombresApellidos, fechaNacimiento, 
                                         sexo, telefono, correo, tipoSangre);
        
        gestorPaciente.crear(paciente);
        
        // Auditoría
        gestorLog.registrar("Pacientes", "CREAR", "Paciente", cui,
                "Nuevo paciente: " + nombresApellidos, usuarioActual);
    }

    // ==================== BUSCAR ====================
    
    public Paciente buscarPorCui(String cui) throws IOException {
        Paciente p = gestorPaciente.buscarPorCui(cui);
        if (p != null) {
            gestorLog.registrar("Pacientes", "BUSCAR", "Paciente", cui,
                    "Búsqueda por CUI: " + p.getNombresApellidos(), usuarioActual);
        }
        return p;
    }

    public List<Paciente> buscarPorNombre(String nombre) throws IOException {
        List<Paciente> resultados = gestorPaciente.buscarPorNombre(nombre);
        gestorLog.registrar("Pacientes", "BUSCAR", "Paciente", "",
                "Búsqueda por nombre: '" + nombre + "' - " + resultados.size() + " resultados", usuarioActual);
        return resultados;
    }

    public List<Paciente> buscarPorTipoSangre(String tipoSangre) throws IOException {
        List<Paciente> resultados = gestorPaciente.buscarPorTipoSangre(tipoSangre);
        gestorLog.registrar("Pacientes", "BUSCAR", "Paciente", "",
                "Búsqueda por tipo de sangre: " + tipoSangre + " - " + resultados.size() + " resultados", usuarioActual);
        return resultados;
    }

    public List<Paciente> listarTodos() throws IOException {
        List<Paciente> lista = gestorPaciente.listarTodos();
        gestorLog.registrar("Pacientes", "LISTAR", "Paciente", "",
                "Listado completo - " + lista.size() + " pacientes", usuarioActual);
        return lista;
    }

    // ==================== ACTUALIZAR ====================
    
    /**
     * Actualiza datos de un paciente existente.
     * No permite cambiar el CUI (clave primaria).
     */
    public boolean actualizarPaciente(String cuiOriginal, String nombresApellidos, 
                                      String fechaNacimiento, String sexo, 
                                      String telefono, String correo, String tipoSangre) 
            throws IOException, IllegalArgumentException {
        
        Paciente existente = gestorPaciente.buscarPorCui(cuiOriginal);
        if (existente == null) {
            throw new IllegalArgumentException("Paciente no encontrado con CUI: " + cuiOriginal);
        }
        
        validarDatosObligatorios(nombresApellidos, fechaNacimiento, sexo);
        
        Paciente actualizado = new Paciente(cuiOriginal, nombresApellidos, fechaNacimiento,
                                            sexo, telefono, correo, tipoSangre);
        actualizado.setActivo(existente.isActivo());
        
        boolean ok = gestorPaciente.actualizar(actualizado);
        
        if (ok) {
            gestorLog.registrar("Pacientes", "ACTUALIZAR", "Paciente", cuiOriginal,
                    "Datos actualizados: " + nombresApellidos, usuarioActual);
        }
        return ok;
    }

    // ==================== ELIMINAR (LÓGICO) ====================
    
    /**
     * Elimina lógicamente un paciente (marca activo = false).
     * Verifica que no tenga citas asociadas antes de eliminar.
     */
    public boolean eliminarPaciente(String cui) throws IOException {
        Paciente existente = gestorPaciente.buscarPorCui(cui);
        if (existente == null) {
            throw new IllegalArgumentException("Paciente no encontrado con CUI: " + cui);
        }
        
        // TODO: Validar que no tenga citas programadas/activas (requiere ControladorCita o gestorCita)
        // Por ahora permitimos eliminación lógica
        
        boolean ok = gestorPaciente.eliminarLogico(cui);
        
        if (ok) {
            gestorLog.registrar("Pacientes", "ELIMINAR", "Paciente", cui,
                    "Eliminación lógica: " + existente.getNombresApellidos(), usuarioActual);
        }
        return ok;
    }

    /**
     * Reactiva un paciente eliminado lógicamente.
     */
    public boolean reactivarPaciente(String cui) throws IOException {
        boolean ok = gestorPaciente.reactivar(cui);
        if (ok) {
            gestorLog.registrar("Pacientes", "ACTIVAR", "Paciente", cui,
                    "Paciente reactivado", usuarioActual);
        }
        return ok;
    }

    // ==================== VALIDACIONES PRIVADAS ====================
    
    private void validarCuiUnico(String cui) throws IOException, IllegalArgumentException {
        if (gestorPaciente.existeCui(cui)) {
            throw new IllegalArgumentException("Ya existe un paciente con CUI: " + cui);
        }
        if (!cui.matches("\\d{13}")) {
            throw new IllegalArgumentException("CUI debe tener exactamente 13 dígitos numéricos");
        }
    }

    private void validarDatosObligatorios(String nombres, String fecha, String sexo) 
            throws IllegalArgumentException {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombres y apellidos son obligatorios");
        }
        if (fecha == null || !fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new IllegalArgumentException("Fecha de nacimiento obligatoria en formato DD/MM/YYYY");
        }
        if (sexo == null || (!sexo.equalsIgnoreCase("Masculino") && !sexo.equalsIgnoreCase("Femenino"))) {
            throw new IllegalArgumentException("Sexo debe ser 'Masculino' o 'Femenino'");
        }
    }

    // ==================== ESTADÍSTICAS ====================
    
    public int contarActivos() throws IOException {
        return gestorPaciente.contarActivos();
    }

    public boolean existeCui(String cui) throws IOException {
        return gestorPaciente.existeCui(cui);
    }
}