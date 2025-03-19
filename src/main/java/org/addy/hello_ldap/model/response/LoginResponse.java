package org.addy.hello_ldap.model.response;

public record LoginResponse(
        boolean success,
        String accessToken
) {
}
