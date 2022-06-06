package de.hsflensburg.authservice.service.UserService;

import de.hsflensburg.authservice.domain.dto.RegisterRequest;
import de.hsflensburg.authservice.domain.dto.UserUpdateRequest;
import de.hsflensburg.authservice.domain.exception.MethodUnavailableException;
import de.hsflensburg.authservice.domain.exception.RoleAlreadyExists;
import de.hsflensburg.authservice.domain.model.Role;
import de.hsflensburg.authservice.domain.model.RoleEnum;
import de.hsflensburg.authservice.domain.model.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IUserService {
    UserModel createUser(RegisterRequest request) throws MethodUnavailableException;
    Optional<UserModel> updateUser(String username, UserUpdateRequest userUpdateRequest) throws MethodUnavailableException;
    Optional<UserModel> findByCredentials(String usernameOrEmail, String password);
    Optional<UserModel> findByUsernameOrEmail(String usernameOrEmail);
    Optional<UserModel> findByUsername(String username);

    void updateUserPassword(String username, String password) throws MethodUnavailableException;

    Optional<Set<Role>> getUserRoles(String username) throws MethodUnavailableException;
    void addUserRole(String subject, RoleEnum roleEnum) throws MethodUnavailableException, RoleAlreadyExists;
    void removeUserRole(String subject, RoleEnum roleEnum) throws MethodUnavailableException;
    List<UserModel> searchUserWithRole(RoleEnum roleEnum, String name);
}
