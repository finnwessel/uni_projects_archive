package de.hsflensburg.authservice.api.data;

import de.hsflensburg.authservice.domain.model.BasicUser;
import de.hsflensburg.authservice.domain.model.Role;
import de.hsflensburg.authservice.repository.BasicUserRepo;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserTestDataFactory {
    private final BasicUserRepo userRepo;

    public UserTestDataFactory(BasicUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public static final String notExistingUsername = "not_existing_username";
    public static final String clearTextPassword = "12345678";
    public final String hashedPassword = "$2a$10$vyhic6Zv1/DHd9LWAV92Oe.T3c7LzUImcLRbDxxtUEylu8OT6QJ72";

    public final List<BasicUser> basicUsers = List.of(
            new BasicUser("bert", "bert@bert.com", "Bert", "Müller", hashedPassword,
                    Set.of(new Role("ROLE_STUDENT"))
            ),
            new BasicUser("herbert", "herbert@herbert.de", "Herbert", "Petersen", hashedPassword,
                    Set.of(new Role("ROLE_ADMIN"))
            ),
            new BasicUser("maria", "maria@maria.de", "Maria", "Jürgensen", hashedPassword,
                    Set.of(new Role("ROLE_TEACHER"))
            )
    );

    public void up() {
        userRepo.insert(basicUsers);
    }

    public void down() {
        userRepo.deleteAll(basicUsers);
    }
}
