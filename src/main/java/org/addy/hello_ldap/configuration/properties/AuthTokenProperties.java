package org.addy.hello_ldap.configuration.properties;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.util.List;

@Data
@ConfigurationProperties("helloldap.security.auth.token")
public final class AuthTokenProperties {private String issuer;
    private String secret;
    private long expiration;
    private List<String> audience;

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
