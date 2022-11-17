package rsa.Proyect;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;

public class AuthCert {

    public static void main(String args[]) throws NoSuchAlgorithmException {

        int PORT = 5000;

        // Generacion de llaves del emisor
        Generator emisorKeys = new Generator();

        // Generacion de llaves del receptor
        Generator receptorKeys = new Generator();

        try {
            KeyDelivery kd = new KeyDelivery();

            //  Se espera la conexion del emisor
            ServerSocket ssEmisorPriv = new ServerSocket(PORT);
            System.out.println("Esperando al emisor...");

            //  Se manda la clave privada del emisor al emisor
            kd.sendPrivateKey(emisorKeys.getPrivateKey(), ssEmisorPriv);

            //  Se manda la clave publica del receptor al emisor
            ServerSocket ssReceptorPubl = new ServerSocket(PORT);

            kd.sendPublicKey(receptorKeys.getPublicKey(), ssReceptorPubl);
            System.out.println("Claves enviadas al emisor");

            //  Se manda la clave privada del receptor al receptor
            ServerSocket ssReceptorPriv = new ServerSocket(PORT);
            System.out.println("Esperando al receptor...");

            kd.sendPrivateKey(receptorKeys.getPrivateKey(), ssReceptorPriv);

            //  Se manda la clave publica del emisor al receptor
            ServerSocket ssEmisorPubl = new ServerSocket(PORT);

            kd.sendPublicKey(emisorKeys.getPublicKey(), ssEmisorPubl);
            System.out.println("Claves enviadas al receptor");

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
