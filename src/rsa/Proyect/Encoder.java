package rsa.Proyect;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Encoder {

    public Encoder() {}
    
    // Se obtienen las claves en bytes y se encodean a sus tipos PrivateKey y PublicKey
    
    public PublicKey publicKey(Socket socketPublicK) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        
        byte[] lenb = new byte[4];
        socketPublicK.getInputStream().read(lenb, 0, 4);
        ByteBuffer bb = ByteBuffer.wrap(lenb);
        int len = bb.getInt();
        byte[] publicKeyBytes = new byte[len];
        socketPublicK.getInputStream().read(publicKeyBytes);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(ks);
        socketPublicK.close();

        return publicKey;
    }

    public PrivateKey privateKey(Socket socketPrivateK) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        
        byte[] lenb = new byte[4];
        socketPrivateK.getInputStream().read(lenb, 0, 4);
        ByteBuffer bb = ByteBuffer.wrap(lenb);
        int len = bb.getInt();
        byte[] privateKeyBytes = new byte[len];
        socketPrivateK.getInputStream().read(privateKeyBytes);
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(ks);
        socketPrivateK.close();
        
        return privateKey;
    }

}
