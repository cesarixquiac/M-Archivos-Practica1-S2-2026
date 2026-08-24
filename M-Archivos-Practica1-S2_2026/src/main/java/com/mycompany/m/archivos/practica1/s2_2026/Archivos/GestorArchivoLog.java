package com.mycompany.m.archivos.practica1.s2_2026.Archivos;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de logs de auditoría usando RandomAccessFile.
 * Archivo: logs.dat - Registros de longitud fija.
 * Registra: timestamp, módulo, operación, entidad, ID entidad, detalles, usuario.
 */
public class GestorArchivoLog {

    private static final String NOMBRE_ARCHIVO = "logs.dat";
    private final RandomAccessFile archivo;
    private final long tamanioRegistro;
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Longitudes fijas (en caracteres)
    public static final int TAM_TIMESTAMP = 19;    // dd/MM/yyyy HH:mm:ss
    public static final int TAM_MODULO = 20;       // Pacientes, Medicos, Citas, Reportes
    public static final int TAM_OPERACION = 15;    // CREAR, ACTUALIZAR, ELIMINAR, BUSCAR, EXPORTAR
    public static final int TAM_ENTIDAD = 15;      // Paciente, Medico, Cita
    public static final int TAM_ID_ENTIDAD = 36;   // UUID o CUI
    public static final int TAM_DETALLES = 200;    // Descripción de la operación
    public static final int TAM_USUARIO = 30;      // Usuario que realizó la acción

    public GestorArchivoLog() throws IOException {
        File f = new File(NOMBRE_ARCHIVO);
        this.archivo = new RandomAccessFile(f, "rw");
        this.tamanioRegistro = 
            (TAM_TIMESTAMP + TAM_MODULO + TAM_OPERACION + TAM_ENTIDAD + TAM_ID_ENTIDAD + TAM_DETALLES + TAM_USUARIO) * 2 + 2;
    }

    private void validarArchivoAbierto() throws IOException {
        if (archivo == null || !archivo.getChannel().isOpen()) {
            throw new IOException("Archivo de logs no está abierto");
        }
    }

    private long calcularPosicion(long indice) {
        return indice * tamanioRegistro;
    }

    private int obtenerTotalRegistros() throws IOException {
        validarArchivoAbierto();
        return (int) (archivo.length() / tamanioRegistro);
    }

    // ==================== REGISTRO DE LOGS ====================

    /**
     * Registra una operación en el log de auditoría.
     * @param modulo Módulo: "Pacientes", "Medicos", "Citas", "Reportes", "Sistema"
     * @param operacion Operación: "CREAR", "ACTUALIZAR", "ELIMINAR", "BUSCAR", "EXPORTAR", "LOGIN", "LOGOUT"
     * @param entidad Tipo de entidad: "Paciente", "Medico", "Cita"
     * @param idEntidad ID de la entidad afectada (CUI para pacientes, UUID para médicos/citas)
     * @param detalles Descripción detallada
     * @param usuario Usuario que realizó la acción
     */
    public void registrar(String modulo, String operacion, String entidad, 
                          String idEntidad, String detalles, String usuario) throws IOException {
        validarArchivoAbierto();
        
        String timestamp = LocalDateTime.now().format(FECHA_HORA);
        
        archivo.seek(archivo.length()); // Append al final
        
        // Escribir cada campo con longitud fija
        escribirCampo(timestamp, TAM_TIMESTAMP);
        escribirCampo(modulo, TAM_MODULO);
        escribirCampo(operacion, TAM_OPERACION);
        escribirCampo(entidad, TAM_ENTIDAD);
        escribirCampo(idEntidad, TAM_ID_ENTIDAD);
        escribirCampo(detalles, TAM_DETALLES);
        escribirCampo(usuario, TAM_USUARIO);
        
        // Campo activo (siempre 1 para logs)
        archivo.writeChar('1');
    }

    private void escribirCampo(String valor, int longitud) throws IOException {
        if (valor == null) valor = "";
        StringBuffer buffer = new StringBuffer(valor);
        buffer.setLength(longitud);
        archivo.writeChars(buffer.toString());
    }

    private String leerCampo(int longitud) throws IOException {
        char[] campo = new char[longitud];
        for (int i = 0; i < longitud; i++) {
            campo[i] = archivo.readChar();
        }
        return new String(campo).replace('\0', ' ').trim();
    }

    // ==================== CONSULTA DE LOGS ====================

    /**
     * Obtiene todos los logs (más recientes primero).
     */
    public List<LogEntry> listarTodos() throws IOException {
        validarArchivoAbierto();
        List<LogEntry> logs = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = total - 1; i >= 0; i--) {
            LogEntry entry = leerEntrada(calcularPosicion(i));
            if (entry != null) {
                logs.add(entry);
            }
        }
        return logs;
    }

    /**
     * Obtiene logs filtrados por módulo.
     */
    public List<LogEntry> filtrarPorModulo(String modulo) throws IOException {
        List<LogEntry> todos = listarTodos();
        List<LogEntry> filtrados = new ArrayList<>();
        for (LogEntry e : todos) {
            if (e.modulo.equalsIgnoreCase(modulo)) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    /**
     * Obtiene logs filtrados por operación.
     */
    public List<LogEntry> filtrarPorOperacion(String operacion) throws IOException {
        List<LogEntry> todos = listarTodos();
        List<LogEntry> filtrados = new ArrayList<>();
        for (LogEntry e : todos) {
            if (e.operacion.equalsIgnoreCase(operacion)) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    /**
     * Obtiene logs filtrados por entidad.
     */
    public List<LogEntry> filtrarPorEntidad(String entidad) throws IOException {
        List<LogEntry> todos = listarTodos();
        List<LogEntry> filtrados = new ArrayList<>();
        for (LogEntry e : todos) {
            if (e.entidad.equalsIgnoreCase(entidad)) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    /**
     * Obtiene logs filtrados por rango de fechas.
     */
    public List<LogEntry> filtrarPorFecha(String fechaInicio, String fechaFin) throws IOException {
        List<LogEntry> todos = listarTodos();
        List<LogEntry> filtrados = new ArrayList<>();
        
        for (LogEntry e : todos) {
            int cmpI = compararFechaHora(e.timestamp, fechaInicio + " 00:00:00");
            int cmpF = compararFechaHora(e.timestamp, fechaFin + " 23:59:59");
            if (cmpI >= 0 && cmpF <= 0) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    /**
     * Obtiene logs filtrados por usuario.
     */
    public List<LogEntry> filtrarPorUsuario(String usuario) throws IOException {
        List<LogEntry> todos = listarTodos();
        List<LogEntry> filtrados = new ArrayList<>();
        for (LogEntry e : todos) {
            if (e.usuario.equalsIgnoreCase(usuario)) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    /**
     * Busca logs por ID de entidad específico.
     */
    public List<LogEntry> buscarPorIdEntidad(String idEntidad) throws IOException {
        List<LogEntry> todos = listarTodos();
        List<LogEntry> filtrados = new ArrayList<>();
        for (LogEntry e : todos) {
            if (e.idEntidad.equals(idEntidad)) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    private int compararFechaHora(String f1, String f2) {
        try {
            // Formato: dd/MM/yyyy HH:mm:ss
            String[] parts1 = f1.split(" ");
            String[] parts2 = f2.split(" ");
            String[] d1 = parts1[0].split("/");
            String[] d2 = parts2[0].split("/");
            String[] t1 = parts1[1].split(":");
            String[] t2 = parts2[1].split(":");
            
            int y1 = Integer.parseInt(d1[2]), m1 = Integer.parseInt(d1[1]), d1d = Integer.parseInt(d1[0]);
            int y2 = Integer.parseInt(d2[2]), m2 = Integer.parseInt(d2[1]), d2d = Integer.parseInt(d2[0]);
            int h1 = Integer.parseInt(t1[0]), min1 = Integer.parseInt(t1[1]), s1 = Integer.parseInt(t1[2]);
            int h2 = Integer.parseInt(t2[0]), min2 = Integer.parseInt(t2[1]), s2 = Integer.parseInt(t2[2]);
            
            if (y1 != y2) return Integer.compare(y1, y2);
            if (m1 != m2) return Integer.compare(m1, m2);
            if (d1d != d2d) return Integer.compare(d1d, d2d);
            if (h1 != h2) return Integer.compare(h1, h2);
            if (min1 != min2) return Integer.compare(min1, min2);
            return Integer.compare(s1, s2);
        } catch (Exception e) {
            return 0;
        }
    }

    private LogEntry leerEntrada(long posicion) throws IOException {
        archivo.seek(posicion);
        LogEntry entry = new LogEntry();
        entry.timestamp = leerCampo(TAM_TIMESTAMP);
        entry.modulo = leerCampo(TAM_MODULO);
        entry.operacion = leerCampo(TAM_OPERACION);
        entry.entidad = leerCampo(TAM_ENTIDAD);
        entry.idEntidad = leerCampo(TAM_ID_ENTIDAD);
        entry.detalles = leerCampo(TAM_DETALLES);
        entry.usuario = leerCampo(TAM_USUARIO);
        entry.activo = (archivo.readChar() == '1');
        return entry;
    }

    public void cerrar() throws IOException {
        if (archivo != null) {
            archivo.close();
        }
    }

    // Clase interna para representar una entrada de log
    public static class LogEntry {
        public String timestamp;
        public String modulo;
        public String operacion;
        public String entidad;
        public String idEntidad;
        public String detalles;
        public String usuario;
        public boolean activo;

        @Override
        public String toString() {
            return String.format("[%s] %s | %s | %s | ID:%s | %s | User:%s",
                    timestamp, modulo, operacion, entidad, idEntidad, detalles, usuario);
        }
    }
}