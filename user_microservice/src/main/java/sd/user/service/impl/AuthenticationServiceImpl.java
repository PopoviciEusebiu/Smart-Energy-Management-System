package sd.user.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sd.user.exceptions.InvalidUserDataException;
import sd.user.exceptions.UserAlreadyExistsException;
import sd.user.mapper.UserMapper;
import sd.user.model.AuthenticationResponse;
import sd.user.model.User;
import sd.user.repository.RoleRepository;
import sd.user.repository.TokenRepository;
import sd.user.repository.UserRepository;
import sd.user.service.AuthenticationService;
import sd.user.model.Token;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;


    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(User request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new InvalidUserDataException("Username cannot be null or empty.");
        }

        if (request.getEmailAddress() == null || !request.getEmailAddress().contains("@")) {
            throw new InvalidUserDataException("Email is invalid.");
        }

        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            throw new InvalidUserDataException("First name cannot be null or empty.");
        }

        if (request.getLastName() == null || request.getLastName().isEmpty()) {
            throw new InvalidUserDataException("Last name cannot be null or empty.");
        }

        if (userRepository.existsUserByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + request.getUsername() + " already exists");
        }
        if (userRepository.existsUserByUsername(request.getEmailAddress())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmailAddress() + " already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .emailAddress(request.getEmailAddress())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(List.of(roleRepository.findByRole("ADMIN")))
                .password(passwordEncoder.encode(request.getPassword()))
                .logged(Boolean.FALSE)
                .build();

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt, "Registration successful.");
    }

    @Override
    public AuthenticationResponse authenticate(User request) {
        log.info("Attempting login for username: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername());

        if(user == null){
            return new AuthenticationResponse(null, "User not found!");
        }

        user.setLogged(Boolean.TRUE);
        String jwt = jwtService.generateToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt, "User login successful");
    }

    private void revokeAllTokenByUser(User user){
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());

        if(validTokens.isEmpty()){
            return;
        }

        validTokens.forEach(t ->{
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwt, User user){
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

}
