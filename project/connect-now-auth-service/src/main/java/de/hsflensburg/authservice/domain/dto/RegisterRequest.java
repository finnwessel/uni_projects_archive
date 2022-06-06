package de.hsflensburg.authservice.domain.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {
    @NotNull @Email @Size(min = 3, max = 100)
    private String email;
    @NotNull @Size(min = 1, max = 50)
    private String firstName;
    @NotNull @Size(min = 1, max = 50)
    private String lastName;
    @NotNull @Size(min = 1, max = 128)
    private String password;
}
