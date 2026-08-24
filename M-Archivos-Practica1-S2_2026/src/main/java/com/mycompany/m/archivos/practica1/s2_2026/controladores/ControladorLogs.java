package com.mycompany.m.archivos.practica1.s2_2026.controladores;

import com.mycompany.m.archivos.practica1.s2_2026.Archivos.GestorArchivoLog;
import java.io.IOException;
import java.util.List;

/**
 * Controlador unificado para auditoría/logs.
 * Proporciona métodos convenientes para registrar operaciones desde cualquier módulo.
 */
public class ControladorLogs {

    private final GestorArchivoLog gestorLog;
    private final String usuarioActual;

    public ControladorLogs(GestorArchivoLog gestorLog, String usuarioActual) {
        this.gestorLog = gestorLog;
        this.usuarioActual = usuarioActual;
    }

    // ==================== REGISTRO DE OPERACIONES ESTÁNDAR ====================
    
    public void logCrear(String modulo, String entidad, String idEntidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "CREAR", entidad, idEntidad, detalles, usuarioActual);
    }

    public void logActualizar(String modulo, String entidad, String idEntidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "ACTUALIZAR", entidad, idEntidad, detalles, usuarioActual);
    }

    public void logEliminar(String modulo, String entidad, String idEntidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "ELIMINAR", entidad, idEntidad, detalles, usuarioActual);
    }

    public void logBuscar(String modulo, String entidad, String idEntidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "BUSCAR", entidad, idEntidad, detalles, usuarioActual);
    }

    public void logListar(String modulo, String entidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "LISTAR", entidad, "", detalles, usuarioActual);
    }

    public void logActivar(String modulo, String entidad, String idEntidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "ACTIVAR", entidad, idEntidad, detalles, usuarioActual);
    }

    public void logDesactivar(String modulo, String entidad, String idEntidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "DESACTIVAR", entidad, idEntidad, detalles, usuarioActual);
    }

    public void logCancelar(String modulo, String entidad, String idEntidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "CANCELAR", entidad, idEntidad, detalles, usuarioActual);
    }

    public void logAtender(String modulo, String entidad, String idEntidad, String detalles) throws IOException {
        gestorLog.registrar(modulo, "ATENDER", entidad, idEntidad, detalles, usuarioActual);
    }

    public void logExportar(String formato, String entidad, String detalles) throws IOException {
        gestorLog.registrar("Reportes", "EXPORTAR_" + formato.toUpperCase(), entidad, "", detalles, usuarioActual);
    }

    public void logLogin() throws IOException {
        gestorLog.registrar("Sistema", "LOGIN", "Usuario", usuarioActual, "Inicio de sesión", usuarioActual);
    }

    public void logLogout() throws IOException {
        gestorLog.registrar("Sistema", "LOGOUT", "Usuario", usuarioActual, "Cierre de sesión", usuarioActual);
    }

    // ==================== CONSULTA DE LOGS ====================
    
    public List<GestorArchivoLog.LogEntry> verTodos() throws IOException {
        return gestorLog.listarTodos();
    }

    public List<GestorArchivoLog.LogEntry> filtrarPorModulo(String modulo) throws IOException {
        return gestorLog.filtrarPorModulo(modulo);
    }

    public List<GestorArchivoLog.LogEntry> filtrarPorOperacion(String operacion) throws IOException {
        return gestorLog.filtrarPorOperacion(operacion);
    }

    public List<GestorArchivoLog.LogEntry> filtrarPorEntidad(String entidad) throws IOException {
        return gestorLog.filtrarPorEntidad(entidad);
    }

    public List<GestorArchivoLog.LogEntry> filtrarPorFecha(String fechaInicio, String fechaFin) throws IOException {
        return gestorLog.filtrarPorFecha(fechaInicio, fechaFin);
    }

    public List<GestorArchivoLog.LogEntry> filtrarPorUsuario(String usuario) throws IOException {
        return gestorLog.filtrarPorUsuario(usuario);
    }

    public List<GestorArchivoLog.LogEntry> buscarPorIdEntidad(String idEntidad) throws IOException {
        return gestorLog.buscarPorIdEntidad(idEntidad);
    }

    // ==================== ACCESO DIRECTO AL GESTOR ====================
    
    public GestorArchivoLog getGestorLog() {
        return gestorLog;
    }
}