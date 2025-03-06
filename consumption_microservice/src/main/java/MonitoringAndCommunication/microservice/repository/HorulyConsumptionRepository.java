package MonitoringAndCommunication.microservice.repository;

import MonitoringAndCommunication.microservice.model.Device;
import MonitoringAndCommunication.microservice.model.HourlyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HorulyConsumptionRepository extends JpaRepository<HourlyConsumption,Integer> {

    List<HourlyConsumption> findByDevice_DeviceIdAndDate(Integer deviceId, LocalDate date);

    void deleteAllByDevice(Device device);
}
