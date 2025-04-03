package ru.vpiteresneg.kata.Rest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vpiteresneg.kata.Rest.model.Role;
import ru.vpiteresneg.kata.Rest.model.User;
import ru.vpiteresneg.kata.Rest.repositories.RoleRepository;
import ru.vpiteresneg.kata.Rest.service.UserService;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String adminPanel(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", userService.getAll());
        return "admin";
    }

    @GetMapping("/new")
    public String newUserForm(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", Arrays.asList("ADMIN", "USER"));
        return "new";
    }

    @PostMapping
    public String createUser(@ModelAttribute User user,
                             @RequestParam(value = "selectedRoles", required = false) List<String> selectedRoles) {

        if (selectedRoles != null && !selectedRoles.isEmpty()) {
            List<Role> roles = selectedRoles.stream()
                    .map(roleName -> roleRepository.findByName("ROLE_" + roleName)
                            .orElseThrow(() -> new RuntimeException("Роль не найдена: " + roleName)))
                    .collect(Collectors.toList());
            user.setRoles(roles);
        } else {
            user.setRoles(List.of(roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Роль USER не найдена"))));
        }

        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@AuthenticationPrincipal User currentUser,
                               @PathVariable Long id, Model model) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("allRoles", Arrays.asList("ADMIN", "USER"));
        return "edit";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam Integer age,
                             @RequestParam String email,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             @RequestParam(value = "selectedRoles", required = false) List<String> selectedRoles) {

        User user = userService.findById(id); // достаём старого

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);

        // 🛡️ Важно: оставить старый пароль, если новый не задан
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword);
        } // иначе не трогаем, останется старый

        if (selectedRoles != null && !selectedRoles.isEmpty()) {
            List<Role> roles = selectedRoles.stream()
                    .map(roleName -> roleRepository.findByName("ROLE_" + roleName)
                            .orElseThrow(() -> new RuntimeException("Роль не найдена: " + roleName)))
                    .collect(Collectors.toList());
            user.setRoles(roles);
        }

        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/confirm-delete/{id}")
    public String confirmDelete(@AuthenticationPrincipal User currentUser,
                                @PathVariable Long id, Model model) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", userService.findById(id));
        return "confirm-delete";
    }



}