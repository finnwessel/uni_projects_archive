package de.hsflensburg.authservice.domain.model;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Data
@Entry( /* base = "ou=users", */ objectClasses = { "top", "person", "inetOrgPerson" })
public final class LdapUser {
    @Id
    private Name id;

    private @Attribute(name = "uid") String username;
    private @Attribute(name = "givenName") String firstName;
    private @Attribute(name = "sn") String lastName;
    private @Attribute(name = "mail") String email;
    private @Attribute(name = "ou") String ou;
    private @Attribute(name = "userPassword") String password;

    // standard getters/setters
}
