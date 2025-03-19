package org.addy.hello_ldap.model.request;

public record LoginRequest(
        String username,
        String password
) {
}
