package com.otsos.userservice.controllers;

import com.otsos.userservice.dto.AuthDto;
import com.otsos.userservice.dto.ChangePasswordDto;
import com.otsos.userservice.dto.UserDto;
import com.otsos.userservice.exceptions.UserNotFoundException;
import com.otsos.userservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto userDto = userService.getUser(id);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        AuthDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<AuthDto> updatePassword(@PathVariable Long id, @RequestBody ChangePasswordDto dto) {
        AuthDto updatedUser = userService.updatePassword(id, dto.getOldPassword(), dto.getNewPassword());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
