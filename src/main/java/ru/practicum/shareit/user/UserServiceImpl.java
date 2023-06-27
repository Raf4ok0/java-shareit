package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.utils.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    public static final int PAGE_SIZE = 20;
    public static final int START_PAGE = 0;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        List<UserDto> allUsers = new ArrayList<>();

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(START_PAGE, PAGE_SIZE, sortById);

        do {
            Page<User> userPage = userStorage.findAll(page);
            userPage.getContent().forEach(user -> allUsers.add(UserMapper.toUserDto(user)));
            if (userPage.hasNext()) {
                page = PageRequest.of(userPage.getNumber() + 1, userPage.getSize(), userPage.getSort());
            } else {
                page = null;
            }
        } while (page != null);

        log.info("Получен список пользователей длиной {}", allUsers.size());
        return allUsers;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto createUser(UserDto user) {
        User savedUser = userStorage.save(UserMapper.toUser(user));
        log.info("Создан пользователь с id = {}", savedUser.getId());
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(long userId) {
        Optional<User> user = userStorage.findById(userId);

        if (user.isEmpty()) {
            log.warn("Выполнена попытка получить пользователя по несуществующему id = {}", userId);
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }

        log.info("Получен пользователь с id = {}", userId);
        return UserMapper.toUserDto(user.get());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDto updateUser(UserDto user) {
        log.info("updateUser: {}", user);
        User currentUser = userStorage.findById(user.getId()).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, user.getId()))
        );

        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            currentUser.setName(user.getName());
        }

        User updatedUser = userStorage.save(currentUser);
        log.info("Обновлена информация о пользователе с id = {}", updatedUser.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(long userId) {
        try {
            userStorage.deleteById(userId);
            log.info("Пользователь с id = {} удален", userId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Выполнена попытка удалить несуществующего пользователя по id = {}", userId);
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
    }

}

