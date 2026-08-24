package com.mycompany.m.archivos.practica1.s2_2026.Archivos;

import com.mycompany.m.archivos.practica1.s2_2026.entidades.Paciente;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de persistencia para la entidad Paciente usando RandomAccessFile.
 * Archivo: pacientes.dat - Registros de longitud fija.
 * Operaciones: CRUD + búsquedas + listados.
 */
public class GestorArchivoPaciente {

    private static final String NOMBRE_ARCHIVO = "pacientes.dat";
    private final RandomAccessFile archivo;
    private final long tamanioRegistro;

    public GestorArchivoPaciente() throws IOException {
        File f = new File(NOMBRE_ARCHIVO);
        boolean nuevo = !f.exists();
        this.archivo = new RandomAccessFile(f, "rw");
        this.tamanioRegistro = Paciente.getTamanioRegistro();
        
        if (nuevo) {
            // Archivo nuevo, opcional: escribir cabecera o dejar vacío
        }
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

    private Paciente leerRegistroEn(long posicion) throws IOException {
        archivo.seek(posicion);
        Paciente p = new Paciente();
        p.leer(archivo);
        return p;
    }

    // ==================== CRUD BÁSICO ====================

    /**
     * Agrega un nuevo paciente al final del archivo.
     * @return true si se insertó correctamente
     */
    public boolean crear(Paciente paciente) throws IOException {
        validarArchivoAbierto();
        
        // Verificar unicidad de CUI
        if (buscarPorCui(paciente.getCui()) != null) {
            throw new IllegalArgumentException("Ya existe un paciente con CUI: " + paciente.getCui());
        }
        
        archivo.seek(archivo.length()); // Ir al final
        paciente.escribir(archivo);
        return true;
    }

    /**
     * Busca un paciente por CUI (clave única).
     * @return Paciente si existe y está activo, null si no existe o está eliminado
     */
    public Paciente buscarPorCui(String cui) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Paciente p = leerRegistroEn(calcularPosicion(i));
            if (p.isActivo() && p.getCui().equals(cui)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca pacientes por nombre/apellido (búsqueda parcial, case-insensitive).
     */
    public List<Paciente> buscarPorNombre(String nombre) throws IOException {
        validarArchivoAbierto();
        List<Paciente> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        String busqueda = nombre.toLowerCase().trim();
        
        for (int i = 0; i < total; i++) {
            Paciente p = leerRegistroEn(calcularPosicion(i));
            if (p.isActivo() && p.getNombresApellidos().toLowerCase().contains(busqueda)) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    /**
     * Busca pacientes por tipo de sangre.
     */
    public List<Paciente> buscarPorTipoSangre(String tipoSangre) throws IOException {
        validarArchivoAbierto();
        List<Paciente> resultados = new ArrayList<>();
        int total = obtenerTotalRegistros();
        String busqueda = tipoSangre.toLowerCase().trim();
        
        for (int i = 0; i < total; i++) {
            Paciente p = leerRegistroEn(calcularPosicion(i));
            if (p.isActivo() && p.getTipoSangre().toLowerCase().equals(busqueda)) {
                resultados.add(p);
            }
        }
        return resultados;
    }

    /**
     * Obtiene todos los pacientes activos.
     */
    public List<Paciente> listarTodos() throws IOException {
        validarArchivoAbierto();
        List<Paciente> lista = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Paciente p = leerRegistroEn(calcularPosicion(i));
            if (p.isActivo()) {
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Obtiene todos los pacientes (incluyendo eliminados lógicamente).
     */
    public List<Paciente> listarTodosIncluyendoEliminados() throws IOException {
        validarArchivoAbierto();
        List<Paciente> lista = new ArrayList<>();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            lista.add(leerRegistroEn(calcularPosicion(i)));
        }
        return lista;
    }

    /**
     * Actualiza un paciente existente (sobrescribe en su posición).
     * @return true si se actualizó, false si no se encontró
     */
    public boolean actualizar(Paciente paciente) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Paciente existente = new Paciente();
            existente.leer(archivo);
            
            if (existente.getCui().equals(paciente.getCui())) {
                // Verificar que no se esté cambiando a un CUI que ya existe en OTRO registro
                if (!existente.getCui().equals(paciente.getCui()) && buscarPorCui(paciente.getCui()) != null) {
                    throw new IllegalArgumentException("Ya existe otro paciente con CUI: " + paciente.getCui());
                }
                
                archivo.seek(pos);
                paciente.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina lógicamente un paciente (marca activo = false).
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminarLogico(String cui) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Paciente p = new Paciente();
            p.leer(archivo);
            
            if (p.getCui().equals(cui) && p.isActivo()) {
                p.setActivo(false);
                archivo.seek(pos);
                p.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina físicamente un paciente (compacta el archivo).
     * @return true si se eliminó, false si no se encontró
     * ADVERTENCIA: Cambia las posiciones de registros posteriores.
     */
    public boolean eliminarFisico(String cui) throws IOException {
        validarArchivoAbierto();
        List<Paciente> todos = listarTodosIncluyendoEliminados();
        boolean encontrado = false;
        List<Paciente> nuevos = new ArrayList<>();
        
        for (Paciente p : todos) {
            if (p.getCui().equals(cui) && p.isActivo()) {
                encontrado = true;
                // No agregar a la lista nueva (se elimina)
            } else {
                nuevos.add(p);
            }
        }
        
        if (encontrado) {
            reescribirArchivoCompleto(nuevos);
        }
        return encontrado;
    }

    /**
     * Reactiva un paciente eliminado lógicamente.
     */
    public boolean reactivar(String cui) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            long pos = calcularPosicion(i);
            archivo.seek(pos);
            Paciente p = new Paciente();
            p.leer(archivo);
            
            if (p.getCui().equals(cui) && !p.isActivo()) {
                p.setActivo(true);
                archivo.seek(pos);
                p.escribir(archivo);
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si existe un paciente con el CUI dado (activo o no).
     */
    public boolean existeCui(String cui) throws IOException {
        validarArchivoAbierto();
        int total = obtenerTotalRegistros();
        
        for (int i = 0; i < total; i++) {
            Paciente p = leerRegistroEn(calcularPosicion(i));
            if (p.getCui().equals(cui)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cuenta pacientes activos.
     */
    public int contarActivos() throws IOException {
        return listarTodos().size();
    }

    // ==================== MANTENIMIENTO ====================

    /**
     * Reescribe todo el archivo con la lista dada (para compactación).
     */
    private void reescribirArchivoCompleto(List<Paciente> pacientes) throws IOException {
        archivo.seek(0);
        archivo.setLength(0); // Truncar
        for (Paciente p : pacientes) {
            p.escribir(archivo);
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