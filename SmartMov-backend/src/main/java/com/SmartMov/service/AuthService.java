package com.SmartMov.service;


import com.SmartMov.dto.LoginRequest;
import com.SmartMov.dto.LoginResponse;
import com.SmartMov.dto.RegisterRequest;
import com.SmartMov.dto.UserResponse;
import com.SmartMov.entity.User;
import com.SmartMov.exception.BusinessException;
import com.SmartMov.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
}

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    public LoginResponse login(LoginRequest request) {

    User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() ->
                    new BusinessException("Invalid username or password"));

    if (!passwordEncoder.matches(
            request.getPassword(),
            user.getPassword())) {

        throw new BusinessException("Invalid username or password");
    }

    String token = jwtService.generateToken(
            user.getUsername()
    );

    return new LoginResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getEmail()
    );
}
}