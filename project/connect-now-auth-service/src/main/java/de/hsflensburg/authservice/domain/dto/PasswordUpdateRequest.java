package de.hsflensburg.authservice.domain.dto;

public record PasswordUpdateRequest(String oldPassword, String newPassword) {
}
