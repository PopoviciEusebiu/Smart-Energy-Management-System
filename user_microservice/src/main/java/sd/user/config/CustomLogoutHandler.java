package sd.user.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import sd.user.model.Token;
import sd.user.repository.TokenRepository;
import sd.user.repository.UserRepository;
import sd.user.model.User;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            Token storedToken = tokenRepository.findByToken(token).orElse(null);

            if(storedToken!=null){
                storedToken.setLoggedOut(true);
                tokenRepository.save(storedToken);

                Optional<User> user = userRepository.findById(storedToken.getUser().getId());

                if(user.isPresent()){
                    user.get().setLogged(Boolean.FALSE);
                    userRepository.save(user.get());
                }

            }
        }
    }
}
