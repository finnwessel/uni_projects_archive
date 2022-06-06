package de.hsflensburg.authservice.api;

import de.hsflensburg.authservice.domain.dto.UserResponse;
import de.hsflensburg.authservice.domain.dto.UserUpdateRequest;
import de.hsflensburg.authservice.domain.exception.MethodUnavailableException;
import de.hsflensburg.authservice.domain.mapper.UserMapper;
import de.hsflensburg.authservice.domain.model.UserModel;
import de.hsflensburg.authservice.service.UserService.IUserService;
import de.hsflensburg.authservice.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Tag(name = "Profile")
@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
public class ProfileController {
    private final IUserService userService;
    private final NotificationService notificationService;

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username){
        Optional<UserModel> user = userService.findByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(UserMapper.INSTANCE.userToUserResponse(user.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // currently, the same as get user by name but maybe used to expose secret information in the future
    @GetMapping()
    public ResponseEntity<UserResponse> getUser() {
        String username = getAuthenticatedUserName();

        Optional<UserModel> user = userService.findByUsernameOrEmail(username);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(UserMapper.INSTANCE.userToUserResponse(user.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping()
    public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        String username = getAuthenticatedUserName();
        try {
            Optional<UserModel> user = userService.updateUser(username, userUpdateRequest);

            if (user.isPresent()) {
                return ResponseEntity.ok().body(UserMapper.INSTANCE.userToUserResponse(user.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MethodUnavailableException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String getAuthenticatedUserName(){
        User authenticatedUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedUser.getUsername();
    }
}
