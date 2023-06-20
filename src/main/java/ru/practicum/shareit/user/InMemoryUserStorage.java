package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long lastId;

    @Override
    public User create(User user) {
        if (emails.contains(user.getEmail())) {
            throw new AlreadyExistException("Такой EMail уже используется");
        }
        user.setId(++lastId);
        users.put(user.getId(), user);
        emails.add(user.getEmail());

        log.info("Создан пользователь с id = {}", user.getId());
        return user;
    }

    @Override
    public User get(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        User user = users.get(userId);
        log.info("Получен пользователь с id = {}", userId);

        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        System.out.println(oldUser);
        if (!oldUser.getEmail().equals(user.getEmail()) && emails.contains(user.getEmail())) {
            throw new AlreadyExistException("Такой EMail уже используется");
        }
        System.out.println(emails);
        emails.remove(oldUser.getEmail());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        log.info("Обновлена информация о пользователе с id = {}", user.getId());
        System.out.println(user);
        System.out.println(emails);
        return user;
    }

    @Override
    public void delete(long userId) {
        User oldUser = users.get(userId);
        users.remove(userId);
        emails.remove(oldUser.getEmail());
        log.info("Пользователь с id = {} удален", userId);
    }

    @Override
    public List<User> getAll() {
        List<User> allUsers = new ArrayList<>(users.values());
        log.info("Получен список пользователей длиной {}", allUsers.size());

        return allUsers;
    }

    @Override
    public boolean isUserExist(long userId) {
        log.info("Получена информация о существовании пользователя с id = {}", userId);
        return users.containsKey(userId);
    }
}
