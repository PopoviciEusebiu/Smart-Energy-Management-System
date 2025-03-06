package MonitoringAndCommunication.microservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceMeasurementDTO {

    @NotNull(message = "Measurement Value cannot be null")
    private Double measurement_value;

    @NotNull(message = "Timestamp cannot be null")
    private Long timestamp;

    private Integer device_id;

    private long hourStartTimestamp;

    private int firstHour;
}
