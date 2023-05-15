/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gateway;

import DAOS.SensorDAO;
import Entidades.Registro;
import Entidades.Sensor;
import com.rabbitmq.client.AMQP;
import java.io.IOException;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import conexiones.ConexionBD;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 *
 * @author Vastem
 */
public class Receiver {

    private final static String QUEUE_NAME = "cola_datos";
    static Gateway gw;
    private static ConexionBD conn = new ConexionBD();
    private static SensorDAO sen;

    public static void main(String[] argv) throws Exception {
        sen = new SensorDAO(conn);
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
//                    String[] values = message.split(",");
//                    float temperatura = Float.parseFloat(values[0]);
//                    float humedad = Float.parseFloat(values[1]);
//                    long fechaMillis = Long.parseLong(values[2]);
//                    String sensorId = values[3];
//
//                    ObjectId id = new ObjectId(sensorId);
//                    Sensor sensor = sen.consultarSensor(id);
//
//                     Crear el objeto Registro
//                    Registro registro = new Registro();
//                    registro.setTemperatura(temperatura);
//                    registro.setHumedad(humedad);
//                    Calendar fecha = Calendar.getInstance();
//                    fecha.setTimeInMillis(fechaMillis);
//                    registro.setFecha(fecha);
//                    registro.setSensor(sensor);
                    
                    
                    //channel.basicAck(envelope.getDeliveryTag(), false);
                    
                    System.out.println("Mensaje recibido: " + message);
                    send(message);
                } catch (Exception ex) {
                    //Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            public void send(String msg) throws IOException, InterruptedException {
                gw.sendData(msg);
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

}
