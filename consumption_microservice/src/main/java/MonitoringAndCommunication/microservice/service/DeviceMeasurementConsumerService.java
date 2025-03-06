package MonitoringAndCommunication.microservice.service;

import MonitoringAndCommunication.microservice.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public interface DeviceMeasurementConsumerService {
    @RabbitListener(queues = RabbitMqConfig.MEASUREMENT_QUEUE_NAME)
    void receiveMessage(@Payload String message);
}
