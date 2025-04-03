package ru.vpiteresneg.kata.Rest.controller;

import org.springframework.web.bind.annotation.*;
import ru.vpiteresneg.kata.Rest.model.Role;
import ru.vpiteresneg.kata.Rest.repositories.RoleRepository;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleRestController {

    private final RoleRepository roleRepository;

    public RoleRestController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}