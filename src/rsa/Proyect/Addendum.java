package rsa.Proyect;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Addendum {

    public Addendum() {}
    
    public String generateHash(String msj) throws NoSuchAlgorithmException {

        String addendum;

        /*
        String[] noSpace = msj.replaceAll(" ", "").split("\\s+");
        int p = Integer.parseInt(noSpace[0]);
        
        for (int x = 1; x < noSpace.length; x++){
            String xor = String.valueOf(noSpace[x]);
            int xorInt = Integer.parseInt(xor);
            p = (p^xorInt);
        }
        addendum = String.valueOf(p);      
         */
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(
                msj.getBytes(StandardCharsets.UTF_8));
        
        addendum = bytesToHex(encodedHash);

        return addendum;
    }
    
    private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if(hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
}
}
