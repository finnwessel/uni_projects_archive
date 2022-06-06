package de.hsflensburg.authservice.populator;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CustomLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {
    private final List<String> studentRoleIdentifier;
    private final List<String> teacherRoleIdentifier;

    private final List<String> adminRoleIdentifier;

    public CustomLdapAuthoritiesPopulator(List<String> studentRoleIdentifier, List<String> teacherRoleIdentifier, List<String> adminRoleIdentifier) {
        this.studentRoleIdentifier = studentRoleIdentifier;
        this.teacherRoleIdentifier = teacherRoleIdentifier;
        this.adminRoleIdentifier = adminRoleIdentifier;
    }

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Attributes attributes = userData.getAttributes();
        try {
            // if ou is not present return
            if (attributes.get("ou") == null) {
                return grantedAuthorities;
            }
            String ou = (String) attributes.get("ou").get();
            List<String> userRoles = Arrays.stream(ou.split(":")).toList();

            if (containsRole(userRoles, studentRoleIdentifier)) {
                grantedAuthorities.add(new SimpleGrantedAuthority("STUDENT"));
            }

            if (containsRole(userRoles, teacherRoleIdentifier)) {
                grantedAuthorities.add(new SimpleGrantedAuthority("TEACHER"));
            }

        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return grantedAuthorities;
    }
    private static boolean containsRole(List<String> userRoles, List<String> identifier) {
        return userRoles.stream().anyMatch(identifier::contains);
    }
}
