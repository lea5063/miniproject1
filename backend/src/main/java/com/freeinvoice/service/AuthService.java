package com.freeinvoice.service;

import com.freeinvoice.domain.Client;
import com.freeinvoice.domain.Role;
import com.freeinvoice.domain.User;
import com.freeinvoice.dto.AuthDtos.LoginRequest;
import com.freeinvoice.dto.AuthDtos.LoginResponse;
import com.freeinvoice.dto.AuthDtos.SignupRequest;
import com.freeinvoice.dto.AuthDtos.UserResponse;
import com.freeinvoice.exception.ApiException;
import com.freeinvoice.repository.ClientRepository;
import com.freeinvoice.repository.UserRepository;
import com.freeinvoice.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, ClientRepository clientRepository,
                        PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        }
        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name(),
                request.role(),
                request.companyName()
        );
        userRepository.save(user);

        // CLIENT로 가입 시, 프리랜서가 미리 등록해둔 거래처(clients)와 이메일로 자동 연결
        if (request.role() == Role.CLIENT) {
            List<Client> pending = clientRepository.findByEmail(request.email());
            pending.forEach(c -> c.setClientUser(user));
        }

        return UserResponse.from(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new LoginResponse(token, UserResponse.from(user));
    }
}
