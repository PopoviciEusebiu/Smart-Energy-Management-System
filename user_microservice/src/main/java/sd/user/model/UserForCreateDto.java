package sd.user.model;


import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserForCreateDto {
    private String username;
    private String password;
    private String emailAddress;
    private String lastName;
    private String firstName;
    private List<String> roles;
    private Integer adminId;
}
