package org.addy.hello_ldap.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.model.User;
import org.addy.hello_ldap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("by-name/{displayName}")
    public List<User> getByDisplayName(@PathVariable String displayName) {
        return userService.findByDisplayName(displayName);
    }

    @GetMapping("{username}")
    public User getByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        userService.create(user);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .build(user.getUsername());

        return ResponseEntity.created(uri).body(user);
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PutMapping("{username}")
    public ResponseEntity<Void> update(@PathVariable String username, @RequestBody User user) {
        userService.update(username, user);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @DeleteMapping("{username}")
    public ResponseEntity<Void> delete(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails currentUser) {

        userService.delete(username, currentUser);

        return ResponseEntity.noContent().build();
    }

}
