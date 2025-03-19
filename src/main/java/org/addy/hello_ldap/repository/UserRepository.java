package org.addy.hello_ldap.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.ldap.UserAttributesMapper;
import org.addy.hello_ldap.model.User;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserRepository {

    private final LdapTemplate ldapTemplate;
    private final UserAttributesMapper userAttributesMapper;

    public boolean authenticate(String username, String password) {
        return ldapTemplate.authenticate("ou=users", "uid=" + username, password);
    }

    public List<User> findAll() {
        return ldapTemplate.search(
                query().base("ou=users").where("objectClass").is("inetOrgPerson"),
                userAttributesMapper
        );
    }

    public List<User> findByDisplayName(String displayName) {
        return ldapTemplate.search(
                "ou=users",
                new LikeFilter("sn", "*" + displayName + "*").encode(),
                userAttributesMapper
        );
    }

    public User findByUsername(String username) {
        return ldapTemplate.lookup("cn=" + username + ",ou=users", userAttributesMapper);
    }

    public void create(User user) {
        var context = new DirContextAdapter(buildDn(user.getUsername()));
        bindAttributes(context, user.getUsername(), user);

        ldapTemplate.bind(context);
    }

    public void update(String username, User user) {
        DirContextOperations context = ldapTemplate.lookupContext(buildDn(username));
        bindAttributes(context, username, user);

        ldapTemplate.modifyAttributes(context);
    }

    public void delete(String username) {
        ldapTemplate.unbind(buildDn(username));
    }

    private static Name buildDn(String username) {
        return LdapNameBuilder.newInstance()
                .add("ou", "users")
                .add("cn", username)
                .build();
    }

    private static void bindAttributes(DirContextOperations context, String username, User user) {
        context.setAttributeValues("objectClass", new String[] { "inetOrgPerson", "shadowAccount" });
        context.setAttributeValue("cn", username);
        context.setAttributeValue("uid", username);
        context.setAttributeValue("sn", user.getDisplayName());
        context.setAttributeValue("displayName", user.getDisplayName());
        context.setAttributeValue("givenName", user.getDisplayName());
        context.setAttributeValue("userPassword", user.getPassword());
    }
}
