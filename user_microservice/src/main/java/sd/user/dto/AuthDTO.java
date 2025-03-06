package sd.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sd.user.model.Role;

@Getter
@Setter
@Data
public class AuthDTO {

    private String username;
    private String password;

    private Role role;
}
