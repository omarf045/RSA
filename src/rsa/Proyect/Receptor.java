package rsa.Proyect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
import javax.crypto.spec.SecretKeySpec;

public class Receptor {

    public static void main(String args[]) throws UnknownHostException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InterruptedException {

        int AUTH_PORT = 5000, CLIENT_SERVER_PORT = 5001;
        Encoder encoder = new Encoder();
        Scanner scanner = new Scanner(System.in);
        Comunication comunicator = new Comunication();
        Encryptor cryptor = new Encryptor();

        //  Autoridad
        System.out.println(" ¬ Ingresa la IP de la autoridad certificadora: ");
        InetAddress ipC = InetAddress.getByName(scanner.nextLine());

        //  Clave Privada Receptor
        Socket socketPrivateKeyReceptor = new Socket(ipC, AUTH_PORT);
        PrivateKey privateKeyReceptor = encoder.privateKey(socketPrivateKeyReceptor);

        //  Clave Publica Emisor
        Socket socketPublicKeyEmisor = new Socket(ipC, AUTH_PORT);
        PublicKey publicKeyEmisor = encoder.publicKey(socketPublicKeyEmisor);

        System.out.println(" └ Claves recibidas");

        //  Emisor
        System.out.println(" ¬ Ingresa la IP del emisor: ");
        InetAddress ipE = InetAddress.getByName(scanner.nextLine());

        // Recibir clave secreta cifrada
        byte[] cipheredSecretKey = comunicator.getBytes(ipE, CLIENT_SERVER_PORT);

        System.out.println(" └ Clave secreta recibida");

        //  Descifrar clave secreta
        byte[] encodedSecretKey = cryptor.RSADecryption(privateKeyReceptor, cipheredSecretKey);
        SecretKey secretKey = new SecretKeySpec(encodedSecretKey, 0, encodedSecretKey.length, "AES");

        Client client = new Client(ipE, CLIENT_SERVER_PORT);

        client.setPrivateKey(privateKeyReceptor);
        client.setPublicKey(publicKeyEmisor);
        client.setSecretKey(secretKey);

        client.start();

        while (client.isReady == false) {
            System.out.println("Esperando conexion...");
            Thread.sleep(1000);
        }

        String msg = "";

        while (!msg.contains("exit()")) {
            System.out.print(">>> ");
            msg = "Receptor: " + scanner.nextLine();
            client.sendData(msg);
        }

        client.closeConnection();
    }

}
