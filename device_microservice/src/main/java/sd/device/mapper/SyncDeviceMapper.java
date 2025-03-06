package sd.device.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sd.device.dto.SyncDeviceDTO;
import sd.device.model.Device;

@RequiredArgsConstructor
@Component
public class SyncDeviceMapper implements BaseMapper<SyncDeviceDTO, Device> {
    @Override
    public SyncDeviceDTO entityToDto(Device device) {
        return SyncDeviceDTO.builder()
                .device_id(device.getId())
                .maxHourlyConsumption(device.getMaxHourlyConsumption())
                .user_id(device.getUser().getUserId())
                .build();
    }

    @Override
    public Device DtoToEntity(SyncDeviceDTO syncDeviceDTO) {
        return null;
    }
}
