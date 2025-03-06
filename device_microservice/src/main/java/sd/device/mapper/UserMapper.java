package sd.device.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sd.device.dto.DeviceDTO;
import sd.device.dto.UserDTO;
import sd.device.model.Device;
import sd.device.model.User;

@RequiredArgsConstructor
@Component
public class UserMapper implements BaseMapper<UserDTO, User>{
    @Override
    public UserDTO entityToDto(User user) {
        return UserDTO.builder().userId(user.getUserId()).build();
    }

    @Override
    public User DtoToEntity(UserDTO userDTO) {
        return User.builder().userId(userDTO.getUserId()).build();
    }
}
