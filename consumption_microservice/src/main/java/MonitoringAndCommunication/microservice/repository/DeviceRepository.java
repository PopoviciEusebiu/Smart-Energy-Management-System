package MonitoringAndCommunication.microservice.repository;

import MonitoringAndCommunication.microservice.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    void deleteDeviceByDeviceId(Integer id);
}
