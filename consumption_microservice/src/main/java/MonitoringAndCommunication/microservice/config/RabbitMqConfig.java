package MonitoringAndCommunication.microservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {

    public static final String MEASUREMENT_QUEUE_NAME = "device_measurements";
    public static final String DEVICE_QUEUE_NAME = "device_updates";

    public static final String EXCHANGE_NAME = "device_exchange";

    @Bean
    public Queue measurementQueue() {
        return new Queue(MEASUREMENT_QUEUE_NAME, true);
    }

    @Bean
    public Queue deviceQueue() {
        return new Queue(DEVICE_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding measurementBinding(Queue measurementQueue, TopicExchange exchange) {
        return BindingBuilder.bind(measurementQueue).to(exchange).with("device.measurement.#");
    }

    @Bean
    public Binding deviceBinding(Queue deviceQueue, TopicExchange exchange) {
        return BindingBuilder.bind(deviceQueue).to(exchange).with("device.update.#");
    }
}
