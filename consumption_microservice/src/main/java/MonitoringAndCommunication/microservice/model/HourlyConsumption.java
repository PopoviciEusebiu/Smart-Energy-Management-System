package MonitoringAndCommunication.microservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "HOURLY_CONSUMPTION")
public class HourlyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "device_id", nullable = false)
    private Device device;

    @NotNull(message = "Hour cannot be null")
    private Integer hour;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Energy value cannot be null")
    private double energyValue;
}
