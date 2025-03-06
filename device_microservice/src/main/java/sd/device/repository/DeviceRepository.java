package sd.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sd.device.dto.UserDTO;
import sd.device.model.Device;
import sd.device.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device,Integer> {

    List<Device> findAllDevicesByUser(User user);

    void deleteDevicesByUser(User user);

    Device findDeviceByUser(User user);

}
