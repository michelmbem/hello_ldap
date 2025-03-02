package org.addy.hello_ldap.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.addy.hello_ldap.model.Group;
import org.addy.hello_ldap.service.GroupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("group")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public List<Group> getAll() {
        return groupService.findAll();
    }

    @GetMapping("{name}")
    public Group getByName(@PathVariable String name) {
        return groupService.findByName(name);
    }

}
