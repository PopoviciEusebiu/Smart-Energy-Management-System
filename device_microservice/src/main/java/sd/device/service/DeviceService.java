package sd.device.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;
import sd.device.dto.DeviceDTO;
import sd.device.model.Device;

import java.util.List;

@Component
public interface DeviceService {

    DeviceDTO getDeviceById(Integer id);

    List<DeviceDTO> getAllDevices();

    DeviceDTO createDevice(DeviceDTO deviceDTO) throws JsonProcessingException;

    DeviceDTO updateDevice(Integer id, DeviceDTO deviceDTO) throws JsonProcessingException;

    void deleteDevice(Integer id) throws JsonProcessingException;

    List<DeviceDTO> getDevicesByUserId(Integer userId);

}
