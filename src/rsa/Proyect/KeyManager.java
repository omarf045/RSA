/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa.Proyect;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;

/**
 *
 * @author Omar Pulido
 */
public class KeyManager {

    public KeyManager() {
    }

    public SecretKey generateSecretKey() throws NoSuchAlgorithmException {

        SecretKey key;
        int KEY_SIZE = 128;
        
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        key = keyGenerator.generateKey();

        return key;
    }
}
