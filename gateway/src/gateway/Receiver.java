/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gateway;

import com.rabbitmq.client.AMQP;
import java.io.IOException;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vastem
 */
public class Receiver {

    private final static String QUEUE_NAME = "cola_datos";
    static Gateway gw;

    public static void main(String[] argv) throws Exception {
        gw = new Gateway("127.0.0.1", 9000);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("Esperando mensajes...");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                
                try {
                    String message = new String(body, "UTF-8");
                    System.out.println("Mensaje recibido: " + message);
                    send(message);
                } catch (Exception ex) {
                    //Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            public void send(String msg) throws IOException, InterruptedException{
                gw.sendData(msg);
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
    
}
