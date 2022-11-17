package rsa.Proyect;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;

public class AuthCert {

    public static void main(String args[]) throws NoSuchAlgorithmException {

        int PORT = 5000;

        // Llaves Emisor
        Generator emisorKeys = new Generator();

        //Llaves Receptor
        Generator receptorKeys = new Generator();

        try {
            KeyDelivery kd = new KeyDelivery();

            //Emisor
            ServerSocket ssEmisorPriv = new ServerSocket(PORT);
            System.out.println("Esperanding al Emisor...");

            kd.sendPrivateKey(emisorKeys.getPrivateKey(), ssEmisorPriv);

            ServerSocket ssReceptorPubl = new ServerSocket(PORT);

            kd.sendPublicKey(receptorKeys.getPublicKey(), ssReceptorPubl);
            System.out.println("Claves Enviadas al Emisor");

            //Receptor
            ServerSocket ssReceptorPriv = new ServerSocket(PORT);
            System.out.println("Esperanding al Receptor...");

            kd.sendPrivateKey(receptorKeys.getPrivateKey(), ssReceptorPriv);

            ServerSocket ssEmisorPubl = new ServerSocket(PORT);

            kd.sendPublicKey(emisorKeys.getPublicKey(), ssEmisorPubl);
            System.out.println("Claves Enviadas al Receptor");

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
