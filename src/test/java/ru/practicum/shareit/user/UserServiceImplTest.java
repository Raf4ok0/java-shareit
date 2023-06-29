package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.utils.Constants.USER_NOT_FOUND_MESSAGE;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<User> requestCaptor;

    @Test
    void createUser_WhenUserDtoIsCorrect_ThenUserCreated() {
        User userToSave = new User(0L, "name", "email@mail.ru");
        UserDto inputUserDto = new UserDto(11, "name", "email@mail.ru");
        UserDto expectedUserDto = new UserDto(1, "name", "email@mail.ru");
        when(userStorage.save(any(User.class)))
                .thenReturn(new User(1L, "name", "email@mail.ru"));

        assertThatCode(() -> {
            UserDto actuslUserDto = userService.createUser(inputUserDto);
            assertThat(actuslUserDto)
                    .as("Проверка создания пользователя при корректных входных данных")
                    .isNotNull()
                    .isEqualTo(expectedUserDto);
        }).doesNotThrowAnyException();

        verify(userStorage, Mockito.times(1)).save(requestCaptor.capture());
        assertThat(requestCaptor.getValue())
                .as("Проверка передаваемого аргумента в метод сохранения пользователя")
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", userToSave.getName())
                .hasFieldOrPropertyWithValue("email", userToSave.getEmail());
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getUser_WhenUserExists_ThenReturnUser() {
        long id = 1;
        when(userStorage.findById(id))
                .thenReturn(Optional.of(new User(id, "name", "email@mail.ru")));
        UserDto expectedUserDto = new UserDto(id, "name", "email@mail.ru");

        assertThatCode(() -> {
            UserDto actualUserDto = userService.getUser(id);
            assertThat(actualUserDto)
                    .as("Проверка получения пользователя, когда пользователь существует")
                    .isNotNull()
                    .isEqualTo(expectedUserDto);
        }).doesNotThrowAnyException();

        verify(userStorage, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getUser_WhenUserDoesNotExist_ThenThrowNotFoundException() {
        long id = 1;
        when(userStorage.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка получения пользователя, когда пользователь не существует")
                .isThrownBy(() -> userService.getUser(id))
                .withMessage(String.format(USER_NOT_FOUND_MESSAGE, id));

        verify(userStorage, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void deleteUser_WhenUserExists_ThenUserDeleted() {
        assertThatCode(() -> userService.deleteUser(anyLong()))
                .as("Проверка удаления пользователя, когда пользователь существует")
                .doesNotThrowAnyException();

        verify(userStorage, Mockito.times(1)).deleteById(anyLong());
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ThenThrowNotFoundException() {
        long id = 1;
        doThrow(EmptyResultDataAccessException.class).when(userStorage).deleteById(id);

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка удаления пользователя, когда пользователь не существует")
                .isThrownBy(() -> userService.deleteUser(id))
                .withMessage(String.format(USER_NOT_FOUND_MESSAGE, id));

        verify(userStorage, Mockito.times(1)).deleteById(id);
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ThenThrowNotFoundException() {
        long id = 1;
        when(userStorage.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка обновления пользователя, когда пользователь не существует")
                .isThrownBy(() -> userService.updateUser(new UserDto(id, "newName", "new@mail.ru")))
                .withMessage(String.format(USER_NOT_FOUND_MESSAGE, id));

        verify(userStorage, Mockito.times(1)).findById(id);
        verify(userStorage, Mockito.never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userStorage, Mockito.never()).save(any(User.class));
        Mockito.verifyNoMoreInteractions(userStorage);
    }

    @Test
    void updateUser_WhenDtoContainsNullNameAndEmail_ThenUserUpdatedWithOnlyCorrectFields() {
        long id = 1;
        String oldName = "oldName";
        String oldEmail = "old@mail.ru";
        User oldUser = new User(id, oldName, oldEmail);
        when(userStorage.findById(id)).thenReturn(Optional.of(oldUser));
        when(userStorage.save(any(User.class))).thenReturn(oldUser);
        UserDto dtoToUpdate = new UserDto(id, null, null);

        assertThatCode(() -> {
            UserDto updated = userService.updateUser(dtoToUpdate);
            assertThat(updated)
                    .as("Проверка обновления пользователя, когда новые почта и имя null")
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("id", id)
                    .hasFieldOrPropertyWithValue("name", oldName)
                    .hasFieldOrPropertyWithValue("email", oldEmail);
        }).doesNotThrowAnyException();

        verify(userStorage, Mockito.times(1)).findById(id);
        verify(userStorage, Mockito.never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userStorage, Mockito.times(1)).save(requestCaptor.capture());
        assertThat(requestCaptor.getValue())
                .as("Проверка передаваемого аргумента в метод обновления пользователя")
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", oldName)
                .hasFieldOrPropertyWithValue("email", oldEmail);
        Mockito.verifyNoMoreInteractions(userStorage);
    }
}