package de.hsflensburg.authservice.api;

import de.hsflensburg.authservice.service.MinioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile")
@RestController
@RequestMapping("avatar/")
@RequiredArgsConstructor
public class AvatarController {
    private final MinioService minioService;
    @GetMapping("presignedUpload")
    public ResponseEntity<Object> uploadAvatar() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String identifier = ((User) authentication.getPrincipal()).getUsername();

        try {
            return ResponseEntity.ok().body(minioService.getPresignedPostFormData(identifier));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("")
    public ResponseEntity<Void> removeAvatar() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String identifier = ((User) authentication.getPrincipal()).getUsername();

        boolean isRemoved = minioService.removeAvatar(identifier);
        if (isRemoved) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
