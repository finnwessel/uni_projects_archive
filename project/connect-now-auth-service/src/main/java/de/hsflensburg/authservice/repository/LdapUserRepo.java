package de.hsflensburg.authservice.repository;

import de.hsflensburg.authservice.domain.model.LdapUser;
import de.hsflensburg.authservice.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.*;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Repository;

import javax.naming.directory.SearchControls;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Repository
public interface LdapUserRepo extends LdapUserRepoCustom, LdapRepository<LdapUser> {
    LdapUser findByUsername(String username);
    List<LdapUser> findByUsernameLikeIgnoreCase(String username);
}

interface LdapUserRepoCustom {
    Set<Role> getUserRoles(String username);
    LdapUser findByUsernameOrEmail(String usernameOrEmail);
    List<LdapUser> findByGroupsAndName(List<String> groups, String name);
    LdapUser existsWithIdentifierAndPassword(String username, String password);
}

@RequiredArgsConstructor
class LdapUserRepoCustomImpl implements LdapUserRepoCustom {
    private static final Integer THREE_SECONDS = 3000;

    private final LdapTemplate ldapTemplate;

    @Override
    public Set<Role> getUserRoles(String username) {
        String ldapName = LdapNameBuilder.newInstance().add("ou=users,dc=example,dc=org").add("cn", username).build().toString();
        return ldapTemplate.search(query()
                        .where("objectclass")
                        .is("groupOfUniqueNames")
                        .and("uniqueMember")
                        .is(ldapName), (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get())
                .stream()
                .map(group -> String.format("%s%s", Role.PREFIX, group.toUpperCase()))
                .map(Role::new).collect(Collectors.toSet());
    }

    @Override
    public LdapUser findByUsernameOrEmail(String usernameOrEmail) {
        OrFilter orFilter = new OrFilter();
        orFilter.or(new EqualsFilter("uid", usernameOrEmail));
        orFilter.or(new EqualsFilter("mail", usernameOrEmail));

        AndFilter andFilter = new AndFilter();
        andFilter.and(orFilter);
        andFilter.and(new PresentFilter("uid"));

        LdapQuery query = query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(THREE_SECONDS)
                .countLimit(1)
                .attributes("uid", "givenName", "sn", "mail")
                .base(LdapUtils.emptyLdapName())
                .filter(orFilter);

        return ldapTemplate.findOne(query, LdapUser.class);
    }

    public List<LdapUser> findByGroupsAndName(List<String> groups, String name) {
        OrFilter orFilter = new OrFilter();
        for (String group: groups) {
            orFilter.or(new LikeFilter("ou", "*" + group + "*"));
        }

        AndFilter andFilter = new AndFilter();
        andFilter.and(new LikeFilter("cn", "*" + name + "*"));
        andFilter.and(orFilter);

        LdapQuery query = query()
                .searchScope(SearchScope.SUBTREE)
                .timeLimit(THREE_SECONDS)
                .countLimit(50)
                .attributes("uid", "givenName", "sn", "mail")
                .base(LdapUtils.emptyLdapName())
                .filter(andFilter);

        return ldapTemplate.find(query, LdapUser.class);
    }

    @Override
    public LdapUser existsWithIdentifierAndPassword(String identifier, String password) {
        try {
            OrFilter orFilter = new OrFilter();
            orFilter.or(new EqualsFilter("uid", identifier));
            orFilter.or(new EqualsFilter("mail", identifier));

            AndFilter andFilter = new AndFilter();
            andFilter.and(orFilter);
            andFilter.and(new EqualsFilter("userPassword", password));
            andFilter.and(new PresentFilter("uid"));

            LdapQuery query = query()
                    .searchScope(SearchScope.SUBTREE)
                    .timeLimit(THREE_SECONDS)
                    .countLimit(1)
                    .attributes("uid", "givenName", "sn", "mail")
                    .base(LdapUtils.emptyLdapName())
                    .filter(andFilter);

            return ldapTemplate.findOne(query, LdapUser.class);
        } catch (Exception e) {
            return null;
        }
    }


}
