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
@Table(name = "DEVICE_MEASUREMENTS")
public class DeviceMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "device_id", nullable = false)
    private Device device;

    @NotNull(message = "Measurement Value cannot be null")
    private double measurement_value;

    @NotNull(message = "Timestamp cannot be null")
    private Long timestamp;
}
