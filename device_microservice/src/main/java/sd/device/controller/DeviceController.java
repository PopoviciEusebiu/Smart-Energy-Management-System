package sd.device.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.device.dto.DeviceDTO;
import sd.device.service.DeviceService;

import java.util.List;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable Integer id) {
        DeviceDTO deviceDTO = deviceService.getDeviceById(id);
        return ResponseEntity.ok(deviceDTO);
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        List<DeviceDTO> deviceDTOs = deviceService.getAllDevices();
        return ResponseEntity.ok(deviceDTOs);
    }

    @PostMapping("/create")
    public ResponseEntity<DeviceDTO> createDevice(@RequestBody @Valid DeviceDTO deviceDTO) throws JsonProcessingException {
        DeviceDTO createdDevice = deviceService.createDevice(deviceDTO);
        return ResponseEntity.status(201).body(createdDevice);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable Integer id, @RequestBody DeviceDTO deviceDTO) throws JsonProcessingException {
        DeviceDTO updatedDevice = deviceService.updateDevice(id, deviceDTO);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable Integer id) throws JsonProcessingException {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok("The device with id " + id + " has been successfully deleted.");
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<DeviceDTO>> getUserDevices(@PathVariable Integer id){
        List<DeviceDTO> deviceDTOs = deviceService.getDevicesByUserId(id);
        return ResponseEntity.ok(deviceDTOs);
    }
}
