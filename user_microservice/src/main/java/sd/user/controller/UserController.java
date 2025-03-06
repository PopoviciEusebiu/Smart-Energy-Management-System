package sd.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.user.dto.UserDTO;
import sd.user.model.User;
import sd.user.model.UserForCreateDto;
import sd.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
//@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable Integer id){
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAllUsers(){
        List<UserDTO> userDTOList = userService.getAllUsers();
        return ResponseEntity.ok(userDTOList);
    }

    @GetMapping("/withUserRole")
    public ResponseEntity<List<UserDTO>> findAllUsersWithUserRole(){
        List<UserDTO> userDTOList = userService.getAllUserWithUserRole();
        return ResponseEntity.ok(userDTOList);
    }

    @GetMapping("/withAdminRole")
    public ResponseEntity<List<UserDTO>> findAllUsersWithAdminRole(){
        List<UserDTO> userDTOList = userService.getAllUserWithAdminRole();
        return ResponseEntity.ok(userDTOList);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserForCreateDto user) {
        userService.createUser(user);
        return new ResponseEntity<>("User created successfully!", HttpStatus.CREATED);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UserDTO user){
        UserDTO updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User with id " + id + " has been successfully deleted.");
    }

}
