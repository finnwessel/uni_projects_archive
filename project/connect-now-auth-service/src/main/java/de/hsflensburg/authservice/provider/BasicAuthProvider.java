package de.hsflensburg.authservice.provider;

import de.hsflensburg.authservice.domain.model.UserModel;
import de.hsflensburg.authservice.service.UserService.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
@ConditionalOnProperty(name="auth.service.authentication-method", havingValue="basic")
@Qualifier("authProvider")
@Component
public class BasicAuthProvider implements AuthenticationProvider {

    private IUserService userService;

    @Autowired
    public void setUserService (@Qualifier("basicUserService") IUserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Retrieve user with given credentials
        Optional<UserModel> user = userService.findByCredentials(name, password);
        // Return correct user or otherwise return null
        return user.map(u -> new UsernamePasswordAuthenticationToken(u, password, u.getAuthorities())).orElse(null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
