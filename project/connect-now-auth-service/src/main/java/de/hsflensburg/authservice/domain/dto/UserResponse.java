package de.hsflensburg.authservice.domain.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
