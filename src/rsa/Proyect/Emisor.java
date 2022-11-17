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

            //  Autoridad
            System.out.println(" ¬ Ingresa la IP de la autoridad certificadora: ");
            InetAddress ipC = InetAddress.getByName(scanner.nextLine());

            //  Clave Privada Emisor
            Socket socketPrivateKeyEmisor = new Socket(ipC, AUTH_PORT);
            PrivateKey privateKeyEmisor = encoder.privateKey(socketPrivateKeyEmisor);

            //  Clave Publica Receptor
            Socket socketPublicKeyReceptor = new Socket(ipC, AUTH_PORT);
            PublicKey publicKeyReceptor = encoder.publicKey(socketPublicKeyReceptor);

            System.out.println(" └ Claves recibidas");

            /*System.out.println(" ¬ Escriba su mensaje: ");
            String msj = scanner.nextLine();*/
            //  Generacion de clave secreta
            SecretKey secretKey = new KeyManager().generateSecretKey();

            //  Cifrado de la clave secreta
            byte[] rawSecretKey = secretKey.getEncoded();
            byte[] cipheredSecretKey = cryptor.RSAEncryption(publicKeyReceptor, rawSecretKey);

            //  Envio de la clave secreta cifrada
            comunicator.sendBytes(CLIENT_SERVER_PORT, cipheredSecretKey);

            System.out.println(" └ Clave secreta enviada");

            Server server = new Server(CLIENT_SERVER_PORT);

            server.setPrivateKey(privateKeyEmisor);
            server.setPublicKey(publicKeyReceptor);
            server.setSecretKey(secretKey);

            server.start();

            while (server.isReady == false) {
                System.out.println("Esperando conexion...");
                Thread.sleep(1000);
            }

            String msg = "";

            while (!msg.contains("exit()")) {
                System.out.print(">>> ");
                msg = "Emisor: " + scanner.nextLine();
                server.sendData(msg);
            }

            server.closeConnection();

        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
        }
    }

    public static byte[] joinByteArray(byte[] byte1, byte[] byte2, byte[] byte3) {
        return ByteBuffer.allocate(byte1.length + byte2.length + byte3.length)
                .put(byte1)
                .put(byte2)
                .put(byte3)
                .array();
    }
}
