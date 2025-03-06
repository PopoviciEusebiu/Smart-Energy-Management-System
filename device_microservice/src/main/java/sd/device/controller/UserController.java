package sd.device.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.device.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/addUser/{userId}")
    public ResponseEntity<String> addUser(@PathVariable Integer userId){
        userService.addUserId(userId);
        return ResponseEntity.ok("User Id added for new user created with id: " + userId);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) throws JsonProcessingException {
        userService.deleteUserId(userId);
        return ResponseEntity.ok("The user with id: " + userId + " and all their devices were deleted");
    }
}
