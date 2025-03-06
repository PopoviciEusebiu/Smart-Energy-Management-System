package sd.device.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import sd.device.dto.SyncDeviceDTO;
import sd.device.exceptions.UserNotFoundException;
import sd.device.mapper.SyncDeviceMapper;
import sd.device.model.Device;
import sd.device.repository.DeviceRepository;
import sd.device.repository.UserRepository;
import sd.device.service.UserService;
import sd.device.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final SyncDeviceMapper syncDeviceMapper;

    @Override
    public void addUserId(Integer id) {
        log.info("Adding user with id: {}", id);
        User user = User.builder().userId(id).build();
        userRepository.save(user);
        log.info("User with id: {} has been successfully added.", id);
    }

    @Override
    public void deleteUserId(Integer id) throws JsonProcessingException {
        log.info("Deleting user with id: {}", id);

        User user = userRepository.findUserByUserId(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        Device device = deviceRepository.findDeviceByUser(user);
        if (device != null) {
            SyncDeviceDTO syncDeviceDto = new SyncDeviceDTO();
            syncDeviceDto.setAction("delete");
            syncDeviceDto.setDevice_id(device.getId());
            publishDeviceUpdate(syncDeviceDto);
        } else {
            log.warn("No devices found for user with id: {}", id);
        }

        log.info("Deleting devices associated with user id: {}", id);
        deviceRepository.deleteDevicesByUser(user);
        userRepository.deleteUserByUserId(id);
        log.info("User with id: {} and their devices have been successfully deleted.", id);
    }


    private void publishDeviceUpdate(SyncDeviceDTO device) throws JsonProcessingException {
        if (device != null) {
            String message = objectMapper.writeValueAsString(device);

            rabbitTemplate.convertAndSend("device.update.delete", message);

            System.out.println("Mesaj trimis în RabbitMQ: " + message);
        }
    }
}
