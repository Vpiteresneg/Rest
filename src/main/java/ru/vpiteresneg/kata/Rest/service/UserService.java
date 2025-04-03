package ru.vpiteresneg.kata.Rest.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import ru.vpiteresneg.kata.Rest.model.User;


import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User findById(Long id);
    List<User> getAll();
    void saveUser(User user);
    void deleteUser(Long id);
    boolean saveUserWithRole(User user, String roleName);


    Optional<User> findByEmail(String email);
}
