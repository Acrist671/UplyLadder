package com.otsos.userservice.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user", schema = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @NotNull
    private String email;

    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Column(name = "surname", nullable = false)
    @NotNull
    private String surname;

    @Column(name = "password", nullable = false)
    @NotNull
    private String password;

    public void createByRegisterDto(RegisterDto registerDto) {
        this.email = registerDto.getEmail();
        this.name = registerDto.getName();
        this.surname = registerDto.getSurname();
        this.password = registerDto.getPassword();
    }

    public void fullUpdate(UserDto user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.surname = user.getSurname();
    }
}
