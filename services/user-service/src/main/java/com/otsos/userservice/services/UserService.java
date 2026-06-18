package com.otsos.userservice.services;

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
        Optional<User> user = userRepository.findById(id);
        return user.map(this::returnUserDto).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto user) {
        User userToUpdate = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));

        userToUpdate.fullUpdate(user);
        userRepository.save(userToUpdate);

        return returnUserDto(userToUpdate);
    }

    @Transactional
    public UserDto updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IncorrectPasswordException(id.toString());
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return returnUserDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
        userRepository.delete(user);
    }

    private UserDto returnUserDto(User user) {
        UserDto userDto = new UserDto(user.getEmail(), user.getName(), user.getSurname());
        userDto.setJwtToken(jwtTokenProvider.generateToken(user));
        return userDto;
    }
}
