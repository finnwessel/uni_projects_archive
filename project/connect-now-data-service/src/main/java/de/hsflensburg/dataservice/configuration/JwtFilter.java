package de.hsflensburg.dataservice.configuration;

import de.hsflensburg.dataservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.util.StringUtils.hasLength;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Retrieve authorization header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // Check if header is correct bearer token header, otherwise continue with next filter
        if (!hasLength(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Retrieve jwt from header
        final String token = authHeader.split(" ")[1].trim();
        // Try token validation, otherwise continue with next filter
        if (!jwtService.isValidAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = jwtService.getSubjectFromToken(token);
        List<? extends GrantedAuthority> authorities = jwtService.getRolesFromToken(token);
        User principal = new User(username, "", true, true, true, true, authorities);

        // Attach user details and granted authorities to authentication
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal, null,
                authorities
        );
        authentication
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Proceed with filtering
        filterChain.doFilter(request, response);
    }
}
