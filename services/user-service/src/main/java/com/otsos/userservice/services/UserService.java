package com.otsos.userservice.services;

import com.otsos.userservice.dto.AuthDto;
import com.otsos.userservice.dto.User;
import com.otsos.userservice.dto.UserDto;
import com.otsos.userservice.exceptions.IncorrectPasswordException;
import com.otsos.userservice.exceptions.UserNotFoundException;
import com.otsos.userservice.repositories.UserRepository;
import com.otsos.userservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public UserDto getUser(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));

        return new UserDto(user.getEmail(),
                user.getName(), user.getSurname());
    }

    @Transactional
    public AuthDto updateUser(Long id, UserDto user) {
        User userToUpdate = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));

        userToUpdate.fullUpdate(user);
        userRepository.save(userToUpdate);

        return new AuthDto(userToUpdate.getId(), userToUpdate.getName(),
                userToUpdate.getSurname(), jwtTokenProvider.generateToken(userToUpdate));
    }

    @Transactional
    public AuthDto updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IncorrectPasswordException(id.toString());
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new AuthDto(user.getId(), user.getName(),
                user.getSurname(), jwtTokenProvider.generateToken(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
        userRepository.delete(user);
    }
}
