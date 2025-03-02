package org.addy.hello_ldap.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.ldap.GroupAttributesMapper;
import org.addy.hello_ldap.model.Group;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Repository;

import javax.naming.ldap.LdapName;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Slf4j
@RequiredArgsConstructor
@Repository
public class GroupRepository {

    private final LdapTemplate ldapTemplate;
    private final GroupAttributesMapper groupAttributesMapper;

    public List<Group> findAll() {
        return ldapTemplate.search(
                query().base("ou=groups").where("objectClass").is("posixGroup"),
                groupAttributesMapper);
    }

    public List<Group> findByUsername(String username) {
        return ldapTemplate.search(
                query().base("ou=groups")
                        .where("objectClass").is("posixGroup")
                        .and("memberUid").is(username),
                groupAttributesMapper);
    }

    public Group findByName(String name) {
        return ldapTemplate.lookup("cn=" + name + ",ou=groups", groupAttributesMapper);
    }

    public void addMember(String groupName, String username) {
        DirContextOperations context = ldapTemplate.lookupContext(buildDn(groupName));
        context.addAttributeValue("memberUid", username);

        ldapTemplate.modifyAttributes(context);
    }

    public void removeMember(String groupName, String username) {
        DirContextOperations context = ldapTemplate.lookupContext(buildDn(groupName));
        context.removeAttributeValue("memberUid", username);

        ldapTemplate.modifyAttributes(context);
    }

    private static LdapName buildDn(String groupName) {
        return LdapNameBuilder.newInstance()
                .add("ou", "groups")
                .add("cn", groupName)
                .build();
    }
}
