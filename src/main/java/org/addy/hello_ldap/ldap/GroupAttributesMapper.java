package org.addy.hello_ldap.ldap;

import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.model.Group;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import static org.addy.hello_ldap.ldap.LdapUtils.*;

@Slf4j
@Component
public class GroupAttributesMapper implements AttributesMapper<Group> {

    @Override
    public Group mapFromAttributes(Attributes attributes) throws NamingException {
        dumpAttributes(attributes);

        return new Group(getString(attributes, "cn"));
    }
}
