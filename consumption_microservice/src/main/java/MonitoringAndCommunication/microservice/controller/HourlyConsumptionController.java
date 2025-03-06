package MonitoringAndCommunication.microservice.controller;

import MonitoringAndCommunication.microservice.dto.HourlyConsumptionDTO;
import MonitoringAndCommunication.microservice.service.HourlyConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/consumption")
@RequiredArgsConstructor
public class HourlyConsumptionController {

    private final HourlyConsumptionService hourlyConsumptionService;

    @GetMapping("/historical/{deviceId}")
    public ResponseEntity<List<HourlyConsumptionDTO>> getHistoricalConsumption(
            @PathVariable Integer deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<HourlyConsumptionDTO> consumptions = hourlyConsumptionService.getHourlyConsumptionForOneDay(deviceId,date);
        return ResponseEntity.ok(consumptions);
    }
}
