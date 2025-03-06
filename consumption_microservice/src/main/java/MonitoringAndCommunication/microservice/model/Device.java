package MonitoringAndCommunication.microservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "device_info")
public class Device {
    @Id
    @Column(name = "device_id", unique = true, nullable = false)
    private Integer deviceId;

    @NotNull(message = "Max Hourly Consumption cannot be null")
    private double maxHourlyConsumption;

    @Column(name = "user_id")
    private Integer user_id;
}
