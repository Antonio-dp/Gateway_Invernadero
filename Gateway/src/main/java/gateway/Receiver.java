/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gateway;

import DAOS.SensorDAO;
import com.rabbitmq.client.AMQP;
import java.io.IOException;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import conexiones.ConexionBD;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 *
 * @author Vastem
 */
public class Receiver {
    private static PrivateKey privateKey;
    private final static String QUEUE_NAME = "cola_datos";
    static Gateway gw;
    private static ConexionBD conn = new ConexionBD();
    private static SensorDAO sen;

    public static void main(String[] argv) throws Exception {
        sen = new SensorDAO(conn);
        gw = new Gateway("127.0.0.1", 9000);

        // Configurar la clave privada para descifrar
        String privateKeyString = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQClh2OMregfYA3ZJshz0KvKrFU7putrxu0zJHwyMQMgYB7x/+2a6uSrfwFbSbavgv6/u6J2tDvmysaLqcG6O36wBDS5kaJT5FMIQlmISNIGumGUsqG0+IASBX47C+lbeBghx8jGYsxi8hsn6pW6mPppjWLWfOYkTOUheGZBRpuyWtGXAZXRZzSN5hidNCyvH4Qm9i4Ie9fxR9+lcWMSO+hljUBCPz3MeES0ybB+4Bwz4yPZlv3oRWDZIaduP0bX3N5+ysPYq3FuxUUdquKy0jz0v5UeRDKpCwajvpzzCaRRD+2RtVXW4vwlVX5C4LaS0C69zNvwVXp1L3AJzT69mSRHAgMBAAECggEAO/ncZiK1Enk5S080HlKjjGH42A5ZCsofNAKqRX1gxqBNVh4HN7SYelMgaLVCzkFGkK4p5ZzUf4FFg2FU4megNaKwf/R2vrLiKwHvcuP/xZROuxQmZ24K6Xy4Cij8urm+9K5w3wD5UXirwkDLU7sOMKScBV9n/AsokoIPeuNckrijPvj6kalf0uMcAkthH4wE1BymdixJGAwlLCfxJr02RTqccXM4KXwlMoxRldPo2HPXyibC8a3/RsjHek63271g1udb5QU0Zhb1Io8sCubRorZXIYjeYOp6pjeowUOonc/fLnb5qWNmvfSIPCuvBgg4goE0q5KQ8pHQ04tu/+O3sQKBgQC3VXU3Hhl6Ik7+BnfSPmy+f5o9iuRO1TJS4BG9vRK5bDyrDaaujpHhr4/c9Pt+xn+Jru2g8obA/aURY2d9d2SN0opVPmCGiz/jxuJxRXyPC1jFMZFFH3bQn+JBniRsI7+iFtAUivnhy2+OyvqYZ1eXxcizwBVKSBn9CpEJwdZlbQKBgQDnI0uKE0ZLhsp0KWznxBuJFgS9CTHb/RY/jlClf3Bpcall9qc/QzNlr8LetLg77BPB3InJBYR7+Hb6LxXyXECM/7xb7SRJGUJgF/iP2X/kGdQ9F4qsZwGu/UJ2L/S+ugTXIWRWW7Qle92LwqHMTcMUNj7xTlZZC2V9+o20I5lEAwKBgH8YPldEhZL2394Yq85Tul1h5pKNi/Let7FeZs4rmiRzVaebohbW/WkApXIfX44mm9neLBxspWB7NojUabVAJLRw5bdss8vyEwucH/U4n74mtpaV40iRJHRCsr6cnFFfgwUiXYQwSETxHFhYfNtUoRO1aIq8OcYyL9oNDjk+aJwNAoGBAIFM91cYjTx2/Q6alffZduXrUV9Go4PDQwzu2iKa9hGmqfMGVm1HdJswBb18L4wl9q9+Zf30fjazuise6BIalWnLLl3mfWP/I2iKQFyIecjqwhYi80qanRB9UQ64qRuBCHAol+7Pgilt73Pdv5GA9t55siBfiGJrw60D2v2o5HZtAoGAMCHSr4n4dZebYu2Gn6jnSYhPsW8oiuuS606i6/gzqEoPgjujG1KvMN6L9YIFEPwWEabnCgoa5tKzUWWHMVGp7OIgJ1l7mFdpU8k5fr0DCPqnyp2Mq/vLyG6eCcORn8ogbs50hMOd5PASMVgXRUVwBpCfilGJ4TlX/u9eFIl6vY0="; // Reemplazar con tu clave privada generada
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

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
                    String decryptedData = decryptData(body);

                    System.out.println("Mensaje recibido: " + decryptedData);
                    send(decryptedData);
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

    private static String decryptData(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes);
    }
}
