package sd.user.service;

import org.springframework.stereotype.Component;
import sd.user.dto.UserDTO;
import sd.user.model.AuthenticationResponse;
import sd.user.model.User;
import sd.user.model.UserForCreateDto;

import java.util.List;


@Component
public interface UserService {

    UserDTO getUserById(Integer id);
    List<UserDTO> getAllUsers();
    List<UserDTO> getAllUserWithUserRole();
    AuthenticationResponse createUser(UserForCreateDto request);

    UserDTO updateUser(Integer id, UserDTO userDTO);

    void deleteUser(Integer id);
    List<UserDTO> getAllUserWithAdminRole();



}
