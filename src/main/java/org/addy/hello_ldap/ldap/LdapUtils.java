package org.addy.hello_ldap.ldap;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

@Slf4j
@UtilityClass
public class LdapUtils {

    public void dumpAttributes(Attributes attributes) throws NamingException {
        log.info("---------- Dumping atttributes ----------");

        var allAttributes = attributes.getAll();

        while (allAttributes.hasMore()) {
            Attribute attribute = allAttributes.next();
            log.info("{} = {}", attribute.getID(), attribute.get());
        }
    }
}
