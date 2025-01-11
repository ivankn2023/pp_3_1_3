package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserPage(Model model, Principal principal) {
        User loggedInUser = userService.findByUsername(principal.getName());
        model.addAttribute("loggedInUser", loggedInUser);
        return "user"; // Это ваша страница для пользователей
    }

    @GetMapping("/switchRole")
    public String switchRole(@RequestParam("role") String role, Principal principal) {
        // Можно добавить логику для проверки роли и перенаправления к нужной странице.

        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin"; // Перенаправление на страницу администратора
        } else if (role.equals("ROLE_USER")) {
            return "redirect:/user"; // Перенаправление на страницу пользователя
        }
        return "redirect:/user"; // По умолчанию возвращаем на страницу пользователя
    }
}