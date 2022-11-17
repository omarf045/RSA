package rsa.Proyect;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Encryptor {

    public Encryptor() {
    }

    public byte[] RSAEncryption(Key key, byte[] msj) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] msjRSA = encryptCipher.doFinal(msj);
        return msjRSA;
    }

    public byte[] RSADecryption(Key key, byte[] msj) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.DECRYPT_MODE, key);

        byte[] msjRSA = encryptCipher.doFinal(msj);
        return msjRSA;
    }

    public byte[] AESEncryption(SecretKey key, String msj) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {

        Cipher cipher = Cipher.getInstance("AES");
        byte[] bytes = msj.getBytes("UTF8");

        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes = cipher.doFinal(bytes);

        return cipherBytes;
    }

    public byte[] AESDecryption(SecretKey key, byte[] msj) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {

        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(msj);

        return bytes;
    }
}
