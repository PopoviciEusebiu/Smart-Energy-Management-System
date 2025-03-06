package MonitoringAndCommunication.microservice.service;

import MonitoringAndCommunication.microservice.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public interface DeviceSyncService {

    @RabbitListener(queues = RabbitMqConfig.DEVICE_QUEUE_NAME)
    void receiveDeviceUpdate(String message);
}
