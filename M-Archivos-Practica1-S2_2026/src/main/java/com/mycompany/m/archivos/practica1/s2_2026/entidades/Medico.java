package com.mycompany.m.archivos.practica1.s2_2026.entidades;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Entidad Médico para el sistema de clínica médica.
 * Almacenamiento en archivo binario de longitud fija usando RandomAccessFile.
 */
public class Medico {

    // Longitudes fijas para cada campo (en caracteres)
    public static final int TAM_UUID = 36;
    public static final int TAM_NOMBRES = 50;
    public static final int TAM_ESPECIALIDAD = 50;
    public static final int TAM_TELEFONO = 15;
    public static final int TAM_CORREO = 50;
    public static final int TAM_HORARIO = 5; // Formato "HH:MM"

    // Tamaño total del registro en bytes (chars = 2 bytes cada uno + 2 bytes para boolean como char)
    public static final int TAM_REGISTRO = 
        (TAM_UUID + TAM_NOMBRES + TAM_ESPECIALIDAD + TAM_TELEFONO + TAM_CORREO + TAM_HORARIO + TAM_HORARIO) * 2 + 2;

    private String idMedico;        // UUID generado automáticamente
    private String nombresApellidos; // Obligatorio
    private String especialidad;     // Obligatorio
    private String telefono;
    private String correo;           // Opcional
    private String horarioInicio;    // HH:MM
    private String horarioFin;       // HH:MM
    private boolean activo;          // true = activo, false = inactivo

    public Medico() {
        this.idMedico = UUID.randomUUID().toString();
        this.activo = true;
        this.nombresApellidos = "";
        this.especialidad = "";
        this.telefono = "";
        this.correo = "";
        this.horarioInicio = "";
        this.horarioFin = "";
    }

    public Medico(String nombresApellidos, String especialidad, String telefono, 
                  String correo, String horarioInicio, String horarioFin) {
        this.idMedico = UUID.randomUUID().toString();
        setNombresApellidos(nombresApellidos);
        setEspecialidad(especialidad);
        setTelefono(telefono);
        setCorreo(correo);
        setHorarioInicio(horarioInicio);
        setHorarioFin(horarioFin);
        this.activo = true;
    }

    // Constructor para lectura desde archivo (sin generar nuevo UUID)
    public Medico(String idMedico, String nombresApellidos, String especialidad, 
                  String telefono, String correo, String horarioInicio, String horarioFin, boolean activo) {
        this.idMedico = idMedico;
        this.nombresApellidos = nombresApellidos;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.correo = correo;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.activo = activo;
    }

    // ==================== GETTERS ====================
    public String getIdMedico() { return idMedico; }
    public String getNombresApellidos() { return nombresApellidos; }
    public String getEspecialidad() { return especialidad; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getHorarioInicio() { return horarioInicio; }
    public String getHorarioFin() { return horarioFin; }
    public boolean isActivo() { return activo; }

    // ==================== SETTERS CON VALIDACIÓN ====================
    public void setIdMedico(String idMedico) { 
        if (idMedico != null && idMedico.length() <= TAM_UUID) {
            this.idMedico = idMedico;
        }
    }

    public void setNombresApellidos(String nombresApellidos) {
        if (nombresApellidos == null) nombresApellidos = "";
        if (nombresApellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombres y apellidos son obligatorios");
        }
        if (nombresApellidos.length() > TAM_NOMBRES) {
            throw new IllegalArgumentException("Nombres y apellidos no puede exceder " + TAM_NOMBRES + " caracteres");
        }
        this.nombresApellidos = nombresApellidos;
    }

    public void setEspecialidad(String especialidad) {
        if (especialidad == null) especialidad = "";
        if (especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("Especialidad es obligatoria");
        }
        if (especialidad.length() > TAM_ESPECIALIDAD) {
            throw new IllegalArgumentException("Especialidad no puede exceder " + TAM_ESPECIALIDAD + " caracteres");
        }
        this.especialidad = especialidad;
    }

    public void setTelefono(String telefono) {
        if (telefono == null) telefono = "";
        if (telefono.length() > TAM_TELEFONO) {
            throw new IllegalArgumentException("Teléfono no puede exceder " + TAM_TELEFONO + " caracteres");
        }
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        if (correo == null) correo = "";
        if (correo.length() > TAM_CORREO) {
            throw new IllegalArgumentException("Correo no puede exceder " + TAM_CORREO + " caracteres");
        }
        this.correo = correo;
    }

    public void setHorarioInicio(String horarioInicio) {
        if (horarioInicio == null) horarioInicio = "";
        if (!horarioInicio.isEmpty()) {
            if (!horarioInicio.matches("\\d{2}:\\d{2}")) {
                throw new IllegalArgumentException("Formato de hora inválido. Use HH:MM");
            }
            if (horarioInicio.length() > TAM_HORARIO) {
                throw new IllegalArgumentException("Horario inicio no puede exceder " + TAM_HORARIO + " caracteres");
            }
        }
        this.horarioInicio = horarioInicio;
    }

    public void setHorarioFin(String horarioFin) {
        if (horarioFin == null) horarioFin = "";
        if (!horarioFin.isEmpty()) {
            if (!horarioFin.matches("\\d{2}:\\d{2}")) {
                throw new IllegalArgumentException("Formato de hora inválido. Use HH:MM");
            }
            if (horarioFin.length() > TAM_HORARIO) {
                throw new IllegalArgumentException("Horario fin no puede exceder " + TAM_HORARIO + " caracteres");
            }
        }
        this.horarioFin = horarioFin;
    }

    public void setActivo(boolean activo) { this.activo = activo; }

    // ==================== SERIALIZACIÓN RandomAccessFile ====================
    
    public void escribir(RandomAccessFile file) throws IOException {
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, idMedico, TAM_UUID);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, nombresApellidos, TAM_NOMBRES);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, especialidad, TAM_ESPECIALIDAD);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, telefono, TAM_TELEFONO);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, correo, TAM_CORREO);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, horarioInicio, TAM_HORARIO);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, horarioFin, TAM_HORARIO);
        file.writeChar(activo ? '1' : '0');
    }

    public Medico leer(RandomAccessFile file) throws IOException {
        this.idMedico = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_UUID);
        this.nombresApellidos = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_NOMBRES);
        this.especialidad = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_ESPECIALIDAD);
        this.telefono = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_TELEFONO);
        this.correo = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_CORREO);
        this.horarioInicio = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_HORARIO);
        this.horarioFin = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_HORARIO);
        this.activo = (file.readChar() == '1');
        return this;
    }

    public static int getTamanioRegistro() { return TAM_REGISTRO; }

    @Override
    public String toString() {
        return "Medico{" +
                "idMedico='" + idMedico + '\'' +
                ", nombresApellidos='" + nombresApellidos + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", horarioInicio='" + horarioInicio + '\'' +
                ", horarioFin='" + horarioFin + '\'' +
                ", activo=" + activo + '}';
    }
}