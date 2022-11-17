package rsa.Proyect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Emisor {

    public static void main(String args[]) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InterruptedException {
        int AUTH_PORT = 5000, CLIENT_SERVER_PORT = 5001;

        try {
            Encoder encoder = new Encoder();
            Scanner scanner = new Scanner(System.in);
            Encryptor cryptor = new Encryptor();
            Comunication comunicator = new Comunication();

            //  Se conecta a la autoridad certificadora
            System.out.println(" ¬ Ingresa la IP de la autoridad certificadora: ");
            InetAddress ipC = InetAddress.getByName(scanner.nextLine());

            //  Se recibe la clave privada del emisor
            Socket socketPrivateKeyEmisor = new Socket(ipC, AUTH_PORT);
            PrivateKey privateKeyEmisor = encoder.privateKey(socketPrivateKeyEmisor);

            //  Se recibe la clave publica del receptor
            Socket socketPublicKeyReceptor = new Socket(ipC, AUTH_PORT);
            PublicKey publicKeyReceptor = encoder.publicKey(socketPublicKeyReceptor);

            System.out.println(" └ Claves recibidas");

            //  Se genera de clave secreta
            SecretKey secretKey = new KeyManager().generateSecretKey();

            //  Cifrado de la clave secreta
            byte[] rawSecretKey = secretKey.getEncoded();
            byte[] cipheredSecretKey = cryptor.RSAEncryption(publicKeyReceptor, rawSecretKey);

            //  Se envia la clave secreta cifrada
            comunicator.sendBytes(CLIENT_SERVER_PORT, cipheredSecretKey);

            System.out.println(" └ Clave secreta enviada");

            //  Se instancia la clase Server
            Server server = new Server(CLIENT_SERVER_PORT);

            // Setteo de las claves a utilizar
            server.setPrivateKey(privateKeyEmisor);
            server.setPublicKey(publicKeyReceptor);
            server.setSecretKey(secretKey);

            //  Se inicia la conexion
            server.start();

            //  Espera a que la conexion este lista
            while (server.isReady == false) {
                System.out.println("Esperando conexion...");
                Thread.sleep(1000);
            }

            String msg = "";
            
            //  Ciclo para mandar los mensajes
            while (!msg.contains("exit()")) {
                System.out.print(">>> ");
                msg = "Emisor: " + scanner.nextLine();
                server.sendData(msg);
            }

            //  Se cierra la conexion
            server.closeConnection();

        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
        }
    }
}
