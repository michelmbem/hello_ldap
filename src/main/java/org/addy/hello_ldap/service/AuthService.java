package org.addy.hello_ldap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.model.Group;
import org.addy.hello_ldap.model.User;
import org.addy.hello_ldap.model.request.LoginRequest;
import org.addy.hello_ldap.model.response.LoginResponse;
import org.addy.hello_ldap.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse authenticate(LoginRequest request) {
        if (userService.authenticate(request.username(), request.password())) {
            User user = userService.findByUsername(request.username());
            String accessToken = jwtTokenProvider.generateToken(
                    user.getUsername(),
                    user.getGroups().stream().map(Group::getName).toList()
            );

            return new LoginResponse(true, accessToken);
        }

        return new LoginResponse(false, null);
    }
}
