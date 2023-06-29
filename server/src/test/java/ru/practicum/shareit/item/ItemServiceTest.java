package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    private ItemDto post;
    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        post = new ItemDto(0, "name", "description", true, null);
        user1 = userService.createUser(new UserDto(0, "name", "mail@google.com"));
        user2 = userService.createUser(new UserDto(0, "name2", "mail2@google.com"));
    }

    @Test
    void createItem() {
        ItemDto dto = itemService.createItem(user1.getId(), post);
        assertNotNull(dto);
        assertEquals(post.getDescription(), dto.getDescription());
    }

    @Test
    void getItem() {
        ItemDto dto = itemService.createItem(user1.getId(), post);
        ItemWithBookingDto res = itemService.getItem(dto.getId(), user1.getId());
        assertNotNull(res);
    }

    @Test
    void getItemsByOwner() {
        ItemDto dto = itemService.createItem(user1.getId(), post);
        List<ItemWithBookingDto> res = itemService.getUsersItems(user1.getId(), 0, 100);
        assertNotNull(res);
        assertEquals(1, res.size());
    }

    @Test
    void search() {
        ItemDto dto = itemService.createItem(user1.getId(), post);
        List<ItemDto> res = itemService.searchItems("escr", 0, 100);
        assertNotNull(res);
        ;
        assertEquals(1, res.size());
        assertEquals(dto, res.get(0));
        res = itemService.searchItems("", 0, 100);
        assertEquals(0, res.size());
    }

    @Test
    void createComment() {
        LocalDateTime from = LocalDateTime.now().minusHours(1);
        LocalDateTime till = from.plusMinutes(10);
        ItemDto dto = itemService.createItem(user1.getId(), post);
        BookingDto post = bookingService.createBooking(
                new BookingCreationDto(dto.getId(), from, till),
                user2.getId());
        bookingService.setBookingStatus(user1.getId(), post.getId(), true);
        CommentDto commentPost = new CommentDto(0L, "comment", null, null);
        CommentDto comment = itemService.createComment(commentPost, user2.getId(), dto.getId());
        assertEquals("comment", comment.getText());
    }

}
