package org.addy.hello_ldap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.model.request.LoginRequest;
import org.addy.hello_ldap.security.JwtTokenProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public String authenticate(LoginRequest request) {
        if (((UserService) userDetailsService).authenticate(request.getUsername(), request.getPassword())) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            return jwtTokenProvider.generateToken(userDetails.getUsername(),
                    userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        }

        return null;
    }
}
