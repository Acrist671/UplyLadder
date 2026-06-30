package com.otsos.userservice.services;

import com.otsos.userservice.dto.AuthDto;
import com.otsos.userservice.dto.LoginDto;
import com.otsos.userservice.dto.RegisterDto;
import com.otsos.userservice.dto.User;
import com.otsos.userservice.exceptions.ApiException;
import com.otsos.userservice.exceptions.IncorrectPasswordException;
import com.otsos.userservice.exceptions.UserAlreadyExistsException;
import com.otsos.userservice.exceptions.UserNotFoundException;
import com.otsos.userservice.repositories.UserRepository;
import com.otsos.userservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthDto registerUser(RegisterDto registerDto) throws ApiException {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(registerDto.getEmail());
        }
        else{
            registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            User user = new User();
            user.createByRegisterDto(registerDto);

            user = userRepository.save(user);
            String token = jwtTokenProvider.generateToken(user);

            return returnAuthDto(user, token);
        }
    }

    @Transactional(readOnly = true)
    public AuthDto loginUser(LoginDto loginDto) throws ApiException {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(loginDto.getEmail());
        }
        else if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException(user.getId().toString());
        }
        return returnAuthDto(user, jwtTokenProvider.generateToken(user));
    }

    private AuthDto returnAuthDto(User user, String token) {
        return new AuthDto(user.getId(), user.getName(), user.getSurname(), token);
    }
}
