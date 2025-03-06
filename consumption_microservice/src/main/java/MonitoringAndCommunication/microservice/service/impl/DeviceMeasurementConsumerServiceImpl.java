package MonitoringAndCommunication.microservice.service.impl;

import MonitoringAndCommunication.microservice.dto.DeviceMeasurementDTO;
import MonitoringAndCommunication.microservice.model.Device;
import MonitoringAndCommunication.microservice.model.DeviceMeasurement;
import MonitoringAndCommunication.microservice.model.HourlyConsumption;
import MonitoringAndCommunication.microservice.repository.DeviceMeasurementRepository;
import MonitoringAndCommunication.microservice.repository.DeviceRepository;
import MonitoringAndCommunication.microservice.repository.HorulyConsumptionRepository;
import MonitoringAndCommunication.microservice.service.DeviceMeasurementConsumerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeviceMeasurementConsumerServiceImpl implements DeviceMeasurementConsumerService {

    private final ObjectMapper objectMapper;
    private final DeviceMeasurementRepository deviceMeasurementRepository;
    private final DeviceRepository deviceRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final HorulyConsumptionRepository horulyConsumptionRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "lock:";

    @Override
    public void receiveMessage(@Payload String message) {
        System.out.println("Message received: " + message);

        try {
            DeviceMeasurementDTO deviceMeasurementDTO = objectMapper.readValue(message, DeviceMeasurementDTO.class);
            String deviceId = deviceMeasurementDTO.getDevice_id().toString();

            String countKey = "device:" + deviceId + ":count";
            String consumptionKey = "device:" + deviceId + ":hourlyConsumption";
            String lockKey = LOCK_PREFIX + "device:" + deviceId;

            boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(10));
            if (!lockAcquired) {
                System.out.println("Another instance is processing this device: " + deviceId);
                return;
            }

            try {
                long count = redisTemplate.opsForValue().increment(countKey, 1);
                double hourlyConsumption = redisTemplate.opsForValue().increment(consumptionKey, deviceMeasurementDTO.getMeasurement_value());

                System.out.println("Device ID: " + deviceId);
                System.out.println("Count: " + count);
                System.out.println("Hourly Consumption: " + hourlyConsumption + " kWh");

                Device device = deviceRepository.findById(deviceMeasurementDTO.getDevice_id())
                        .orElseThrow(() -> new RuntimeException("Device not found"));

                DeviceMeasurement measurement = DeviceMeasurement.builder()
                        .device(device)
                        .measurement_value(deviceMeasurementDTO.getMeasurement_value())
                        .timestamp(deviceMeasurementDTO.getTimestamp())
                        .build();

                deviceMeasurementRepository.save(measurement);

                if (hourlyConsumption > device.getMaxHourlyConsumption()) {
                    String notification = String.format(
                            "Careful! The device with id %d has exceeded the hourly consumption limit: %.2f kWh (Limit: %.2f kWh)",
                            device.getDeviceId(), hourlyConsumption, device.getMaxHourlyConsumption()
                    );

                    simpMessagingTemplate.convertAndSend("/topic/notifications/" + device.getUser_id(), notification);
                    System.out.println("Notification sent: " + notification);
                }

                if (count >= 6) {
                    HourlyConsumption hourlyRecord = HourlyConsumption.builder()
                            .device(device)
                            .hour(deviceMeasurementDTO.getFirstHour())
                            .date(LocalDate.ofInstant(Instant.ofEpochMilli(deviceMeasurementDTO.getHourStartTimestamp()), ZoneId.systemDefault()))
                            .energyValue(hourlyConsumption)
                            .build();

                    horulyConsumptionRepository.save(hourlyRecord);

                    redisTemplate.opsForValue().set(countKey, "0");
                    redisTemplate.opsForValue().set(consumptionKey, "0.0");

                    System.out.println("Hourly consumption has been reset for device: " + deviceId);
                }
            } finally {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

