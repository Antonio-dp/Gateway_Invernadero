/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gateway;

import datos.DataGenerator;
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
    private DataGenerator dataGenerator;
    
    public Gateway(String host, int port) {
        this.host = host;
        this.port = port;
        this.dataGenerator = new DataGenerator();
    }
    
    public void sendData() throws IOException, InterruptedException {
        try (Socket socket = new Socket(host, port)) {
            OutputStream outputStream = socket.getOutputStream();
            while (true) {
                String data = dataGenerator.generateData();
                outputStream.write(data.getBytes());
                outputStream.flush();
                Thread.sleep(1000); // Espera un segundo antes de enviar los datos siguientes
            }
        }
    }
    
}
