package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface UserStorage extends JpaRepository<User, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);

}
