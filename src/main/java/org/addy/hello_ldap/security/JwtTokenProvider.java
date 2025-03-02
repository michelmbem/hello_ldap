package org.addy.hello_ldap.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.configuration.properties.AuthTokenProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final AuthTokenProperties tokenProperties;
    private final UserDetailsService userDetailsService;

    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenProperties.getExpiration());

        return Jwts.builder()
                .subject(username)
                .issuer(tokenProperties.getIssuer())
                .issuedAt(now)
                .expiration(validity)
                .audience().add(tokenProperties.getAudience()).and()
                .claims(Jwts.claims().add("roles", roles).build())
                .signWith(tokenProperties.getSigningKey())
                .compact();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTH_HEADER_NAME);

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX))
            return bearerToken.substring(TOKEN_PREFIX.length());

        return null;
    }

    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        Date now = new Date();

        return claims.getIssuer().equals(tokenProperties.getIssuer()) &&
                claims.getIssuedAt().before(now) &&
                claims.getExpiration().after(now) &&
                claims.getAudience().containsAll(tokenProperties.getAudience());
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(parseToken(token).getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(tokenProperties.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
