package sd.user.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sd.user.dto.UserDTO;
import sd.user.exceptions.InvalidUserDataException;
import sd.user.exceptions.UserAlreadyExistsException;
import sd.user.exceptions.UserNotFoundException;
import sd.user.mapper.UserMapper;
import sd.user.model.AuthenticationResponse;
import sd.user.model.Token;
import sd.user.model.User;
import sd.user.model.UserForCreateDto;
import sd.user.repository.RoleRepository;
import sd.user.repository.TokenRepository;
import sd.user.repository.UserRepository;
import sd.user.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;

    @Override
    public UserDTO getUserById(Integer id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        log.info("User found: {}", user.getUsername());
        return userMapper.entityToDto(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        List<UserDTO> users = userRepository.findAll().stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
        log.info("Found {} users", users.size());
        return users;
    }

    @Override
    public List<UserDTO> getAllUserWithUserRole() {
        log.info("Fetching all users with role user");
        List<UserDTO> users = userRepository.findAll().stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());

        List<UserDTO> userList = new ArrayList<>();
        for(UserDTO u : users){
            if(u.getRoles().stream().anyMatch(role -> role.getRole().equals("USER"))){
                userList.add(u);
            }
        }

        log.info("Found {} users", userList.size());

        return userList;
    }

    @Override
    public List<UserDTO> getAllUserWithAdminRole() {
        log.info("Fetching all users with role admin");
        List<UserDTO> users = userRepository.findAll().stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());

        List<UserDTO> userList = new ArrayList<>();
        for(UserDTO u : users){
            if(u.getRoles().stream().anyMatch(role -> role.getRole().equals("ADMIN"))){
                userList.add(u);
            }
        }

        log.info("Found {} users", userList.size());

        return userList;
    }

    @Override
    public AuthenticationResponse createUser(UserForCreateDto request) {
        log.info("Creating new user with username: {}", request.getUsername());

        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            log.error("Username cannot be null or empty.");
            throw new InvalidUserDataException("Username cannot be null or empty.");
        }

        if (request.getEmailAddress() == null || !request.getEmailAddress().contains("@")) {
            log.error("Invalid email: {}", request.getEmailAddress());
            throw new InvalidUserDataException("Email is invalid.");
        }

        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            log.error("First name cannot be null or empty.");
            throw new InvalidUserDataException("First name cannot be null or empty.");
        }

        if (request.getLastName() == null || request.getLastName().isEmpty()) {
            log.error("Last name cannot be null or empty.");
            throw new InvalidUserDataException("Last name cannot be null or empty.");
        }

        if (userRepository.existsUserByUsername(request.getUsername())) {
            log.error("User with username {} already exists", request.getUsername());
            throw new UserAlreadyExistsException("User with username " + request.getUsername() + " already exists");
        }
        if (userRepository.existsUserByUsername(request.getEmailAddress())) {
            log.error("User with email {} already exists", request.getEmailAddress());
            throw new UserAlreadyExistsException("User with email " + request.getEmailAddress() + " already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .emailAddress(request.getEmailAddress())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(List.of(roleRepository.findByRole("USER")))
                .password(passwordEncoder.encode(request.getPassword()))
                .logged(Boolean.FALSE)
                .build();

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        saveUserToken(jwt, user);

        log.info("User created successfully with id: {}", user.getId());

        RestTemplate restTemplate = new RestTemplate();
        String deviceUrl = "http://localhost:8081/user/addUser/" + user.getId();

        String token = "Bearer " + jwt;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(deviceUrl, HttpMethod.POST, entity, String.class);
            log.info("Device service response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Error calling Device service: {}", e.getMessage());
        }

        log.info("User information sent to Device service for user id: {}", user.getId());

        return new AuthenticationResponse(jwt, "Registration successful.");
    }

    private void saveUserToken(String jwt, User user){
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    @Override
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        log.info("Updating user with id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
            log.error("Username cannot be null or empty.");
            throw new InvalidUserDataException("Username cannot be null or empty.");
        }

        if (userDTO.getEmailAddress() == null || userDTO.getEmailAddress().isEmpty() || !userDTO.getEmailAddress().contains("@")) {
            log.error("Invalid email: {}", userDTO.getEmailAddress());
            throw new InvalidUserDataException("Email cannot be null, empty or invalid.");
        }

        if (userDTO.getFirstName() == null || userDTO.getFirstName().isEmpty()) {
            log.error("First name cannot be null or empty.");
            throw new InvalidUserDataException("First name cannot be null or empty.");
        }

        if (userDTO.getLastName() == null || userDTO.getLastName().isEmpty()) {
            log.error("Last name cannot be null or empty.");
            throw new InvalidUserDataException("Last name cannot be null or empty.");
        }

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmailAddress(userDTO.getEmailAddress());

        User updatedUser = userRepository.save(existingUser);
        log.info("User with id {} has been successfully updated.", id);
        return userMapper.entityToDto(updatedUser);
    }

    @Override
    public void deleteUser(Integer id) {
        log.info("Deleting user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        userRepository.delete(user);
        log.info("User with id: {} has been deleted.", id);

        RestTemplate restTemplate = new RestTemplate();
        //String deviceUrl = "http://device:8081/user/deleteUser/" + user.getId();
        String deviceUrl = "http://localhost:8081/user/deleteUser/" + user.getId();


        try {
            restTemplate.delete(deviceUrl);
            log.info("User devices deleted successfully in Device service for user id: {}", user.getId());
        } catch (Exception e) {
            log.error("Error occurred while trying to delete user devices in Device service for user id: {}. Error: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Failed to delete user devices in Device service");
        }
    }
}
