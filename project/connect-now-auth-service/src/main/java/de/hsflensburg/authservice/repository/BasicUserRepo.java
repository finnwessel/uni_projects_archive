package de.hsflensburg.authservice.repository;

import de.hsflensburg.authservice.domain.model.BasicUser;
import de.hsflensburg.authservice.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BasicUserRepo extends BasicUserRepoCustom, MongoRepository<BasicUser, ObjectId> {
    Optional<BasicUser> findByUsername(String username);
}

interface BasicUserRepoCustom {
    Optional<BasicUser> findByUsernameOrEmail(String usernameOrEmail);
    Optional<Set<Role>> getUserRoles(String username);
    void addUserRole(String username, Role role);
    void removeUserRole(String username, Role role);
    List<BasicUser> searchUserWithRoleAndName(Role role, String name);
    boolean existsByUsernameAndAuthoritiesContaining(String username, Role role);
}
@RequiredArgsConstructor
class BasicUserRepoCustomImpl implements BasicUserRepoCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<BasicUser> findByUsernameOrEmail(String usernameOrEmail) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("username").is(usernameOrEmail), Criteria.where("email").is(usernameOrEmail)));

        BasicUser basicUser = mongoTemplate.findOne(query, BasicUser.class);
        if (basicUser == null) {
            return Optional.empty();
        }
        return Optional.of(basicUser);
    }

    @Override
    public Optional<Set<Role>> getUserRoles(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        query.fields().include("authorities");
        BasicUser basicUser = mongoTemplate.findOne(query, BasicUser.class);
        if (basicUser == null) {
            return Optional.empty();
        }
        return Optional.of(basicUser.getAuthorities());
    }
    @Override
    public void addUserRole(String username, Role role) {
        Update update = new Update();
        update.push("authorities", role);
        Criteria criteria = Criteria.where("username").is(username);
        mongoTemplate.updateFirst(Query.query(criteria), update, BasicUser.class);
    }
    @Override
    public void removeUserRole(String username, Role role) {
        Update update = new Update();
        update.pull("authorities", role);
        Criteria criteria = Criteria.where("username").is(username);
        mongoTemplate.updateFirst(Query.query(criteria), update, BasicUser.class);
    }

    @Override
    public List<BasicUser> searchUserWithRoleAndName(Role role, String name) {
        Criteria authoritiesCriteria = Criteria.where("authorities").in(role);
        TextCriteria nameCriteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matching(name);
        Query query = new Query();
        query.addCriteria(authoritiesCriteria);
        query.addCriteria(nameCriteria);
        return mongoTemplate.find(query, BasicUser.class);
    }

    @Override
    public boolean existsByUsernameAndAuthoritiesContaining(String username, Role role) {
        Query query = new Query();

        Criteria usernameCriteria = Criteria.where("username").is(username);
        Criteria authoritiesCriteria = Criteria.where("authorities").in(role);

        query.addCriteria(new Criteria().andOperator(authoritiesCriteria, usernameCriteria));

        return mongoTemplate.exists(query, BasicUser.class);
    }
}
