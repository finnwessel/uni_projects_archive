package de.hsflensburg.authservice.api;

import de.hsflensburg.authservice.domain.dto.UserRole;
import de.hsflensburg.authservice.domain.exception.MethodUnavailableException;
import de.hsflensburg.authservice.domain.exception.RoleAlreadyExists;
import de.hsflensburg.authservice.domain.model.Role;
import de.hsflensburg.authservice.service.UserService.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Tag(name = "Role")
@RestController
@RequestMapping("role")
@RequiredArgsConstructor
public class RoleController {
    private final IUserService userService;
    @GetMapping("{username}")
    public ResponseEntity<Set<String>> getUserRoles(@PathVariable String username) {
        try {
            Optional<Set<Role>> userRoles = userService.getUserRoles(username);
            if (userRoles.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Set<String> roles = new HashSet<>();
            for (Role role: userRoles.get()) {
                roles.add(role.getAuthority());
            }
            return ResponseEntity.ok().body(roles);
        } catch (MethodUnavailableException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PostMapping("{username}")
    public ResponseEntity<String> addRole(@PathVariable String username, @RequestBody @Valid UserRole userRole) {
        try {
            userService.addUserRole(username, userRole.getRole());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RoleAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{username}")
    public ResponseEntity<String> removeRole(@PathVariable String username, @RequestBody @Valid UserRole userRole) {
        try {
            userService.removeUserRole(username, userRole.getRole());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
