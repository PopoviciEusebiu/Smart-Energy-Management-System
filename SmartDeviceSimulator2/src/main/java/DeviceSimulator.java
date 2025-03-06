import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DeviceSimulator {

    private static final Logger logger = LoggerFactory.getLogger(DeviceSimulator.class);
    private static final long hourStartTimestamp = System.currentTimeMillis();

    private static int count = 0;

    private static int hour =  LocalDateTime.ofInstant(Instant.ofEpochMilli(hourStartTimestamp), ZoneId.systemDefault()).getHour();

    public static void main(String[] args) throws Exception {



        Properties properties = new Properties();
        try (InputStream input = DeviceSimulator.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                return;
            }
            properties.load(input);
        }

        String host = properties.getProperty("rabbitmq.host");
        String username = properties.getProperty("rabbitmq.username");
        String password = properties.getProperty("rabbitmq.password");
        String exchangeName = properties.getProperty("rabbitmq.exchange_name");
        String deviceId = properties.getProperty("device.id");
        String filePath = properties.getProperty("file.path");
        int messageInterval = Integer.parseInt(properties.getProperty("message.interval"));


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);

        ObjectMapper objectMapper = new ObjectMapper();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath));
             Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(exchangeName, "topic", true);

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double measurementValue = Double.parseDouble(values[0]);

                Map<String, Object> message = new HashMap<>();
                message.put("timestamp", System.currentTimeMillis());
                message.put("device_id", deviceId);
                message.put("measurement_value", measurementValue);
                message.put("hourStartTimestamp", hourStartTimestamp);
                message.put("firstHour", hour);

                count++;

                if(count >= 6)
                {
                    hour = ( hour + 1) % 24;
                    count = 0;
                }


                String jsonMessage = objectMapper.writeValueAsString(message);

                channel.basicPublish(exchangeName, "device.measurement", null, jsonMessage.getBytes());
                System.out.println("Message sent: " + jsonMessage);

                Thread.sleep(messageInterval);
            }
        }
    }
}
