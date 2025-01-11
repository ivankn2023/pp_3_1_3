package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository rolesRepository;

    @Autowired
    public RoleService(RoleRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public List<Role> getRoles() {
        return rolesRepository.findAll();
    }

    public List<Role> getRolesByIds(List<Long> ids) {
        return rolesRepository.findAllById(ids);
    }
}