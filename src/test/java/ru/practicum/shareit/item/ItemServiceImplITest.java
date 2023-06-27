package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplITest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    @Sql(scripts = "classpath:db/clearDb.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUsersItems_WhenDbIsEmpty_ThenReturnEmptyList() {
        UserDto user = userService.createUser(new UserDto(0, "name", "mail@mail.ru"));

        assertThatCode(() -> {
            List<ItemWithBookingDto> requests = itemService.getUsersItems(user.getId(), 0, 20);
            assertThat(requests)
                    .as("Проверка получения пустого списка вещей пользователя при пустой бд")
                    .isNotNull()
                    .asList()
                    .isEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @Sql(scripts = "classpath:db/clearDb.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchItems_WhenDbIsEmpty_ThenReturnEmptyList() {
        assertThatCode(() -> {
            List<ItemDto> requests = itemService.searchItems("text", 0, 20);
            assertThat(requests)
                    .as("Проверка получения пустого списка вещей по поисковой строке при пустой бд")
                    .isNotNull()
                    .asList()
                    .isEmpty();
        }).doesNotThrowAnyException();
    }

    @Test
    @Sql(scripts = "classpath:db/clearDb.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchItems_WhenDbIsNotEmpty_ThenReturnNotEmptyList() {
        UserDto user = userService.createUser(new UserDto(0, "name1", "mail1@mail.ru"));
        ItemDto item1 = itemService.createItem(user.getId(), new ItemDto(0, "name1", "description1",
                true, null));
        ItemDto item2 = itemService.createItem(user.getId(), new ItemDto(0, "name2", "description2",
                true, null));

        assertThatCode(() -> {
            List<ItemDto> requests = itemService.searchItems("text", 0, 20);
            assertThat(requests)
                    .as("Проверка получения пустого списка вещей по поисковой строке, когда нет совпадений")
                    .isNotNull()
                    .asList()
                    .isEmpty();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            List<ItemDto> requests = itemService.searchItems(" ", 0, 20);
            assertThat(requests)
                    .as("Проверка получения пустого списка вещей по пустой поисковой строке")
                    .isNotNull()
                    .asList()
                    .isEmpty();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            List<ItemDto> requests = itemService.searchItems("name", 5, 5);
            assertThat(requests)
                    .as("Проверка получения пустого списка вещей по поисковой строке, когда на странице " +
                            "нет результатов")
                    .isNotNull()
                    .asList()
                    .isEmpty();
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            List<ItemDto> requests = itemService.searchItems("name", 0, 2);
            assertThat(requests)
                    .as("Проверка получения не пустого списка вещей по поисковой строке")
                    .isNotNull()
                    .asList()
                    .hasSize(2)
                    .contains(item1)
                    .contains(item2);
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            List<ItemDto> requests = itemService.searchItems("description", 0, 1);
            assertThat(requests)
                    .isNotNull()
                    .asList()
                    .hasSize(1)
                    .containsAnyOf(item1, item2);
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            List<ItemDto> requests = itemService.searchItems("name", 1, 1);
            assertThat(requests)
                    .isNotNull()
                    .asList()
                    .hasSize(1)
                    .containsAnyOf(item1, item2);
        }).doesNotThrowAnyException();
    }
}