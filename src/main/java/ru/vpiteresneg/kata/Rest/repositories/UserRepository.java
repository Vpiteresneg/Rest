package ru.vpiteresneg.kata.Rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vpiteresneg.kata.Rest.model.User;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
