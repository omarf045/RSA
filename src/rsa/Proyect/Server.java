/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa.Proyect;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author User
 */
public class Server extends Thread {

    ServerSocket socket;

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

    public Server(int port) {
        this.port = port;
    }

    public void startServer() throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            waitConnection(port);
            getFlows();
            processConnection();
        } catch (EOFException ex) {
            System.err.println(ex);
        }
    }

    public void waitConnection(int port) throws IOException {
        socket = new ServerSocket(port);

        connection = socket.accept();
        System.out.println("Conexion establecida con: " + connection.getInetAddress().getHostAddress());
    }

    public void getFlows() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
    }

    public void processConnection() throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        String cipheredData, cipheredMessage, cipheredAddendum;

        isReady = true;
        do {
            try {
                cipheredData = (String) input.readObject();

                cipheredAddendum = cipheredData.substring(0, 256);
                cipheredMessage = cipheredData.substring(256);

                byte[] bytesAddendum = hexToBytes(cipheredAddendum);
                byte[] bytesMessage = hexToBytes(cipheredMessage);

                byte[] encodedMsj = cryptor.AESDecryption(secretKey, bytesMessage);
                message = new String(encodedMsj, StandardCharsets.UTF_8);

                //  Descifrar addendum
                byte[] encodedAddendum = cryptor.RSADecryption(publicKey, bytesAddendum);
                String addendum = new String(encodedAddendum, StandardCharsets.UTF_8);

                String msjAddendum = new Addendum().generateHash(message);
                if (addendum.equals(msjAddendum)) {
                    if (message.contains("exit()")) {
                        System.exit(0);
                    } else {
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

            // Convertir los bytes a String
            String hexMsg = bytesToHex(cipheredMsg);
            String hexAddendum = bytesToHex(cipheredAddendum);

            String cipheredData = hexAddendum + hexMsg;

            output.writeObject(cipheredData);
            output.flush();
        } catch (IOException ex) {
        }
    }

    public void closeConnection() {
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException ex) {
        }
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
        }
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(String.format("%02x", temp));
        }
        return result.toString();
    }
}