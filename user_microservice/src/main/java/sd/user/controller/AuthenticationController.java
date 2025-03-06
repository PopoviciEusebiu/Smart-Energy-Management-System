package sd.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.user.config.CustomLogoutHandler;
import sd.user.model.AuthenticationResponse;
import sd.user.model.User;
import sd.user.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
//@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CustomLogoutHandler customLogoutHandler;

    public AuthenticationController(AuthenticationService authenticationService, CustomLogoutHandler customLogoutHandler) {
        this.authenticationService = authenticationService;
        this.customLogoutHandler = customLogoutHandler;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody User request) {
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request) {
        AuthenticationResponse response = authenticationService.authenticate(request);

        if(response.getToken() == null){
            if(response.getMessage().equals("User not found")){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        customLogoutHandler.logout(request, null, null);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

