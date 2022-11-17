/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa.Proyect;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    ObjectOutputStream output;
    ObjectInputStream input;
    Socket connection;
    String message;

    public Connection() {

    }


}

