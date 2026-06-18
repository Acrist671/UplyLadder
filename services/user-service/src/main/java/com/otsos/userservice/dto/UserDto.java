package com.otsos.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private String email;
    private String name;
    private String surname;
    private String jwtToken;

    public UserDto(String email, String name, String surname){
        this.email = email;
        this.name = name;
        this.surname = surname;
    }
}
