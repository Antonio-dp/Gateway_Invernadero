/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gateway;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author tonyd
 */
public class Gateway implements Runnable{
    private int puerto;
    private BufferedReader in;
    private BufferedWriter out;
    private Socket sc;
    private static Gateway gateway;
    
    public Gateway(int puerto){
        this.puerto = puerto;
    }
    
    public static Gateway getInstance(){
        if(gateway == null){
            gateway = new Gateway(9000);
        }
        return gateway;
    }
    
    public void iniciarListener(){
        new Thread(gateway).start();
    }
    
    @Override
    public void run() {
        final String HOST = "127.0.0.1";
        try{
            sc = new Socket(HOST, puerto);
            in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(sc.getOutputStream()));
            while(true){
                String mensaje = in.readLine();
                if(mensaje == null) break;
                
            }
        } catch(IOException ie){
            cerrarTodo(sc, in, out);
        }
    }

    public void enviarMensaje(String mensaje){
        try{
                out.write(mensaje);
                out.newLine();
                out.flush();
        } catch(IOException io){
            io.printStackTrace();
        }
        
    }
    
    public void cerrarTodo(Socket socket, BufferedReader in, BufferedWriter out){
        try{
            socket.close();
            in.close();
            out.close();
        } catch(IOException io){
            io.printStackTrace();
        }
        
    }
    
}
