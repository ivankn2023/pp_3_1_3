package ru.kata.spring.boot_security.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class UsersInit {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UsersInit(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void createTestUsersWithRoles() {
        Role role1 = new Role("ROLE_ADMIN");
        Role role2 = new Role("ROLE_USER");

        roleRepository.save(role1);
        roleRepository.save(role2);

        User user1 = new User
                ("user", "Иван", "Иванов",
                        34, new BCryptPasswordEncoder().encode("user"));
        User user2 = new User
                ("admin", "Алексей", "Сидоров",
                        24, new BCryptPasswordEncoder().encode("admin"));

//

        user1.setRoles(new HashSet<>(Set.of(role2)));
        user2.setRoles(new HashSet<>(Set.of(role1)));

        userRepository.save(user1);
        userRepository.save(user2);

    }
}