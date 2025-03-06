package sd.device.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDTO {
    private Integer id;

    private Integer userId;

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Address cannot be null")
    private String address;

    @NotNull(message = "Maximum hourly consumption cannot be null")
    private Double maxHourlyConsumption;
}
