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
            Object attributeValue = attribute.get();

            log.info("{} : {} = {}", attribute.getID(), attributeValue.getClass().getName(), attributeValue);
        }
    }

    public String getString(Attributes attributes, String id) throws NamingException {
        Attribute attribute = attributes.get(id);

        return attribute != null ? attribute.get().toString() : null;
    }

    public byte[] getBytes(Attributes attributes, String id) throws NamingException {
        Attribute attribute = attributes.get(id);

        return attribute != null ? (byte[]) attribute.get() : null;
    }

    public String getBinaryString(Attributes attributes, String id) throws NamingException {
        byte[] bytes = getBytes(attributes, id);

        return bytes != null ? new String(bytes) : null;
    }
}
