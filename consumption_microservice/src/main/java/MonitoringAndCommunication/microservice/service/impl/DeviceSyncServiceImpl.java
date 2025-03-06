package MonitoringAndCommunication.microservice.service.impl;

import MonitoringAndCommunication.microservice.dto.SyncDeviceDTO;
import MonitoringAndCommunication.microservice.model.Device;
import MonitoringAndCommunication.microservice.repository.DeviceMeasurementRepository;
import MonitoringAndCommunication.microservice.repository.DeviceRepository;
import MonitoringAndCommunication.microservice.repository.HorulyConsumptionRepository;
import MonitoringAndCommunication.microservice.service.DeviceSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeviceSyncServiceImpl implements DeviceSyncService {

    private final ObjectMapper objectMapper;
    private final DeviceRepository deviceRepository;
    private final DeviceMeasurementRepository deviceMeasurementRepository;
    private final HorulyConsumptionRepository horulyConsumptionRepository;
    @Override
    public void receiveDeviceUpdate(String message) {
        try {
            SyncDeviceDTO syncDeviceDto = objectMapper.readValue(message, SyncDeviceDTO.class);

            String action = syncDeviceDto.getAction();
            if ("delete".equals(action)) {
                handleDeviceDeletion(syncDeviceDto.getDevice_id());
            } else if ("create".equals(action) || "update".equals(action)) {
                handleDeviceCreateOrUpdate(syncDeviceDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeviceDeletion(Integer deviceId) {
        log.info("Handling device deletion for device_id: {}", deviceId);

        Optional<Device> device = deviceRepository.findById(deviceId);

        deviceMeasurementRepository.deleteAllByDevice(device.get());
        horulyConsumptionRepository.deleteAllByDevice(device.get());

        deviceRepository.deleteDeviceByDeviceId(deviceId);


        log.info("Device and associated measurements deleted successfully for device_id: {}", deviceId);
    }

    private void handleDeviceCreateOrUpdate(SyncDeviceDTO syncDeviceDto) {
        log.info("Handling device create/update: {}", syncDeviceDto);

        Device device = Device.builder()
                .deviceId(syncDeviceDto.getDevice_id())
                .user_id(syncDeviceDto.getUser_id())
                .maxHourlyConsumption(syncDeviceDto.getMaxHourlyConsumption())
                .build();

        deviceRepository.save(device);
        log.info("Device saved/updated successfully: {}", device);
    }
}
