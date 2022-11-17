package rsa.Proyect;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Generator {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public Generator() {
        try {
            //  Se genera un par de claves usando el algoritmo RSA de longitud 1024
            KeyPairGenerator keyGenEmisor = KeyPairGenerator.getInstance("RSA");
            keyGenEmisor.initialize(1024);
            KeyPair pairEmisor = keyGenEmisor.generateKeyPair();
            privateKey = pairEmisor.getPrivate();
            publicKey = pairEmisor.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

}
