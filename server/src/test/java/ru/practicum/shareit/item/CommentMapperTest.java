package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private User user = new User(1L, "user", "mail@google.com");
    private Item item = new Item(1L, "name", "description", true, user, null);

    @Test
    void toCommentDto() {
        Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());
        CommentDto dto = CommentMapper.toCommentDto(comment);
        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
    }

    @Test
    void toComment() {
        CommentDto dto = new CommentDto(1L, "text", user.getName(), LocalDateTime.now());
        Comment comment = CommentMapper.toComment(dto, item, user);
        assertNotNull(comment);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
    }
}
