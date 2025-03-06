package MonitoringAndCommunication.microservice.service;

import MonitoringAndCommunication.microservice.dto.HourlyConsumptionDTO;
import MonitoringAndCommunication.microservice.model.HourlyConsumption;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public interface HourlyConsumptionService {

    List<HourlyConsumptionDTO> getHourlyConsumptionForOneDay(Integer deviceId, LocalDate date);

}
