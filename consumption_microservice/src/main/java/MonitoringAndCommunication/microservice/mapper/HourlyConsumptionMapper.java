package MonitoringAndCommunication.microservice.mapper;

import MonitoringAndCommunication.microservice.dto.HourlyConsumptionDTO;
import MonitoringAndCommunication.microservice.model.Device;
import MonitoringAndCommunication.microservice.model.HourlyConsumption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HourlyConsumptionMapper implements BaseMapper<HourlyConsumptionDTO, HourlyConsumption> {
    @Override
    public HourlyConsumptionDTO entityToDto(HourlyConsumption hourlyConsumption) {
        return HourlyConsumptionDTO.builder()
                .hour(hourlyConsumption.getHour())
                .energyValue(hourlyConsumption.getEnergyValue())
                .deviceId(hourlyConsumption.getDevice().getDeviceId())
                .build();
    }

    @Override
    public HourlyConsumption DtoToEntity(HourlyConsumptionDTO hourlyConsumptionDTO) {
        return HourlyConsumption.builder()
                .hour(hourlyConsumptionDTO.getHour())
                .energyValue(hourlyConsumptionDTO.getEnergyValue())
                .device(Device.builder().deviceId(hourlyConsumptionDTO.getDeviceId()).build())
                .build();
    }
}
