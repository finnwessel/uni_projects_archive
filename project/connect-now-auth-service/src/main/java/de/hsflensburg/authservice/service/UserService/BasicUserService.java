package de.hsflensburg.authservice.service.UserService;

import de.hsflensburg.authservice.domain.dto.RegisterRequest;
import de.hsflensburg.authservice.domain.dto.UserUpdateRequest;
import de.hsflensburg.authservice.domain.exception.MethodUnavailableException;
import de.hsflensburg.authservice.domain.exception.RoleAlreadyExists;
import de.hsflensburg.authservice.domain.mapper.UserMapper;
import de.hsflensburg.authservice.domain.model.*;
import de.hsflensburg.authservice.repository.BasicUserRepo;
import de.hsflensburg.authservice.repository.PasswordResetRequestRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ConditionalOnProperty(name="auth.service.authentication-method", havingValue="basic")
@Qualifier("basicUserService")
@Service
@RequiredArgsConstructor
public class BasicUserService implements IUserService {

    private final BasicUserRepo userRepo;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserModel createUser(RegisterRequest request) {
        BasicUser newBasicUser = new BasicUser(
                request.getEmail().split("@")[0].trim(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                passwordEncoder.encode(request.getPassword())
        );
        BasicUser basicUser = userRepo.insert(newBasicUser);
        return UserMapper.INSTANCE.basicUserToUser(basicUser);
    }

    public Optional<UserModel> updateUser(String username, UserUpdateRequest userUpdateRequest) {
        Optional<BasicUser> basicUser = userRepo.findByUsername(username);

        if (basicUser.isEmpty()) {
            return Optional.empty();
        }

        BasicUser fetchedUser = basicUser.get();

        if (userUpdateRequest.firstName() != null) {
            fetchedUser.setFirstName(userUpdateRequest.firstName());
        }

        if (userUpdateRequest.lastName() != null) {
            fetchedUser.setLastName(userUpdateRequest.lastName());
        }

        if (userUpdateRequest.email() != null) {
            fetchedUser.setEmail(userUpdateRequest.email());
        }

        userRepo.save(fetchedUser);
        return Optional.of(toUser(fetchedUser));
    }

    public Optional<UserModel> findByCredentials(String usernameOrEmail, String password) {
        Optional<UserModel> user = findByUsernameOrEmail(usernameOrEmail);
        if (user.isPresent()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return user;
            }
        }
        return Optional.empty();
    }

    public Optional<UserModel> findByUsernameOrEmail(String usernameOrEmail) {
        Optional<BasicUser> basicUser = userRepo.findByUsernameOrEmail(usernameOrEmail);
        return toUser(basicUser);
    }

    public Optional<UserModel> findByUsername(String username) {
        return toUser(userRepo.findByUsername(username));
    }

    @Override
    public void updateUserPassword(String username, String password) {
        Optional<BasicUser> basicUser = userRepo.findByUsernameOrEmail(username);
        if (basicUser.isPresent()) {
            BasicUser user = basicUser.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepo.save(user);
        }
    }

    public Optional<Set<Role>> getUserRoles(String username) {
        return userRepo.getUserRoles(username);
    }

    public void addUserRole(String subject, RoleEnum roleEnum) throws RoleAlreadyExists {
        Role role = new Role(roleEnum.toString());

        if (userRepo.existsByUsernameAndAuthoritiesContaining(subject, role)) {
            throw new RoleAlreadyExists();
        }

        userRepo.addUserRole(subject, role);
    }

    public void removeUserRole(String subject, RoleEnum roleEnum) {
        userRepo.removeUserRole(subject, new Role(roleEnum.toString()));
    }

    @Override
    public List<UserModel> searchUserWithRole(RoleEnum roleEnum, String name) {
        List<BasicUser> basicUsers = userRepo.searchUserWithRoleAndName(new Role(roleEnum.toString()), name);
        return basicUsers.stream().map(BasicUserService::toUser).collect(Collectors.toList());
    }
    private static Optional<UserModel> toUser(Optional<BasicUser> basicUser) {
        return basicUser.map(UserMapper.INSTANCE::basicUserToUser);
    }
    private static UserModel toUser(BasicUser basicUser) {
        return UserMapper.INSTANCE.basicUserToUser(basicUser);
    }
}
