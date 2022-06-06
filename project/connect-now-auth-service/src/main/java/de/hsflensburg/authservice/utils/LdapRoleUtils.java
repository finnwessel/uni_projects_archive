package de.hsflensburg.authservice.utils;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LdapRoleUtils {
    public static void addUserRoles(String ou,
                                    Collection<SimpleGrantedAuthority> grantedAuthorities,
                                    List<String> studentRoleIdentifier,
                                    List<String> teacherRoleIdentifier
    ) {
        List<String> userRoles = Arrays.stream(ou.split(":")).toList();

        if (containsRole(userRoles, studentRoleIdentifier)) {
            grantedAuthorities.add(new SimpleGrantedAuthority("STUDENT"));
        }

        if (containsRole(userRoles, teacherRoleIdentifier)) {
            grantedAuthorities.add(new SimpleGrantedAuthority("TEACHER"));
        }
    }

    private static boolean containsRole(List<String> userRoles, List<String> identifier) {
        return userRoles.stream().anyMatch(identifier::contains);
    }
}
