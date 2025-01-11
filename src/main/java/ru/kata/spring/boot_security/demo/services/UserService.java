package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        // Здесь вы можете явно установить роли, если это необходимо
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }


    @Transactional
    public void saveUser(User user, String roleName) {
        List<Role> roles = getRolesByRoleName(roleName);

        // Устанавливаем роли пользователю
        user.setRoles(roles);

        // Шифруем пароль
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Сохраняем пользователя
        userRepository.save(user);
    }



    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void deleteUser(long id) {
        if (getUser(id).isPresent()) {
            userRepository.deleteById(id);
        }
    }

    public List<Role> getRolesByRoleName(String roleName) {
        List<Role> roles = new ArrayList<>();

        // Проверяем, какая роль передана и добавляем соответствующие роли
        if ("ROLE_ADMIN".equals(roleName)) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            Role userRole = roleRepository.findByName("ROLE_USER");

            if (adminRole != null) {
                roles.add(adminRole);
            }
            if (userRole != null) {
                roles.add(userRole);
            }
        } else if ("ROLE_USER".equals(roleName)) {
            Role userRole = roleRepository.findByName("ROLE_USER");

            if (userRole != null) {
                roles.add(userRole);
            }
        }
        return roles;
    }

    @Transactional
    public void updateUser(User updatedUser, String roleName) {
        Optional<User> optionalUser = userRepository.findById(updatedUser.getId());

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            existingUser.setName(updatedUser.getName());
            existingUser.setSurname(updatedUser.getSurname());
            existingUser.setAge(updatedUser.getAge());
            existingUser.setUsername(updatedUser.getUsername()); // Устанавливаем новое имя пользователя

            // Если пароль не пустой, шифруем и устанавливаем его
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // Также обновляем роли
            List<Role> roles = getRolesByRoleName(roleName);
            existingUser.setRoles(roles);

            userRepository.save(existingUser);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}