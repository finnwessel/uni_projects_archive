package de.hsflensburg.authservice.api;

import de.hsflensburg.authservice.domain.dto.*;
import de.hsflensburg.authservice.domain.exception.MethodUnavailableException;
import de.hsflensburg.authservice.domain.mapper.UserMapper;
import de.hsflensburg.authservice.service.UserService.IUserService;
import de.hsflensburg.authservice.service.JwtService;
import de.hsflensburg.authservice.service.TokenService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@Tag(name = "Authentication")
@RestController
@RequestMapping("auth/")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final Environment environment;

    // Login endpoint with TokenResponse (AccessToken and RefreshToken)
    @PostMapping("login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (authentication.isAuthenticated()) {
                TokenResponse tokenResponse = new TokenResponse();
                tokenResponse.setAccessToken(jwtService.generateAccessToken(authentication.getAuthorities(), userDetails.getUsername()));
                tokenResponse.setRefreshToken(jwtService.generateRefreshToken(userDetails.getUsername()));

                return ResponseEntity.ok()
                        .body(tokenResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Registration Endpoint
    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest registerRequest) {
        try {
            UserResponse response = UserMapper.INSTANCE.userToUserResponse(userService.createUser(registerRequest));
            return ResponseEntity.created(URI.create("/profile/" + response.getUsername())).build();
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (MethodUnavailableException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Logout Endpoint
    @PostMapping("logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.getRefreshToken();

        Optional<Claims> claims = jwtService.getClaims(token);

        if (claims.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!jwtService.isValidRefreshToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        tokenService.deleteToken(claims.get().getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("register-enabled")
    public ResponseEntity<Boolean> getRegistrationEnabled(){
        for (String profile: environment.getActiveProfiles()) {
            if (profile.equals("basic")) {
                return ResponseEntity.ok().body(true);
            }
        }
        return ResponseEntity.ok().body(false);
    }
}
