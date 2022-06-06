package de.hsflensburg.authservice.api;

import de.hsflensburg.authservice.domain.dto.RefreshTokenRequest;
import de.hsflensburg.authservice.domain.dto.TokenResponse;
import de.hsflensburg.authservice.domain.model.UserModel;
import de.hsflensburg.authservice.service.UserService.IUserService;
import de.hsflensburg.authservice.service.JwtService;
import de.hsflensburg.authservice.service.TokenService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Tag(name = "Token")
@RestController
@RequestMapping("token/")
@RequiredArgsConstructor
public class TokenController {

    private final JwtService jwtService;
    private final TokenService tokenService;
    private final IUserService userService;

    @PostMapping("refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.getRefreshToken();

        Optional<Claims> claims = jwtService.getClaims(token);

        if (claims.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!jwtService.isValidRefreshToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String subject = claims.get().getSubject();

        Optional<UserModel> user = userService.findByUsernameOrEmail(subject);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        tokenService.deleteToken(claims.get().getId());

        String accessToken = jwtService.generateAccessToken(user.get().getAuthorities(), subject);
        String refreshToken = jwtService.generateRefreshToken(claims.get().getSubject());

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .body(tokenResponse);
    }
}
