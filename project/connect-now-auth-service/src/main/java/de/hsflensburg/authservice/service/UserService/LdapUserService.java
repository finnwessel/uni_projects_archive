package de.hsflensburg.authservice.service.UserService;

import de.hsflensburg.authservice.domain.dto.RegisterRequest;
import de.hsflensburg.authservice.domain.dto.UserUpdateRequest;
import de.hsflensburg.authservice.domain.exception.MethodUnavailableException;
import de.hsflensburg.authservice.domain.mapper.UserMapper;
import de.hsflensburg.authservice.domain.model.LdapUser;
import de.hsflensburg.authservice.domain.model.Role;
import de.hsflensburg.authservice.domain.model.RoleEnum;
import de.hsflensburg.authservice.domain.model.UserModel;
import de.hsflensburg.authservice.repository.LdapUserRepo;
import de.hsflensburg.authservice.utils.LdapRoleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(name="auth.service.authentication-method", havingValue="ldap")
@Qualifier("ldapUserService")
@Service
@RequiredArgsConstructor
public class LdapUserService implements IUserService {

    @Value("#{'${ldap.role.identifiers.student}'.split(',')}")
    private List<String> studentRoleIdentifier;

    @Value("#{'${ldap.role.identifiers.teacher}'.split(',')}")
    private List<String> teacherRoleIdentifier;

    @Value("#{'${ldap.role.identifiers.admin}'.split(',')}")
    private List<String> adminRoleIdentifier;

    private final LdapUserRepo ldapUserRepo;

    @Override
    public UserModel createUser(RegisterRequest request) throws MethodUnavailableException {
        throw new MethodUnavailableException("Not available for authentication with current provider");
    }

    public Optional<UserModel> updateUser(String username, UserUpdateRequest userUpdateRequest) throws MethodUnavailableException {
        throw new MethodUnavailableException("Not available for authentication with current provider");
    }

    @Override
    public Optional<UserModel> findByCredentials(String usernameOrEmail, String password) {
        LdapUser ldapUser = ldapUserRepo.existsWithIdentifierAndPassword(usernameOrEmail, password);
        if (ldapUser == null) {
            return Optional.empty();
        }
        return Optional.of(UserMapper.INSTANCE.ldapUserToUser(ldapUser));
    }

    @Override
    public Optional<UserModel> findByUsernameOrEmail(String usernameOrEmail) {
        LdapUser ldapUser = ldapUserRepo.findByUsernameOrEmail(usernameOrEmail);
        return Optional.of(UserMapper.INSTANCE.ldapUserToUser(ldapUser));
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        LdapUser ldapUser = ldapUserRepo.findByUsername(username);
        return Optional.of(UserMapper.INSTANCE.ldapUserToUser(ldapUser));
    }

    @Override
    public void updateUserPassword(String username, String password) throws MethodUnavailableException {
        throw new MethodUnavailableException("Not available for authentication with current provider");
    }

    @Override
    public Optional<Set<Role>> getUserRoles(String username) {
        LdapUser ldapUser = ldapUserRepo.findByUsername(username);

        if (ldapUser == null) {
            return Optional.empty();
        }

        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

        LdapRoleUtils.addUserRoles(ldapUser.getOu(), grantedAuthorities, studentRoleIdentifier, teacherRoleIdentifier);

        return Optional.of(grantedAuthorities.stream()
                .map(authority -> String.format("%s%s", Role.PREFIX, authority.getAuthority().toUpperCase()))
                .map(Role::new).collect(Collectors.toSet()));
    }

    @Override
    public void addUserRole(String subject, RoleEnum roleEnum) throws MethodUnavailableException {
        throw new MethodUnavailableException("Not available for authentication with current provider");
    }

    @Override
    public void removeUserRole(String subject, RoleEnum roleEnum) throws MethodUnavailableException {
        throw new MethodUnavailableException("Not available for authentication with current provider");
    }

    @Override
    public List<UserModel> searchUserWithRole(RoleEnum roleEnum, String name) {
        List<String> groups;
        switch (roleEnum) {
            case ROLE_ADMIN -> groups = adminRoleIdentifier;
            case ROLE_STUDENT -> groups = studentRoleIdentifier;
            case ROLE_TEACHER -> groups = teacherRoleIdentifier;
            default -> groups = new ArrayList<>(0);
        }


        return ldapUserRepo.findByGroupsAndName(groups, name).stream().map(UserMapper.INSTANCE::ldapUserToUser).toList();
    }

}
