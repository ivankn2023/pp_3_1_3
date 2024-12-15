package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
        return "adminPage";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/adminPage";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "adminPage";
        }
        userService.saveUser(user);
        return "redirect:/adminPage";
    }


    @GetMapping("/update")
    public String updateUser(@RequestParam("id") long id, Model model) {
        Optional<User> optionalUser = userService.getUser(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get()); // Извлекаем User из Optional
            return "editUserForm"; // Убедитесь, что это правильное имя шаблона
        } else {
            throw new UsernameNotFoundException(String.format("Username with id = %s not found", id));
        }
    }

    @PostMapping("/update")
    public String saveUser(@ModelAttribute User user) {
        userService.saveUser(user); // Предполагается, что у вас есть метод для обновления
        return "redirect:/admin"; // Перенаправление после обновления
    }



    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") long id) {
        userService.deleteUser(id);
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
}