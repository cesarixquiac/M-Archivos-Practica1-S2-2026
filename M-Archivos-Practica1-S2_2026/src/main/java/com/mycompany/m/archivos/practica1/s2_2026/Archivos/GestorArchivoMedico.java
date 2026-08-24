package com.mycompany.m.archivos.practica1.s2_2026.Archivos;

import com.mycompany.m.archivos.practica1.s2_2026.entidades.Medico;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de persistencia para la entidad Médico usando RandomAccessFile.
 * Archivo: medicos.dat - Registros de longitud fija.
 * Operaciones: CRUD + búsquedas + filtros por estado/especialidad.
 */
public class GestorArchivoMedico {

    private static final String NOMBRE_ARCHIVO = "medicos.dat";
    private final RandomAccessFile archivo;
    private final long tamanioRegistro;

    public GestorArchivoMedico() throws IOException {
        File f = new File(NOMBRE_ARCHIVO);
        this.archivo = new RandomAccessFile(f, "rw");
        this.tamanioRegistro = Medico.getTamanioRegistro();
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

    private Medico leerRegistroEn(long posicion) throws IOException {
        archivo.seek(posicion);
        Medico m = new Medico();
        m.leer(archivo);
        return m;
    }

    // ==================== CRUD BÁSICO ====================

    /**
     * Agrega un nuevo médico al final del archivo.
     */
    public boolean crear(Medico medico) throws IOException {
        validarArchivoAbierto();
        
        // Verificar unicidad de UUID
        if (buscarPorId(medico.getIdMedico()) != null) {
            throw new IllegalArgumentException("Ya existe un médico con ID: " + medico.getIdMedico());
        }
        
        archivo.seek(archivo.length());
        medico.escribir(archivo);
        return true;
    }

    /**
     * Busca un médico por UUID.
     * @return Médico si existe y está activo, null si no existe o está inactivo
     */
    public Medico buscarPorId(String idMedico) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Medico m = leerRegistroEn(calcularPosicion(i));
            if (m.isActivo() && m.getIdMedico().equals(idMedico)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Busca médicos por nombre/apellido (parcial, case-insensitive).
     */
    public List<Medico> buscarPorNombre(String nombre) throws IOException {
        validarArchivoAbierto();
        List<Medico> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        String busqueda = nombre.toLowerCase().trim();
        
        for (int i = 0; i < total; i++) {
            Medico m = leerRegistroEn(calcularPosicion(i));
            if (m.isActivo() && m.getNombresApellidos().toLowerCase().contains(busqueda)) {
                resultados.add(m);
            }
        }
        return resultados;
    }

    /**
     * Busca médicos por especialidad (exacta, case-insensitive).
     */
    public List<Medico> buscarPorEspecialidad(String especialidad) throws IOException {
        validarArchivoAbierto();
        List<Medico> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        String busqueda = especialidad.toLowerCase().trim();
        
        for (int i = 0; i < total; i++) {
            Medico m = leerRegistroEn(calcularPosicion(i));
            if (m.isActivo() && m.getEspecialidad().toLowerCase().equals(busqueda)) {
                resultados.add(m);
            }
        }
        return resultados;
    }

    /**
     * Obtiene todos los médicos activos.
     */
    public List<Medico> listarActivos() throws IOException {
        validarArchivoAbierto();
        List<Medico> lista = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Medico m = leerRegistroEn(calcularPosicion(i));
            if (m.isActivo()) {
                lista.add(m);
            }
        }
        return lista;
    }

    /**
     * Obtiene todos los médicos inactivos.
     */
    public List<Medico> listarInactivos() throws IOException {
        validarArchivoAbierto();
        List<Medico> lista = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Medico m = leerRegistroEn(calcularPosicion(i));
            if (!m.isActivo()) {
                lista.add(m);
            }
        }
        return lista;
    }

    /**
     * Obtiene todos los médicos (incluyendo inactivos).
     */
    public List<Medico> listarTodos() throws IOException {
        validarArchivoAbierto();
        List<Medico> lista = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            lista.add(leerRegistroEn(calcularPosicion(i)));
        }
        return lista;
    }

    /**
     * Actualiza un médico existente.
     */
    public boolean actualizar(Medico medico) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Medico existente = new Medico();
            existente.leer(archivo);
            
            if (existente.getIdMedico().equals(medico.getIdMedico())) {
                archivo.seek(pos);
                medico.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Activa/desactiva un médico (eliminación lógica / reactivación).
     * @param idMedico UUID del médico
     * @param activo true para activar, false para desactivar
     * @return true si se cambió el estado, false si no se encontró
     */
    public boolean cambiarEstado(String idMedico, boolean activo) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Medico m = new Medico();
            m.leer(archivo);
            
            if (m.getIdMedico().equals(idMedico)) {
                m.setActivo(activo);
                archivo.seek(pos);
                m.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina lógicamente (desactiva) un médico.
     */
    public boolean desactivar(String idMedico) throws IOException {
        return cambiarEstado(idMedico, false);
    }

    /**
     * Reactiva un médico desactivado.
     */
    public boolean activar(String idMedico) throws IOException {
        return cambiarEstado(idMedico, true);
    }

    /**
     * Verifica si existe un médico con el ID dado.
     */
    public boolean existeId(String idMedico) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Medico m = leerRegistroEn(calcularPosicion(i));
            if (m.getIdMedico().equals(idMedico)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cuenta médicos activos.
     */
    public int contarActivos() throws IOException {
        return listarActivos().size();
    }

    // ==================== MANTENIMIENTO ====================

    /**
     * Reescribe todo el archivo (compactación).
     */
    private void reescribirArchivoCompleto(List<Medico> medicos) throws IOException {
        archivo.seek(0);
        archivo.setLength(0);
        for (Medico m : medicos) {
            m.escribir(archivo);
        }
    }

    /**
     * Cierra el archivo.
     */
    public void cerrar() throws IOException {
        if (archivo != null) {
            archivo.close();
        }
    }
}