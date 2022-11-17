package rsa.Proyect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Comunication {

    public void sendBytes(int port, byte[] message) {
        try {

            ServerSocket socket = new ServerSocket(port);
            System.out.println("Esperanding al Receptor...");

            Socket dataSocket = socket.accept();
            System.out.println("Connection Accepted mf");

            dataSocket.getOutputStream().write(message);
            dataSocket.getOutputStream().flush();
            dataSocket.close();
            socket.close();

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public byte[] getBytes(InetAddress ip, int port) throws IOException {

        Socket socket = new Socket(ip, port);

        byte[] data = new byte[128];
        socket.getInputStream().read(data);
        socket.close();

        return data;
    }

}
