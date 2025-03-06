package sd.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id;

    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be valid!")
    private String emailAddress;

    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @NotNull(message = "First name cannot be null")
    private String firstName;

    private List<RoleDTO> roles;
}
