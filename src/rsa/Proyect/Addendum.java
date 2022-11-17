package rsa.Proyect;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Addendum {

    public Addendum() {
    }

    //  Se genera el addendum usando una funcion hash (SHA-256) (256 bits)
    public String generateHash(String msj) throws NoSuchAlgorithmException {

        String addendum;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(
                msj.getBytes(StandardCharsets.UTF_8));

        addendum = bytesToHex(encodedHash);

        return addendum;
    }

    //  Convertir bytes a String (hexadecimal)
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
