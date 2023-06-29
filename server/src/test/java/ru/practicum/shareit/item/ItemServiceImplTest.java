package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.Constants;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private UserStorage userStorage;
    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemRequestStorage itemRequestStorage;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    @Test
    void createItem_WhenUserDoesNotExist_ThenThrowsNotFoundException() {
        ItemDto itemDto = new ItemDto(0, "name", "description", true, null);
        long userId = 1;
        when(userStorage.existsById(userId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка создания вещи пользователем, id которого нет в базе")
                .isThrownBy(() -> itemService.createItem(userId, itemDto))
                .withMessage(String.format(Constants.USER_NOT_FOUND_MESSAGE, userId));

        verify(userStorage, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoInteractions(itemStorage);
    }

    @Test
    void createItem_WhenRequestWithSuchIdDoesNotExist_ThenTrowsNotFoundException() {
        long requestId = 1;
        long userId = 1;
        ItemDto itemDto = new ItemDto(1, "name", "description", true, requestId);
        when(userStorage.existsById(userId)).thenReturn(true);
        when(itemRequestStorage.existsById(requestId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка создания вещи по запросу, id которого нет в базе")
                .isThrownBy(() -> itemService.createItem(userId, itemDto))
                .withMessage(String.format(Constants.REQUEST_NOT_FOUND_MESSAGE, requestId));

        verify(itemRequestStorage, Mockito.times(1)).existsById(requestId);
        Mockito.verifyNoInteractions(itemStorage);
    }

    @Test
    void createItem_WhenItemDtoDoesNotContainRequestId_ThenItemCreated() {
        long userId = 1;
        ItemDto inputItemDto = new ItemDto(1, "name", "description", true, null);
        ItemDto expectedItemDto = new ItemDto(1, "name", "description", true, null);
        Item itemToSave = new Item(0L, "name", "description", true, new User(userId),
                null);
        when(userStorage.existsById(userId)).thenReturn(true);
        when(itemStorage.save(itemToSave))
                .thenReturn(new Item(1L, "name", "description", true, new User(1L,
                        "name", "mail@mail.ru"), null));
        when(userStorage.getReferenceById(userId)).thenReturn(new User(1L, null, null));

        assertThatCode(() -> {
            ItemDto actualDto = itemService.createItem(userId, inputItemDto);
            assertThat(actualDto)
                    .as("Проверка создания вещи при корректных входных данных, когда поле запроса не заполнено")
                    .isNotNull()
                    .isEqualTo(expectedItemDto);
        }).doesNotThrowAnyException();

        verify(itemStorage, Mockito.times(1)).save(itemArgumentCaptor.capture());
        assertThat(itemArgumentCaptor.getValue())
                .as("Проверка передаваемого аргумента в метод сохранения вещи")
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", itemToSave.getId())
                .hasFieldOrPropertyWithValue("name", itemToSave.getName())
                .hasFieldOrPropertyWithValue("description", itemToSave.getDescription())
                .hasFieldOrPropertyWithValue("available", itemToSave.isAvailable())
                .hasFieldOrPropertyWithValue("user", itemToSave.getUser())
                .hasFieldOrPropertyWithValue("itemRequest", itemToSave.getItemRequest());
        Mockito.verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void updateItem_WhenItemDoesNotExists_ThenThrowsNotFoundException() {
        long itemId = 1;
        long userId = 1;
        ItemDto inputItemDto = new ItemDto(itemId, "new_name", "new_description", true,
                null);
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка обновления вещи по id, которого нет в базе")
                .isThrownBy(() -> itemService.updateItem(userId, inputItemDto))
                .withMessage(String.format(Constants.ITEM_NOT_FOUND_MESSAGE, itemId));

        verify(itemStorage, Mockito.times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void updateItem_WhenUserNotOwnerOfTheItem_ThenThrowsNotFoundException() {
        long itemId = 1;
        long userId = 1;
        ItemDto inputItemDto = new ItemDto(itemId, "new_name", "new_description", true,
                null);
        Item oldItem = new Item(itemId, "old_name", "old_description", true,
                new User(userId + 1), null);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(oldItem));

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка обновления вещи, id владельца которой не совпадает с переданным id")
                .isThrownBy(() -> itemService.updateItem(userId, inputItemDto))
                .withMessage(String.format(Constants.USERS_ITEM_NOT_FOUND_MESSAGE, itemId, userId));

        verify(itemStorage, Mockito.times(1)).findById(itemId);
        Mockito.verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void updateItem_WhenDtoContainsNullFieldsAndFieldsNotAvailableToUpdate_ThenUpdatedWithOnlyCorrectFields() {
        String oldName = "old_name";
        String oldDescription = "old_description";
        boolean oldAvailable = true;
        long itemId = 1;
        long userId = 1;
        ItemDto inputItemDto = new ItemDto(itemId, null, null, null, 1L);
        ItemDto expectedItemDto = new ItemDto(itemId, oldName, oldDescription, oldAvailable, null);
        Item oldItem = new Item(itemId, oldName, oldDescription, oldAvailable, new User(userId), null);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemStorage.save(oldItem)).thenReturn(oldItem);

        assertThatCode(() -> {
            ItemDto updatedItem = itemService.updateItem(userId, inputItemDto);
            assertThat(updatedItem)
                    .as("Проверка обновления вещи, когда поля для обновления null")
                    .isNotNull()
                    .isEqualTo(expectedItemDto);
        }).doesNotThrowAnyException();

        verify(itemStorage, Mockito.times(1)).save(itemArgumentCaptor.capture());
        assertThat(itemArgumentCaptor.getValue())
                .as("Проверка передаваемого аргумента в метод обновления вещи")
                .hasFieldOrPropertyWithValue("id", itemId)
                .hasFieldOrPropertyWithValue("name", oldName)
                .hasFieldOrPropertyWithValue("description", oldDescription)
                .hasFieldOrPropertyWithValue("available", oldAvailable)
                .hasFieldOrPropertyWithValue("itemRequest", null);
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void updateItem_WhenDtoContainsEmptyFields_ThenUpdatedWithOnlyCorrectFields() {
        String oldName = "old_name";
        String oldDescription = "old_description";
        boolean newAvailable = false;
        long itemId = 1;
        long userId = 1;
        ItemDto inputItemDto = new ItemDto(itemId, "  ", "  ", false, 1L);
        ItemDto expectedItemDto = new ItemDto(itemId, oldName, oldDescription, newAvailable, null);
        Item oldItem = new Item(itemId, oldName, oldDescription, true, new User(userId), null);
        Item updatedItem = new Item(itemId, oldName, oldDescription, newAvailable, new User(userId), null);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemStorage.save(oldItem)).thenReturn(updatedItem);

        assertThatCode(() -> {
            ItemDto actualItem = itemService.updateItem(userId, inputItemDto);
            assertThat(actualItem)
                    .as("Проверка обновления вещи, когда все поля для обновления пустые кроме available")
                    .isNotNull()
                    .isEqualTo(expectedItemDto);
        }).doesNotThrowAnyException();

        verify(itemStorage, Mockito.times(1)).save(itemArgumentCaptor.capture());
        assertThat(itemArgumentCaptor.getValue())
                .as("Проверка передаваемого аргумента в метод обновления вещи")
                .hasFieldOrPropertyWithValue("id", itemId)
                .hasFieldOrPropertyWithValue("name", oldName)
                .hasFieldOrPropertyWithValue("description", oldDescription)
                .hasFieldOrPropertyWithValue("available", newAvailable);
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void updateItem_WhenDtoContainsNotEmptyFields_ThenUpdatedWithOnlyCorrectFields() {
        String newName = "new_name";
        String newDescription = "new_description";
        long itemId = 1;
        long userId = 1;
        ItemDto inputItemDto = new ItemDto(itemId, newName, newDescription, false, null);
        ItemDto expectedItemDto = new ItemDto(itemId, newName, newDescription, true, 1L);
        Item oldItem = new Item(itemId, "old_name", "old_description", true, new User(userId),
                new ItemRequest(1L, null, null, null));
        Item updatedItem = new Item(itemId, newName, newDescription, true, new User(userId),
                new ItemRequest(1L, null, null, null));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemStorage.save(oldItem)).thenReturn(updatedItem);

        assertThatCode(() -> {
            ItemDto actualItem = itemService.updateItem(userId, inputItemDto);
            assertThat(actualItem)
                    .as("Проверка обновления вещи, когда не пустые и не null название и описание вещи")
                    .isNotNull()
                    .isEqualTo(expectedItemDto);
        }).doesNotThrowAnyException();

        verify(itemStorage, Mockito.times(1)).save(itemArgumentCaptor.capture());
        assertThat(itemArgumentCaptor.getValue())
                .as("Проверка передаваемого аргумента в метод обновления вещи")
                .hasFieldOrPropertyWithValue("id", itemId)
                .hasFieldOrPropertyWithValue("name", newName)
                .hasFieldOrPropertyWithValue("description", newDescription)
                .hasFieldOrPropertyWithValue("itemRequest",
                        new ItemRequest(1L, null, null, null));
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void getItem_WhenUserDoesNotExist_ThenThrowsNotFoundException() {
        long itemId = 1;
        long userId = 1;
        when(userStorage.existsById(userId)).thenReturn(false);

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка получения вещи, когда id пользователя не найден в базе")
                .isThrownBy(() -> itemService.getItem(userId, itemId))
                .withMessage(String.format(Constants.USER_NOT_FOUND_MESSAGE, userId));
        verify(userStorage, Mockito.times(1)).existsById(userId);
        Mockito.verifyNoInteractions(itemStorage);
    }

    @Test
    void getItem_WhenItemDoesNotExist_ThenThrowsNotFoundException() {
        long itemId = 1;
        long userId = 1;
        when(userStorage.existsById(userId)).thenReturn(true);
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .as("Проверка получения вещи, когда id вещи не найден в базе")
                .isThrownBy(() -> itemService.getItem(userId, itemId))
                .withMessage(String.format(Constants.ITEM_NOT_FOUND_MESSAGE, itemId));
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verifyNoMoreInteractions(itemStorage);
    }
}