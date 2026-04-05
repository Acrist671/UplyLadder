package com.otsos.userservice.services;

import com.otsos.userservice.dto.User;
import com.otsos.userservice.dto.UserDto;
import com.otsos.userservice.exceptions.IncorrectPasswordException;
import com.otsos.userservice.exceptions.UserNotFoundException;
import com.otsos.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        return user.map(this::returnUserDto).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public UserDto updateUser(Long id, UserDto user) {
        User userToUpdate = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
        userToUpdate.fullUpdate(user);
        userRepository.save(userToUpdate);
        return returnUserDto(userToUpdate);
    }

    public UserDto updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
        if (!passwordEncoder.matches(passwordEncoder.encode(newPassword), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return returnUserDto(user);
        }
        else{
            throw new IncorrectPasswordException(id.toString());
        }
    }

    public UserDto deleteUser(Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(id.toString()));
        userRepository.delete(user);
        return returnUserDto(user);
    }

    private UserDto returnUserDto(User user) {
        return new UserDto(user.getEmail(), user.getName(), user.getSurname());
    }
}
