package sd.user.model;

import lombok.Getter;
import sd.user.dto.UserDTO;

@Getter
public class AuthenticationResponse {
    private final String token;
    private final String message;

    public AuthenticationResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
}