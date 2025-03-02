package org.addy.hello_ldap.ldap;

import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.model.User;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import static org.addy.hello_ldap.ldap.LdapUtils.dumpAttributes;

@Slf4j
@Component
public class UserAttributesMapper implements AttributesMapper<User> {

    @Override
    public User mapFromAttributes(Attributes attributes) throws NamingException {
        dumpAttributes(attributes);

        return new User(
                attributes.get("uid").get().toString(),
                attributes.get("displayName").get().toString(),
                new String((byte[]) attributes.get("userPassword").get()),
                null);
    }
}
