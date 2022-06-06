package de.hsflensburg.authservice.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.hsflensburg.authservice.domain.dto.ChoosePasswordRequest;
import de.hsflensburg.authservice.domain.dto.PasswordResetRequest;
import de.hsflensburg.authservice.domain.dto.PasswordUpdateRequest;
import de.hsflensburg.authservice.domain.exception.MethodUnavailableException;
import de.hsflensburg.authservice.domain.model.PasswordReset;
import de.hsflensburg.authservice.domain.model.UserModel;
import de.hsflensburg.authservice.service.ICredentialsResetService;
import de.hsflensburg.authservice.service.UserService.IUserService;
import de.hsflensburg.authservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class CredentialsController {
    private final IUserService userService;
    private final ICredentialsResetService credentialsResetService;
    private final NotificationService notificationService;
    @Value("${passwordReset.ExpirationInMin}")
    private String passwordResetExpiration;
    @Value("${passwordReset.link}")
    private String passwordResetLink;

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePasswordRequest(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {
        String username = getAuthenticatedUserName();
        Optional<UserModel> userModel = userService.findByCredentials(username, passwordUpdateRequest.oldPassword());

        if (userModel.isEmpty()) {
            //Send 200 in order to not reveal information about usernames and emails
            return ResponseEntity.ok().build();
        }

        try {
            userService.updateUserPassword(username, passwordUpdateRequest.newPassword());
        } catch (MethodUnavailableException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPasswordRequest(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {

        try {
            Optional<UserModel> userModel = userService.findByUsernameOrEmail(passwordResetRequest.email());

            if (userModel.isEmpty()) {
                //Send 200 in order to not reveal information about usernames and emails
                return ResponseEntity.ok().build();
            }

            UserModel u = userModel.get();

            LocalDateTime expiration = LocalDateTime.now().plusMinutes(Long.parseLong(passwordResetExpiration));
            PasswordReset passwordReset = credentialsResetService.addUserPasswordResetRequest(u.getUsername(), expiration);

            String resetLink = passwordResetLink.formatted(passwordReset.getResetRequestId());
            notificationService.queuePasswordResetEmail(u.getFirstName(), u.getLastName(), u.getEmail(), u.getUsername(), resetLink);

            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/choose-password")
    public ResponseEntity<String> choosePasswordRequest(@RequestBody @Valid ChoosePasswordRequest choosePasswordRequest) {
        Optional<PasswordReset> passwordResetRequest = credentialsResetService.getUserPasswordResetRequest(choosePasswordRequest.id());

        if (passwordResetRequest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            userService.updateUserPassword(passwordResetRequest.get().getSubject(), choosePasswordRequest.password());
            credentialsResetService.removeUserPasswordResetRequest(choosePasswordRequest.id());
        } catch (MethodUnavailableException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Updating user password is not available");
        }

        return ResponseEntity.ok().build();
    }

    private String getAuthenticatedUserName(){
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedUser.getUsername();
    }
}
