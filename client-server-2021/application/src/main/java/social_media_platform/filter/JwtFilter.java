package social_media_platform.filter;

import io.jsonwebtoken.Claims;
import social_media_platform.services.JwtService;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@JwtVerification
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    private static final String REALM = "api";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    /**
     * Filter that validates the JWT token
     *
     * @param requestContext the request context
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                .substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            // Validate the token
            Claims claims = validateToken(token);
            bindAccountToContext(requestContext, claims);

        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    /**
     * Checks if the request is using token based authentication
     *
     * @param authorizationHeader the requests authorization header
     * @return
     */
    private boolean isTokenBasedAuthentication(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    /**
     * Aborts authentication on unauthorized request
     *
     * @param requestContext the request context
     */
    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE,
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }

    /**
     * Validates the JWT token
     *
     * @param token JWT token
     * @return claim
     * @throws Exception
     */
    private Claims validateToken(String token) throws Exception {
        return JwtService.decodeJWT(token);
    }

    /**
     * Binds the requesting account to a security context
     *
     * @param requestContext the request context
     * @param claims the JWT claims
     */
    private void bindAccountToContext(ContainerRequestContext requestContext, Claims claims) {
        final long userId = Long.parseLong(claims.getSubject());

        SecurityContext originalContext = requestContext.getSecurityContext();
        Set<String> roles = new HashSet<>();
        if (userId == 1) {
            roles.add(Role.Admin.name());
        } else {
            roles.add(Role.User.name());
        }
        Authorizer authorizer = new Authorizer(roles, userId,null,
                originalContext.isSecure());
        requestContext.setSecurityContext(authorizer);
    }


    public static class Authorizer implements SecurityContext {

        Set<String> roles;
        long id;
        String username;
        boolean isSecure;
        public Authorizer(Set<String> roles, final long id, final String username,
                          boolean isSecure) {
            this.roles = roles;
            this.id = id;
            this.username = username;
            this.isSecure = isSecure;
        }

        @Override
        public Principal getUserPrincipal() {
            return new User(id, username);
        }

        @Override
        public boolean isUserInRole(String role) {
            return roles.contains(role);
        }

        @Override
        public boolean isSecure() {
            return isSecure;
        }

        @Override
        public String getAuthenticationScheme() {
            return JwtFilter.AUTHENTICATION_SCHEME;
        }
    }


    public static class User implements Principal {
        String name;
        long id;

        public User(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getName() { return name; }

        public long getId() {
            return id;
        }
    }
}
