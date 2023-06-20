package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto user) {
        return UserMapper.toUserDto(userStorage.create(UserMapper.toUser(user)));
    }

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.toUserDto(userStorage.get(userId));
    }

    @Override
    public UserDto updateUser(UserDto user) {
        User currentUser = userStorage.get(user.getId());
        User newUser = UserMapper.toUser(user);

        if (user.getEmail() == null) {
            newUser.setEmail(currentUser.getEmail());
        }

        if (user.getName() == null || user.getName().isBlank()) {
            newUser.setName(currentUser.getName());
        }

        return UserMapper.toUserDto(userStorage.update(newUser));
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.delete(userStorage.get(userId).getId());
    }

}

