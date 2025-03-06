package sd.user.service;

import org.springframework.stereotype.Component;
import sd.user.dto.AuthDTO;
import sd.user.dto.UserDTO;
import sd.user.model.AuthenticationResponse;
import sd.user.model.User;

@Component
public interface AuthenticationService {

    AuthenticationResponse register(User request);

    AuthenticationResponse authenticate(User request);
}
