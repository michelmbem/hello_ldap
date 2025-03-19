package org.addy.hello_ldap.configuration.properties;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.util.List;

@ConfigurationProperties("helloldap.security.auth.token")
public record AuthTokenProperties(
        String issuer,
        String secret,
        long expiration,
        List<String> audience
) {
    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
