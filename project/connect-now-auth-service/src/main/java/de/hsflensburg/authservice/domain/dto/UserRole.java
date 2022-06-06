package de.hsflensburg.authservice.domain.dto;

import de.hsflensburg.authservice.domain.model.RoleEnum;
import lombok.Data;

@Data
public class UserRole {
    private RoleEnum role;
}
