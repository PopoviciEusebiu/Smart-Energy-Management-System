package sd.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncDeviceDTO {

    private Integer device_id;

    private Double maxHourlyConsumption;

    private Integer user_id;

    private String action;


}
