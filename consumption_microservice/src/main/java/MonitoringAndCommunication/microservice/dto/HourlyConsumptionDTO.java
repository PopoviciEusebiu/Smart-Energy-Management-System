package MonitoringAndCommunication.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.sql.In;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HourlyConsumptionDTO {

    private Integer hour;

    private double energyValue;

    private Integer deviceId;

}
