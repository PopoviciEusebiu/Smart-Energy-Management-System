package MonitoringAndCommunication.microservice.mapper;

import MonitoringAndCommunication.microservice.dto.DeviceDTO;
import MonitoringAndCommunication.microservice.model.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeviceMapper implements BaseMapper<DeviceDTO, Device> {
    @Override
    public DeviceDTO entityToDto(Device device) {
        return DeviceDTO.builder()
                .device_id(device.getDeviceId())
                .maxHourlyConsumption(device.getMaxHourlyConsumption())
                .user_id(device.getUser_id())
                .build();
    }

    @Override
    public Device DtoToEntity(DeviceDTO deviceDTO) {
        return Device.builder()
                .user_id(deviceDTO.getUser_id())
                .maxHourlyConsumption(deviceDTO.getMaxHourlyConsumption())
                .deviceId(deviceDTO.getDevice_id())
                .build();
    }
}
