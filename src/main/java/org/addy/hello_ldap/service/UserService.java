package org.addy.hello_ldap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.model.User;
import org.addy.hello_ldap.repository.GroupRepository;
import org.addy.hello_ldap.repository.UserRepository;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public boolean authenticate(String username, String password) {
        return userRepository.authenticate(username, password);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByDisplayName(String displayName) {
        return userRepository.findByDisplayName(displayName).stream().map(this::userWithGroups).toList();
    }

    public User findByUsername(String username) {
        return userWithGroups(userRepository.findByUsername(username));
    }

    public void create(User user) {
        userRepository.create(user);
        addUserToGroups(user.getUsername(), user);
    }

    public void update(String username, User user) {
        userRepository.update(username, user);
        removeUserFromAllGroups(username);
        addUserToGroups(username, user);
    }

    public void delete(String username, UserDetails currentUser) {
        if (Objects.equals(username, currentUser.getUsername()))
            throw new IllegalArgumentException("You cannot delete yourself");

        userRepository.delete(username);
        removeUserFromAllGroups(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return findByUsername(username);
        } catch (NameNotFoundException | InvalidNameException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    private User userWithGroups(User user) {
        user.setGroups(groupRepository.findByUsername(user.getUsername()));

        return user;
    }

    private void addUserToGroups(String username, User user) {
        if (user.getGroups() != null)
            user.getGroups().forEach(g -> groupRepository.addMember(g.getName(), username));
    }

    private void removeUserFromAllGroups(String username) {
        groupRepository.findAll().forEach(g -> groupRepository.removeMember(g.getName(), username));
    }
}
