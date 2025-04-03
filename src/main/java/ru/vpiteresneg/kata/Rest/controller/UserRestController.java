package ru.vpiteresneg.kata.Rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vpiteresneg.kata.Rest.model.User;
import ru.vpiteresneg.kata.Rest.service.UserService;

import java.util.List;
// Это REST-контроллер, он обрабатывает HTTP-запросы (GET, POST, PUT, DELETE) и возвращает данные в формате JSON.
@RestController
@RequestMapping("/api/users")// Все методы будут доступны по адресу /api/users (например, /api/users/5)
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();// Возвращает список всех пользователей в формате JSON
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        userService.saveUser(user);// Сохраняет пользователя, пришедшего в теле запроса
        return user;// Возвращает созданного пользователя (уже с ID)
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);// Устанавливаем ID, чтобы обновить именно существующего пользователя
        userService.saveUser(user);// Перезаписываем пользователя в базе
        return user;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}