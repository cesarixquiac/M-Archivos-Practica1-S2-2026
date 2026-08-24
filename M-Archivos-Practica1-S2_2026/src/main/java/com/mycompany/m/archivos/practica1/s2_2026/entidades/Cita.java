package com.mycompany.m.archivos.practica1.s2_2026.entidades;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Entidad Cita para el sistema de clínica médica.
 * Almacenamiento en archivo binario de longitud fija usando RandomAccessFile.
 */
public class Cita {

    // Longitudes fijas para cada campo (en caracteres)
    public static final int TAM_UUID = 36;
    public static final int TAM_CUI_PACIENTE = 13;    // CUI/DPI de 13 dígitos
    public static final int TAM_FECHA = 10;           // DD/MM/YYYY
    public static final int TAM_HORA = 5;             // HH:MM
    public static final int TAM_MOTIVO = 100;
    public static final int TAM_ESTADO = 15;          // "Programada", "Atendida", "Cancelada"
    public static final int TAM_OBSERVACIONES = 150;

    // Tamaño total del registro en bytes
    public static final int TAM_REGISTRO = 
        (TAM_UUID + TAM_CUI_PACIENTE + TAM_UUID + TAM_FECHA + TAM_HORA + TAM_MOTIVO + TAM_ESTADO + TAM_OBSERVACIONES) * 2 + 2; // +2 para char de activo/eliminado

    // Estados válidos
    public static final String EST_PROGRAMADA = "Programada";
    public static final String EST_ATENDIDA = "Atendida";
    public static final String EST_CANCELADA = "Cancelada";

    private String idCita;           // UUID generado automáticamente
    private String cuiPaciente;      // CUI/DPI del paciente (13 dígitos)
    private String idMedico;         // UUID del médico
    private String fecha;            // DD/MM/YYYY
    private String horaInicio;       // HH:MM
    private String motivo;           // Obligatorio
    private String estado;           // Programada, Atendida, Cancelada
    private String observaciones;    // Opcional
    private boolean activa;          // true = activa, false = eliminada lógicamente

    public Cita() {
        this.idCita = UUID.randomUUID().toString();
        this.estado = EST_PROGRAMADA;
        this.activa = true;
        this.cuiPaciente = "";
        this.idMedico = "";
        this.fecha = "";
        this.horaInicio = "";
        this.motivo = "";
        this.observaciones = "";
    }

    public Cita(String cuiPaciente, String idMedico, String fecha, String horaInicio, 
                String motivo, String observaciones) {
        this.idCita = UUID.randomUUID().toString();
        setCuiPaciente(cuiPaciente);
        setIdMedico(idMedico);
        setFecha(fecha);
        setHoraInicio(horaInicio);
        setMotivo(motivo);
        setObservaciones(observaciones);
        this.estado = EST_PROGRAMADA;
        this.activa = true;
    }

    // Constructor para lectura desde archivo
    public Cita(String idCita, String cuiPaciente, String idMedico, String fecha, 
                String horaInicio, String motivo, String estado, String observaciones, boolean activa) {
        this.idCita = idCita;
        this.cuiPaciente = cuiPaciente;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.motivo = motivo;
        this.estado = estado;
        this.observaciones = observaciones;
        this.activa = activa;
    }

    // ==================== GETTERS ====================
    public String getIdCita() { return idCita; }
    public String getCuiPaciente() { return cuiPaciente; }
    public String getIdMedico() { return idMedico; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getMotivo() { return motivo; }
    public String getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public boolean isActiva() { return activa; }

    // ==================== SETTERS CON VALIDACIÓN ====================
    public void setIdCita(String idCita) { 
        if (idCita != null && idCita.length() <= TAM_UUID) {
            this.idCita = idCita;
        }
    }

    public void setCuiPaciente(String cuiPaciente) {
        if (cuiPaciente == null) cuiPaciente = "";
        if (cuiPaciente.trim().isEmpty()) {
            throw new IllegalArgumentException("CUI del paciente es obligatorio");
        }
        if (!cuiPaciente.matches("\\d{" + TAM_CUI_PACIENTE + "}")) {
            throw new IllegalArgumentException("CUI debe tener exactamente " + TAM_CUI_PACIENTE + " dígitos numéricos");
        }
        this.cuiPaciente = cuiPaciente;
    }

    public void setIdMedico(String idMedico) {
        if (idMedico == null) idMedico = "";
        if (idMedico.trim().isEmpty()) {
            throw new IllegalArgumentException("ID del médico es obligatorio");
        }
        if (idMedico.length() > TAM_UUID) {
            throw new IllegalArgumentException("ID médico no puede exceder " + TAM_UUID + " caracteres");
        }
        this.idMedico = idMedico;
    }

    public void setFecha(String fecha) {
        if (fecha == null) fecha = "";
        if (fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("Fecha es obligatoria");
        }
        if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use DD/MM/YYYY");
        }
        if (fecha.length() > TAM_FECHA) {
            throw new IllegalArgumentException("Fecha no puede exceder " + TAM_FECHA + " caracteres");
        }
        this.fecha = fecha;
    }

    public void setHoraInicio(String horaInicio) {
        if (horaInicio == null) horaInicio = "";
        if (horaInicio.trim().isEmpty()) {
            throw new IllegalArgumentException("Hora de inicio es obligatoria");
        }
        if (!horaInicio.matches("\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Formato de hora inválido. Use HH:MM");
        }
        if (horaInicio.length() > TAM_HORA) {
            throw new IllegalArgumentException("Hora no puede exceder " + TAM_HORA + " caracteres");
        }
        this.horaInicio = horaInicio;
    }

    public void setMotivo(String motivo) {
        if (motivo == null) motivo = "";
        if (motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo es obligatorio");
        }
        if (motivo.length() > TAM_MOTIVO) {
            throw new IllegalArgumentException("Motivo no puede exceder " + TAM_MOTIVO + " caracteres");
        }
        this.motivo = motivo;
    }

    public void setEstado(String estado) {
        if (estado == null) estado = "";
        String e = estado.trim();
        if (!e.equals(EST_PROGRAMADA) && !e.equals(EST_ATENDIDA) && !e.equals(EST_CANCELADA)) {
            throw new IllegalArgumentException("Estado inválido. Debe ser: Programada, Atendida o Cancelada");
        }
        if (e.length() > TAM_ESTADO) {
            throw new IllegalArgumentException("Estado no puede exceder " + TAM_ESTADO + " caracteres");
        }
        this.estado = e;
    }

    public void setObservaciones(String observaciones) {
        if (observaciones == null) observaciones = "";
        if (observaciones.length() > TAM_OBSERVACIONES) {
            throw new IllegalArgumentException("Observaciones no puede exceder " + TAM_OBSERVACIONES + " caracteres");
        }
        this.observaciones = observaciones;
    }

    public void setActiva(boolean activa) { this.activa = activa; }

    // Métodos de conveniencia para cambio de estado
    public void marcarAtendida() { this.estado = EST_ATENDIDA; }
    public void cancelar() { this.estado = EST_CANCELADA; }
    public void reprogramar() { this.estado = EST_PROGRAMADA; }

    // ==================== SERIALIZACIÓN RandomAccessFile ====================
    
    public void escribir(RandomAccessFile file) throws IOException {
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, idCita, TAM_UUID);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, cuiPaciente, TAM_CUI_PACIENTE);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, idMedico, TAM_UUID);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, fecha, TAM_FECHA);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, horaInicio, TAM_HORA);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, motivo, TAM_MOTIVO);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, estado, TAM_ESTADO);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, observaciones, TAM_OBSERVACIONES);
        file.writeChar(activa ? '1' : '0');
    }

    public Cita leer(RandomAccessFile file) throws IOException {
        this.idCita = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_UUID);
        this.cuiPaciente = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_CUI_PACIENTE);
        this.idMedico = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_UUID);
        this.fecha = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_FECHA);
        this.horaInicio = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_HORA);
        this.motivo = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_MOTIVO);
        this.estado = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_ESTADO);
        this.observaciones = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_OBSERVACIONES);
        this.activa = (file.readChar() == '1');
        return this;
    }

    public static int getTamanioRegistro() { return TAM_REGISTRO; }

    @Override
    public String toString() {
        return "Cita{" +
                "idCita='" + idCita + '\'' +
                ", cuiPaciente='" + cuiPaciente + '\'' +
                ", idMedico='" + idMedico + '\'' +
                ", fecha='" + fecha + '\'' +
                ", horaInicio='" + horaInicio + '\'' +
                ", motivo='" + motivo + '\'' +
                ", estado='" + estado + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", activa=" + activa + '}';
    }
}