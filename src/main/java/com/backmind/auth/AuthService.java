package com.backmind.auth;

import com.backmind.auth.dto.SignupRequest;
import com.backmind.auth.dto.SignupResponse;
import com.backmind.auth.dto.LoginRequest;
import com.backmind.auth.dto.LoginResponse;
import com.backmind.auth.dto.CurrentUserResponse;
import com.backmind.user.entity.User;
import com.backmind.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        var user = new User(request.email(), passwordEncoder.encode(request.password()));
        var savedUser = userRepository.saveAndFlush(user);

        return new SignupResponse(savedUser.getId(), savedUser.getEmail());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return new LoginResponse(jwtService.issueToken(user));
    }

    public CurrentUserResponse me(User user) {
        return new CurrentUserResponse(user.getId(), user.getEmail());
    }

    public void logout() {
        // JWT sessions are stateless; clients complete logout by discarding the token.
    }
}
