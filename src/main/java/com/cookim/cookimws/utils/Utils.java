package com.cookim.cookimws.utils;

import com.cookim.cookimws.model.Step;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tiene los metodos con los cuales se crearan los tokens de cada usuario
 *
 * @author cookimadmin
 */
public class Utils {

    /**
     * Retorna la representación en hash SHA-256 de una cadena de texto dada
     * como entrada.
     *
     * @param input la cadena de texto a encriptar.
     * @return el hash SHA-256 de la cadena de texto dada como una cadena
     * hexadecimal.
     */
    public static String getSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error al generar hash SHA-256: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retorna la representación en hash MD5 de una cadena de texto dada como
     * entrada.
     *
     * @param input la cadena de texto a encriptar.
     * @return el hash MD5 de la cadena de texto dada como una cadena hexadecimal.
     */
    public static String getMD5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error al generar hash MD5: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Método auxiliar que verifica si todos los archivos de imagen de los pasos
     * han sido eliminados.
     *
     * @param steps la lista de pasos de la receta
     * @return true si todos los archivos de imagen han sido eliminados, false
     * en caso contrario
     */
    public boolean areAllStepImagesRemoved(List<Step> steps) {
        for (Step step : steps) {
            String pathImg = "/var/www/html"+step.getPath();
            if (pathImg != null && isFileExists(pathImg)) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Método auxiliar para verificar si un archivo existe en el servidor.
     *
     * @param filePath la ruta del archivo
     * @return true si el archivo existe, false en caso contrario
     */
    public boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Método auxiliar para eliminar un archivo del servidor.
     *
     * @param filePath la ruta del archivo a eliminar
     */
    public void deleteFileFromServer(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
