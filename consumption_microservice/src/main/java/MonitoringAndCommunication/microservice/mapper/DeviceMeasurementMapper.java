package MonitoringAndCommunication.microservice.mapper;

import MonitoringAndCommunication.microservice.dto.DeviceMeasurementDTO;
import MonitoringAndCommunication.microservice.model.Device;
import MonitoringAndCommunication.microservice.model.DeviceMeasurement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeviceMeasurementMapper implements BaseMapper<DeviceMeasurementDTO, DeviceMeasurement> {
    @Override
    public DeviceMeasurementDTO entityToDto(DeviceMeasurement deviceMeasurement) {
        return DeviceMeasurementDTO.builder()
                .measurement_value(deviceMeasurement.getMeasurement_value())
                .timestamp(deviceMeasurement.getTimestamp())
                .device_id(deviceMeasurement.getDevice().getDeviceId())
                .build();
    }

    @Override
    public DeviceMeasurement DtoToEntity(DeviceMeasurementDTO deviceMeasurementDTO) {
        return DeviceMeasurement.builder()
                .measurement_value(deviceMeasurementDTO.getMeasurement_value())
                .timestamp(deviceMeasurementDTO.getTimestamp())
                .device(Device.builder().deviceId(deviceMeasurementDTO.getDevice_id()).build())
                .build();
    }
}
