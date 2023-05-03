/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gateway;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author tonyd
 */
public class Gateway{
 private String host;
    private int port;
    
    public Gateway(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void sendData(String datos) throws IOException, InterruptedException {
        try (Socket socket = new Socket(host, port)) {
            OutputStream outputStream = socket.getOutputStream();
            while (true) {
                outputStream.write(datos.getBytes());
                outputStream.flush();
                outputStream.close();
            }
        }
    }
    
}
