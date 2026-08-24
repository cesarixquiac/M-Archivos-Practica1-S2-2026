package com.mycompany.m.archivos.practica1.s2_2026.entidades;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Entidad Paciente para el sistema de clínica médica.
 * Almacenamiento en archivo binario de longitud fija usando RandomAccessFile.
 */
public class Paciente {

    // Longitudes fijas para cada campo (en caracteres)
    public static final int TAM_CUI = 13;           // 13 dígitos exactos, sin guiones
    public static final int TAM_NOMBRES = 50;       // Nombres y apellidos
    public static final int TAM_FECHA_NAC = 10;     // DD/MM/YYYY
    public static final int TAM_SEXO = 10;          // "Masculino" o "Femenino"
    public static final int TAM_TELEFONO = 15;
    public static final int TAM_CORREO = 50;
    public static final int TAM_TIPO_SANGRE = 10;   // Campo libre (ej: "A+", "O-", etc.)

    // Tamaño total del registro en bytes (chars = 2 bytes cada uno en writeChars/readChar)
    // 1 boolean (activo) = 1 byte, pero lo escribimos como char para consistencia
    public static final int TAM_REGISTRO = 
        (TAM_CUI + TAM_NOMBRES + TAM_FECHA_NAC + TAM_SEXO + TAM_TELEFONO + TAM_CORREO + TAM_TIPO_SANGRE) * 2 + 2; // +2 para char 'activo'

    private String cui;                 // 13 dígitos, único, obligatorio
    private String nombresApellidos;    // Obligatorio
    private String fechaNacimiento;     // DD/MM/YYYY
    private String sexo;                // "Masculino" o "Femenino"
    private String telefono;
    private String correo;              // Opcional
    private String tipoSangre;          // Campo libre
    private boolean activo;             // true = activo, false = eliminado lógicamente

    public Paciente() {
        this.cui = "";
        this.nombresApellidos = "";
        this.fechaNacimiento = "";
        this.sexo = "";
        this.telefono = "";
        this.correo = "";
        this.tipoSangre = "";
        this.activo = true;
    }

    public Paciente(String cui, String nombresApellidos, String fechaNacimiento, 
                    String sexo, String telefono, String correo, String tipoSangre) {
        setCui(cui);
        setNombresApellidos(nombresApellidos);
        setFechaNacimiento(fechaNacimiento);
        setSexo(sexo);
        setTelefono(telefono);
        setCorreo(correo);
        setTipoSangre(tipoSangre);
        this.activo = true;
    }

    // ==================== GETTERS ====================
    public String getCui() { return cui; }
    public String getNombresApellidos() { return nombresApellidos; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getSexo() { return sexo; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getTipoSangre() { return tipoSangre; }
    public boolean isActivo() { return activo; }

    // ==================== SETTERS CON VALIDACIÓN DE LONGITUD ====================
    public void setCui(String cui) {
        if (cui == null) cui = "";
        if (cui.length() > TAM_CUI) {
            throw new IllegalArgumentException("CUI/DPI no puede exceder " + TAM_CUI + " dígitos");
        }
        if (!cui.isEmpty() && !cui.matches("\\d{" + TAM_CUI + "}")) {
            throw new IllegalArgumentException("CUI/DPI debe contener exactamente " + TAM_CUI + " dígitos numéricos");
        }
        this.cui = cui;
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

    public void setFechaNacimiento(String fechaNacimiento) {
        if (fechaNacimiento == null) fechaNacimiento = "";
        if (fechaNacimiento.length() > TAM_FECHA_NAC) {
            throw new IllegalArgumentException("Fecha de nacimiento no puede exceder " + TAM_FECHA_NAC + " caracteres (DD/MM/YYYY)");
        }
        if (!fechaNacimiento.isEmpty() && !fechaNacimiento.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use DD/MM/YYYY");
        }
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setSexo(String sexo) {
        if (sexo == null) sexo = "";
        String s = sexo.trim();
        if (!s.equalsIgnoreCase("Masculino") && !s.equalsIgnoreCase("Femenino")) {
            throw new IllegalArgumentException("Sexo debe ser 'Masculino' o 'Femenino'");
        }
        if (s.length() > TAM_SEXO) {
            throw new IllegalArgumentException("Sexo no puede exceder " + TAM_SEXO + " caracteres");
        }
        this.sexo = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase(); // Normalizar
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

    public void setTipoSangre(String tipoSangre) {
        if (tipoSangre == null) tipoSangre = "";
        if (tipoSangre.length() > TAM_TIPO_SANGRE) {
            throw new IllegalArgumentException("Tipo de sangre no puede exceder " + TAM_TIPO_SANGRE + " caracteres");
        }
        this.tipoSangre = tipoSangre;
    }

    public void setActivo(boolean activo) { this.activo = activo; }

    // ==================== SERIALIZACIÓN RandomAccessFile ====================
    
    /**
     * Escribe el objeto en el archivo en la posición actual del puntero.
     * Usa writeChars (2 bytes por char) para strings de longitud fija.
     */
    public void escribir(RandomAccessFile file) throws IOException {
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, cui, TAM_CUI);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, nombresApellidos, TAM_NOMBRES);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, fechaNacimiento, TAM_FECHA_NAC);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, sexo, TAM_SEXO);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, telefono, TAM_TELEFONO);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, correo, TAM_CORREO);
        com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.escribirString(file, tipoSangre, TAM_TIPO_SANGRE);
        file.writeChar(activo ? '1' : '0'); // boolean como char
    }

    /**
     * Lee un registro desde el archivo en la posición actual del puntero.
     * Retorna this para encadenamiento.
     */
    public Paciente leer(RandomAccessFile file) throws IOException {
        this.cui = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_CUI);
        this.nombresApellidos = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_NOMBRES);
        this.fechaNacimiento = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_FECHA_NAC);
        this.sexo = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_SEXO);
        this.telefono = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_TELEFONO);
        this.correo = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_CORREO);
        this.tipoSangre = com.mycompany.m.archivos.practica1.s2_2026.Archivos.UtilidadesArchivo.leerString(file, TAM_TIPO_SANGRE);
        this.activo = (file.readChar() == '1');
        return this;
    }

    // ==================== UTILIDADES ====================
    
    public static int getTamanioRegistro() { return TAM_REGISTRO; }

    @Override
    public String toString() {
        return "Paciente{" +
                "cui='" + cui + '\'' +
                ", nombresApellidos='" + nombresApellidos + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", sexo='" + sexo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", tipoSangre='" + tipoSangre + '\'' +
                ", activo=" + activo + '}';
    }
}