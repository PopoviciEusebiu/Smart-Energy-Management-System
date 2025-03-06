package sd.device.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import sd.device.dto.DeviceDTO;
import sd.device.dto.SyncDeviceDTO;
import sd.device.exceptions.DeviceNotFoundException;
import sd.device.exceptions.InvalidDeviceDataException;
import sd.device.exceptions.UserNotFoundException;
import sd.device.mapper.DeviceMapper;
import sd.device.mapper.SyncDeviceMapper;
import sd.device.mapper.UserMapper;
import sd.device.model.Device;
import sd.device.model.User;
import sd.device.repository.DeviceRepository;
import sd.device.repository.UserRepository;
import sd.device.service.DeviceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeviceServiceImpl implements DeviceService {


    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final SyncDeviceMapper syncDeviceMapper;


    @Override
    public DeviceDTO getDeviceById(Integer id) {
        log.info("Fetching device with id: {}", id);
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", id);
                    return new DeviceNotFoundException("Device not found with id: " + id);
                });
        log.info("Device found: {}", device.getDescription());
        return deviceMapper.entityToDto(device);
    }

    @Override
    public List<DeviceDTO> getAllDevices() {
        log.info("Fetching all devices");
        List<DeviceDTO> devices = deviceRepository.findAll().stream()
                .map(deviceMapper::entityToDto)
                .collect(Collectors.toList());
        log.info("Found {} devices", devices.size());
        return devices;
    }

    @Override
    public DeviceDTO createDevice(DeviceDTO deviceDTO) throws JsonProcessingException {
        log.info("Creating new device for user id: {}", deviceDTO.getUserId());

        User user = userRepository.findUserByUserId(deviceDTO.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", deviceDTO.getUserId());
                    return new UserNotFoundException("User not found with id: " + deviceDTO.getUserId());
                });

        Device device = Device.builder()
                .description(deviceDTO.getDescription())
                .address(deviceDTO.getAddress())
                .maxHourlyConsumption(deviceDTO.getMaxHourlyConsumption())
                .user(user)
                .build();

        Device savedDevice = deviceRepository.save(device);
        log.info("Device created successfully with id: {}", savedDevice.getId());

        SyncDeviceDTO syncDeviceDTO = syncDeviceMapper.entityToDto(savedDevice);
        syncDeviceDTO.setAction("create");


        publishDeviceUpdate(syncDeviceDTO, "device.update.add");

        return deviceMapper.entityToDto(savedDevice);
    }

    @Override
    public DeviceDTO updateDevice(Integer id, DeviceDTO deviceDTO) throws JsonProcessingException {
        log.info("Updating device with id: {}", id);

        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", id);
                    return new DeviceNotFoundException("Device not found with id: " + id);
                });

        validateDeviceData(deviceDTO);

        Optional<User> user = userRepository.findUserByUserId(deviceDTO.getUserId());


        existingDevice.setDescription(deviceDTO.getDescription());
        existingDevice.setAddress(deviceDTO.getAddress());
        existingDevice.setMaxHourlyConsumption(deviceDTO.getMaxHourlyConsumption());
        existingDevice.setUser(user.get());

        Device updatedDevice = deviceRepository.save(existingDevice);
        log.info("Device updated successfully with id: {}", updatedDevice.getId());


        SyncDeviceDTO syncDeviceDTO = syncDeviceMapper.entityToDto(updatedDevice);
        syncDeviceDTO.setAction("update");

        publishDeviceUpdate(syncDeviceDTO, "device.update.update");
        return deviceMapper.entityToDto(updatedDevice);
    }

    @Override
    public void deleteDevice(Integer id) throws JsonProcessingException {
        log.info("Deleting device with id: {}", id);

        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", id);
                    return new DeviceNotFoundException("Device not found with id: " + id);
                });

        log.info("Device deleted successfully with id: {}", id);

        SyncDeviceDTO syncDeviceDto = new SyncDeviceDTO();
        syncDeviceDto.setAction("delete");
        syncDeviceDto.setDevice_id(id);
        publishDeviceUpdate(syncDeviceDto, "device.update.delete");

        deviceRepository.delete(existingDevice);
    }

    @Override
    public List<DeviceDTO> getDevicesByUserId(Integer userId) {
        log.info("Fetching devices for user with id: {}", userId);

        User user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new UserNotFoundException("User not found with id: " + userId);
                });

        List<Device> devices = deviceRepository.findAllDevicesByUser(user);

        if (devices.isEmpty()) {
            log.warn("No devices found for user with id: {}", userId);
            throw new DeviceNotFoundException("No devices found for user with id: " + userId);
        }

        log.info("Found {} devices for user with id: {}", devices.size(), userId);
        return devices.stream()
                .map(deviceMapper::entityToDto)
                .collect(Collectors.toList());
    }


    private void validateDeviceData(DeviceDTO deviceDTO) {
        if (deviceDTO.getDescription() == null || deviceDTO.getDescription().isEmpty()) {
            log.error("Description cannot be null or empty.");
            throw new InvalidDeviceDataException("Description cannot be null or empty.");
        }

        if (deviceDTO.getAddress() == null || deviceDTO.getAddress().isEmpty()) {
            log.error("Address cannot be null or empty.");
            throw new InvalidDeviceDataException("Address cannot be null or empty.");
        }

        if (deviceDTO.getMaxHourlyConsumption() == null || deviceDTO.getMaxHourlyConsumption() <= 0) {
            log.error("Maximum hourly consumption cannot be null or zero.");
            throw new InvalidDeviceDataException("Maximum hourly consumption cannot be null or zero.");
        }
    }

    private void publishDeviceUpdate(SyncDeviceDTO device, String routingKey) throws JsonProcessingException {
        if (device != null) {
            String message = objectMapper.writeValueAsString(device);

            rabbitTemplate.convertAndSend(routingKey, message);

            System.out.println("Mesaj trimis în RabbitMQ: " + message);
        }
    }
}
