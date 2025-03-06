package MonitoringAndCommunication.microservice.repository;

import MonitoringAndCommunication.microservice.model.Device;
import MonitoringAndCommunication.microservice.model.DeviceMeasurement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceMeasurementRepository extends JpaRepository<DeviceMeasurement,Integer> {

    void deleteAllByDevice(Device device);

    @Query("SELECT SUM(dm.measurement_value) " +
            "FROM DeviceMeasurement dm " +
            "WHERE dm.device.deviceId = :deviceId " +
            "AND dm.timestamp >= :startTime")
    double calculateHourlyConsumption(@Param("deviceId") Integer deviceId, @Param("startTime") long startTime);
}
