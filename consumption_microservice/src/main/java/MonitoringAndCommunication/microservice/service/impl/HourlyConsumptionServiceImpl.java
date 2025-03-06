package MonitoringAndCommunication.microservice.service.impl;

import MonitoringAndCommunication.microservice.dto.HourlyConsumptionDTO;
import MonitoringAndCommunication.microservice.model.HourlyConsumption;
import MonitoringAndCommunication.microservice.repository.HorulyConsumptionRepository;
import MonitoringAndCommunication.microservice.service.HourlyConsumptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HourlyConsumptionServiceImpl implements HourlyConsumptionService {

    private final HorulyConsumptionRepository horulyConsumptionRepository;
    @Override
    public List<HourlyConsumptionDTO> getHourlyConsumptionForOneDay(Integer deviceId, LocalDate date) {
        List<HourlyConsumption> consumptions = horulyConsumptionRepository
                .findByDevice_DeviceIdAndDate(deviceId, date);

        return consumptions.stream()
                .map(consumption -> HourlyConsumptionDTO.builder()
                        .hour(consumption.getHour())
                        .energyValue(consumption.getEnergyValue())
                        .deviceId(deviceId)
                        .build())
                .collect(Collectors.toList());
    }
}
