package com.mycompany.m.archivos.practica1.s2_2026.Archivos;

import com.mycompany.m.archivos.practica1.s2_2026.entidades.Cita;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de persistencia para la entidad Cita usando RandomAccessFile.
 * Archivo: citas.dat - Registros de longitud fija.
 * Operaciones: CRUD + búsquedas por paciente/médico/fecha/estado + validación de solapamiento.
 */
public class GestorArchivoCita {

    private static final String NOMBRE_ARCHIVO = "citas.dat";
    private final RandomAccessFile archivo;
    private final long tamanioRegistro;
    private static final DateTimeFormatter HORA_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public GestorArchivoCita() throws IOException {
        File f = new File(NOMBRE_ARCHIVO);
        this.archivo = new RandomAccessFile(f, "rw");
        this.tamanioRegistro = Cita.getTamanioRegistro();
    }

    // ==================== MÉTODOS PRIVADOS AUXILIARES ====================
    
    private void validarArchivoAbierto() throws IOException {
        if (archivo == null || !archivo.getChannel().isOpen()) {
            throw new IOException("Archivo no está abierto");
        }
    }

    private long calcularPosicion(long indice) {
        return indice * tamanioRegistro;
    }

    private int obtenerTotalRegistros() throws IOException {
        validarArchivoAbierto();
        long tamArchivo = archivo.length();
        return (int) (tamArchivo / tamanioRegistro);
    }

    private Cita leerRegistroEn(long posicion) throws IOException {
        archivo.seek(posicion);
        Cita c = new Cita();
        c.leer(archivo);
        return c;
    }

    /**
     * Convierte HH:MM a minutos desde medianoche para comparación.
     */
    private int horaAMinutos(String hora) {
        try {
            LocalTime t = LocalTime.parse(hora, HORA_FORMAT);
            return t.getHour() * 60 + t.getMinute();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Verifica si dos citas se solapan (mismo médico, misma fecha, horarios superpuestos).
     * Asumimos duración fija de 30 min por cita para detección de solapamiento.
     * La horaFin se calcula como horaInicio + 30 min.
     */
    private boolean seSolapan(String horaInicio1, String horaInicio2) {
        int inicio1 = horaAMinutos(horaInicio1);
        int inicio2 = horaAMinutos(horaInicio2);
        if (inicio1 == -1 || inicio2 == -1) return false;
        
        int fin1 = inicio1 + 30; // Duración fija 30 min
        int fin2 = inicio2 + 30;
        
        // Solapan si: inicio1 < fin2 AND inicio2 < fin1
        return inicio1 < fin2 && inicio2 < fin1;
    }

    // ==================== CRUD BÁSICO ====================

    /**
     * Agrega una nueva cita validando que no se solape con otras del mismo médico en la misma fecha.
     */
    public boolean crear(Cita cita) throws IOException {
        validarArchivoAbierto();
        
        // Verificar unicidad de UUID
        if (buscarPorId(cita.getIdCita()) != null) {
            throw new IllegalArgumentException("Ya existe una cita con ID: " + cita.getIdCita());
        }
        
        // Validar solapamiento con citas existentes del mismo médico en la misma fecha
        if (existeSolapamiento(cita.getIdMedico(), cita.getFecha(), cita.getHoraInicio(), null)) {
            throw new IllegalArgumentException("El médico ya tiene una cita programada a esa hora en esa fecha");
        }
        
        archivo.seek(archivo.length());
        cita.escribir(archivo);
        return true;
    }

    /**
     * Busca una cita por UUID.
     */
    public Cita buscarPorId(String idCita) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.isActiva() && c.getIdCita().equals(idCita)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Busca citas por CUI de paciente.
     */
    public List<Cita> buscarPorPaciente(String cuiPaciente) throws IOException {
        validarArchivoAbierto();
        List<Cita> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.isActiva() && c.getCuiPaciente().equals(cuiPaciente)) {
                resultados.add(c);
            }
        }
        return resultados;
    }

    /**
     * Busca citas por ID de médico.
     */
    public List<Cita> buscarPorMedico(String idMedico) throws IOException {
        validarArchivoAbierto();
        List<Cita> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.isActiva() && c.getIdMedico().equals(idMedico)) {
                resultados.add(c);
            }
        }
        return resultados;
    }

    /**
     * Busca citas por fecha exacta (DD/MM/YYYY).
     */
    public List<Cita> buscarPorFecha(String fecha) throws IOException {
        validarArchivoAbierto();
        List<Cita> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.isActiva() && c.getFecha().equals(fecha)) {
                resultados.add(c);
            }
        }
        return resultados;
    }

    /**
     * Busca citas por estado.
     */
    public List<Cita> buscarPorEstado(String estado) throws IOException {
        validarArchivoAbierto();
        List<Cita> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.isActiva() && c.getEstado().equals(estado)) {
                resultados.add(c);
            }
        }
        return resultados;
    }

    /**
     * Busca citas por rango de fechas (inclusive).
     * Formato: DD/MM/YYYY
     */
    public List<Cita> buscarPorRangoFechas(String fechaInicio, String fechaFin) throws IOException {
        validarArchivoAbierto();
        List<Cita> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.isActiva()) {
                int cmpInicio = compararFechas(c.getFecha(), fechaInicio);
                int cmpFin = compararFechas(c.getFecha(), fechaFin);
                if (cmpInicio >= 0 && cmpFin <= 0) {
                    resultados.add(c);
                }
            }
        }
        return resultados;
    }

    /**
     * Compara dos fechas en formato DD/MM/YYYY.
     * Retorna: -1 si fecha1 < fecha2, 0 si igual, 1 si fecha1 > fecha2
     */
    private int compararFechas(String fecha1, String fecha2) {
        try {
            String[] f1 = fecha1.split("/");
            String[] f2 = fecha2.split("/");
            int y1 = Integer.parseInt(f1[2]), m1 = Integer.parseInt(f1[1]), d1 = Integer.parseInt(f1[0]);
            int y2 = Integer.parseInt(f2[2]), m2 = Integer.parseInt(f2[1]), d2 = Integer.parseInt(f2[0]);
            
            if (y1 != y2) return Integer.compare(y1, y2);
            if (m1 != m2) return Integer.compare(m1, m2);
            return Integer.compare(d1, d2);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Obtiene todas las citas activas.
     */
    public List<Cita> listarActivas() throws IOException {
        validarArchivoAbierto();
        List<Cita> lista = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.isActiva()) {
                lista.add(c);
            }
        }
        return lista;
    }

    /**
     * Obtiene todas las citas (incluyendo eliminadas).
     */
    public List<Cita> listarTodas() throws IOException {
        validarArchivoAbierto();
        List<Cita> lista = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            lista.add(leerRegistroEn(calcularPosicion(i)));
        }
        return lista;
    }

    /**
     * Actualiza una cita existente.
     * Valida solapamiento si se cambia fecha/hora/médico.
     */
    public boolean actualizar(Cita cita) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Cita existente = new Cita();
            existente.leer(archivo);
            
            if (existente.getIdCita().equals(cita.getIdCita())) {
                // Validar solapamiento excluyendo la cita actual
                if (existeSolapamiento(cita.getIdMedico(), cita.getFecha(), cita.getHoraInicio(), cita.getIdCita())) {
                    throw new IllegalArgumentException("El médico ya tiene una cita programada a esa hora en esa fecha");
                }
                
                archivo.seek(pos);
                cita.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si existe solapamiento para un médico en una fecha/hora dada.
     * @param excluirIdCita ID de cita a excluir de la validación (para actualizaciones), null para nuevas
     */
    public boolean existeSolapamiento(String idMedico, String fecha, String horaInicio, String excluirIdCita) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.isActiva() 
                    && c.getIdMedico().equals(idMedico) 
                    && c.getFecha().equals(fecha)
                    && (excluirIdCita == null || !c.getIdCita().equals(excluirIdCita))
                    && seSolapan(c.getHoraInicio(), horaInicio)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cancela una cita (cambia estado a "Cancelada").
     */
    public boolean cancelar(String idCita) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Cita c = new Cita();
            c.leer(archivo);
            
            if (c.getIdCita().equals(idCita) && c.isActiva()) {
                c.setEstado(Cita.EST_CANCELADA);
                archivo.seek(pos);
                c.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Marca una cita como atendida.
     */
    public boolean marcarAtendida(String idCita) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Cita c = new Cita();
            c.leer(archivo);
            
            if (c.getIdCita().equals(idCita) && c.isActiva()) {
                c.setEstado(Cita.EST_ATENDIDA);
                archivo.seek(pos);
                c.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina lógicamente una cita (marca activa = false).
     */
    public boolean eliminarLogico(String idCita) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Cita c = new Cita();
            c.leer(archivo);
            
            if (c.getIdCita().equals(idCita) && c.isActiva()) {
                c.setActiva(false);
                archivo.seek(pos);
                c.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si existe una cita con el ID dado.
     */
    public boolean existeId(String idCita) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Cita c = leerRegistroEn(calcularPosicion(i));
            if (c.getIdCita().equals(idCita)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cuenta citas activas.
     */
    public int contarActivas() throws IOException {
        return listarActivas().size();
    }

    /**
     * Cuenta citas por estado.
     */
    public int contarPorEstado(String estado) throws IOException {
        return buscarPorEstado(estado).size();
    }

    // ==================== MANTENIMIENTO ====================

    private void reescribirArchivoCompleto(List<Cita> citas) throws IOException {
        archivo.seek(0);
        archivo.setLength(0);
        for (Cita c : citas) {
            c.escribir(archivo);
        }
    }

    public void cerrar() throws IOException {
        if (archivo != null) {
            archivo.close();
        }
    }
}