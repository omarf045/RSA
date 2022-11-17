package rsa.Proyect;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Client extends Thread {

    InetAddress ip;

    Encryptor cryptor = new Encryptor();
    PrivateKey privateKey;
    PublicKey publicKey;
    SecretKey secretKey;
    ObjectOutputStream output;
    ObjectInputStream input;
    Socket connection;
    String message;
    boolean isReady = false;
    int port;

    public Client(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void startClient() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            //  Se conecta al servidor
            connectToServer(ip, port);
            //  Obtiene los flujos
            getFlows();
            //  Inicia el procesamiento de los datos recibidos
            processConnection();
        } catch (EOFException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
        }
    }

    public void connectToServer(InetAddress ip, int port) throws IOException {
        connection = new Socket(ip, port);
    }

    public void getFlows() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
    }

    public void processConnection() throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        String cipheredData, cipheredMessage, cipheredAddendum;

        // Indica que la conexion esta lista
        isReady = true;
        do {
            try {
                //  Recibe los datos cifrados
                cipheredData = (String) input.readObject();

                // Divide los datos cifrados en el addendum cifrado y el mensaje cifrado
                cipheredAddendum = cipheredData.substring(0, 256);
                cipheredMessage = cipheredData.substring(256);

                // Convierte los datos de String (hexadecimal) a binario
                byte[] bytesAddendum = hexToBytes(cipheredAddendum);
                byte[] bytesMessage = hexToBytes(cipheredMessage);

                //  Descifra el mensaje con la clave secreta
                byte[] encodedMsj = cryptor.AESDecryption(secretKey, bytesMessage);
                message = new String(encodedMsj, StandardCharsets.UTF_8);

                //  Descifrar addendum con la clave publica de quien mando el mensaje
                byte[] encodedAddendum = cryptor.RSADecryption(publicKey, bytesAddendum);
                String addendum = new String(encodedAddendum, StandardCharsets.UTF_8);

                //  Generar addendum del mensaje descifrado
                String msjAddendum = new Addendum().generateHash(message);

                //Comparar addendums
                if (addendum.equals(msjAddendum)) {
                    //  Si el mensaje es "exit()" cierra la conexion
                    if (message.contains("exit()")) {
                        System.exit(0);
                    } else {
                        //  Imprime el mensaje
                        System.out.println(message);
                        System.out.print(">> ");
                    }
                }
            } catch (ClassNotFoundException ex) {
            }
        } while (!message.contains("exit()"));

    }

    public void sendData(String data) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        try {
            //  Generacion de addendum a partir del mensaje
            String addendum = new Addendum().generateHash(data);

            //  Cifrado del mensaje y addendum
            byte[] cipheredMsg = cryptor.AESEncryption(secretKey, data);
            byte[] cipheredAddendum = cryptor.RSAEncryption(privateKey, addendum.getBytes());

            //  Convierte los bytes a String en hexadecimal
            String hexMsg = bytesToHex(cipheredMsg);
            String hexAddendum = bytesToHex(cipheredAddendum);

            //  Concatena el addendum cifrado con el mensaje cifrado
            String cipheredData = hexAddendum + hexMsg;

            //  Manda los datos
            output.writeObject(cipheredData);
            output.flush();
        } catch (IOException ex) {
        }
    }

    public void closeConnection() {
        try {
            //  Cierra el socket y los objectStreams
            output.close();
            input.close();
            connection.close();
        } catch (IOException ex) {
        }
    }

    //  Flag para saber si esta lista la conexion
    public boolean isReady() {
        return isReady;
    }

    //  Metodo para iniciar en segundo plano el cliente
    @Override
    public void run() {
        try {
            startClient();
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //  Setters de las claves a utilizar
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    // Convertir de String (hexadecimal) a bytes
    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    // Convertir de bytes a String (hexadecimal)
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(String.format("%02x", temp));
        }
        return result.toString();
    }
}
