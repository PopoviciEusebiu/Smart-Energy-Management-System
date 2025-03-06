package sd.user.mapper;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sd.user.model.User;
import sd.user.dto.UserDTO;

@RequiredArgsConstructor
@Component
public class UserMapper implements BaseMapper<UserDTO, User>{
    private final RoleMapper roleMapper;
    @Override
    public UserDTO entityToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .emailAddress(user.getEmailAddress())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roleMapper.roleListEntityToDto(user.getRoles()))
                .build();
    }

    @Override
    public User DtoToEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .emailAddress(userDTO.getEmailAddress())
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .roles(roleMapper.roleListDtoToEntity(userDTO.getRoles()))
                .build();

    }
}
