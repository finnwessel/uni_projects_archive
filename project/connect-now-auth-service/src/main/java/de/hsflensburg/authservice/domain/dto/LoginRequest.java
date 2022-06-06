package de.hsflensburg.authservice.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull
    private String identifier;
    @NotNull
    private String password;
}
