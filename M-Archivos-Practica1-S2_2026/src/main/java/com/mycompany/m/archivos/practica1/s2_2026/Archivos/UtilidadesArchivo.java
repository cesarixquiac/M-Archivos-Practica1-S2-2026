/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.m.archivos.practica1.s2_2026.Archivos;

/**
 *
 * @author cesar
 */
import java.io.IOException;
import java.io.RandomAccessFile;

public class UtilidadesArchivo {
    
    // Método para escribir un String de longitud fija en el archivo
    public static void escribirString(RandomAccessFile file, String texto, int longitud) throws IOException {
        StringBuffer buffer = null;
        if (texto != null) {
            buffer = new StringBuffer(texto);
        } else {
            buffer = new StringBuffer(longitud);
        }
        
        // Ajustamos la longitud exactamente al tamaño especificado
        buffer.setLength(longitud);
        
        // Escribimos los caracteres en el archivo
        file.writeChars(buffer.toString());
    }
    
    //Metodo para leer un String de longitud fija
    public static String leerString(RandomAccessFile file, int longitud) throws IOException {
        char[] campo = new char[longitud];
        for (int i = 0; i < longitud; i++) {
            campo[i] = file.readChar();
        }
        // Retornamos el String eliminando los caracteres nulos y espacios extra
        return new String(campo).replace('\0', ' ').trim(); 
    }
}