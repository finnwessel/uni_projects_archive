package social_media_platform.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JwtVerification
@Provider
@Priority(Priorities.AUTHORIZATION)
public class JwtRoleFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    /**
     * Filter that checks if user has the permissions to execute a certain method
     *
     * @param requestContext the request context
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Role> classRoles = extractRoles(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Role> methodRoles = extractRoles(resourceMethod);

        try {
            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations
            if (methodRoles.isEmpty()) {
                checkPermissions(classRoles, requestContext.getSecurityContext());
            } else {
                checkPermissions(methodRoles, requestContext.getSecurityContext());
            }

        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    /**
     * Extracts the roles from the annotated element
     *
     * @param annotatedElement an annotated element
     * @return the roles
     */
    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<Role>();
        } else {
            JwtVerification secured = annotatedElement.getAnnotation(JwtVerification.class);
            if (secured == null) {
                return new ArrayList<Role>();
            } else {
                Role[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    /**
     * Checks if a role has permissions
     *
     * @param allowedRoles the roles
     * @param securityContext the security context
     * @throws Exception
     */
    private void checkPermissions(List<Role> allowedRoles, SecurityContext securityContext) throws Exception {
        boolean isAllowed = allowedRoles.stream()
                    .anyMatch(role -> securityContext.isUserInRole(role.name()));
        if (!isAllowed) {
            throw new Exception("User has not the required role to access this method");
        }
        // Check if the user contains one of the allowed roles
        // Throw an Exception if the user has not permission to execute the method
    }
}
