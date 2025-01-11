package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String allUsers(Model model) {
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = userService.findByUsername(authentication.getName());
        model.addAttribute("loggedInUser", loggedInUser);

        return "adminPage";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, @RequestParam String role) {
        userService.saveUser(user, role);
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user,
                             @RequestParam("role") String role,
                             RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Обновление пользователя: " + user);
            userService.updateUser(user, role);
            return "redirect:/admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении пользователя: " + e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") long id) {
        System.out.println("idididi");
        userService.deleteUser(id);
        System.out.println("idididi2");
        return "redirect:/admin";
    }

    @GetMapping("/user_view")
    public String getUserPage(@RequestParam("id") long id, Model model) {
        Optional<User> optionalUser = userService.getUser(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            return "user";
        } else {
            throw new UsernameNotFoundException(String.format("Username with id = %s not found", id));
        }
    }

    @GetMapping("/switchRole")
    public String switchRole(@RequestParam("role") String role) {

        if ("ROLE_ADMIN".equals(role)) {
            return "redirect:/adminPage";
        } else if ("ROLE_USER".equals(role)) {
            return "redirect:/user";
        }
        return "redirect:/admin";
    }
}