package rsa.Proyect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyDelivery {

    public KeyDelivery() {
    }

    public void sendPrivateKey(PrivateKey privateKey, ServerSocket servSock) {
        try {
            Socket sock = servSock.accept();
            System.out.println("Conection Accepted mf");
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(privateKey.getEncoded().length);
            sock.getOutputStream().write(b.array());
            sock.getOutputStream().write(privateKey.getEncoded());
            sock.getOutputStream().flush();
            sock.close();
            servSock.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void sendPublicKey(PublicKey publicKey, ServerSocket servSock) {
        try {
            Socket sock = servSock.accept();
            System.out.println("Conection Accepted mf");
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(publicKey.getEncoded().length);
            sock.getOutputStream().write(b.array());
            sock.getOutputStream().write(publicKey.getEncoded());
            sock.getOutputStream().flush();
            sock.close();
            servSock.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
