package com.cookim.cookimws.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
}
