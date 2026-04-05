package com.otsos.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthDto {
    private Long id;
    private String name;
    private String surname;
    private String jwtToken;
}
