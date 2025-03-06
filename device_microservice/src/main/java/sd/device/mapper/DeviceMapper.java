package sd.device.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sd.device.dto.DeviceDTO;
import sd.device.model.Device;
import sd.device.model.User;

@RequiredArgsConstructor
@Component
public class DeviceMapper implements BaseMapper<DeviceDTO, Device>{
    @Override
    public DeviceDTO entityToDto(Device device) {
        return DeviceDTO.builder()
                .id(device.getId())
                .userId(device.getUser().getUserId())
                .description(device.getDescription())
                .address(device.getAddress())
                .maxHourlyConsumption(device.getMaxHourlyConsumption())
                .build();
    }

    @Override
    public Device DtoToEntity(DeviceDTO deviceDTO) {
        return Device.builder()
                .user(User.builder().userId(deviceDTO.getUserId()).build())
                .description(deviceDTO.getDescription())
                .address(deviceDTO.getAddress())
                .maxHourlyConsumption(deviceDTO.getMaxHourlyConsumption())
                .build();

    }
}
