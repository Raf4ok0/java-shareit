package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto() {
        Item item = new Item(1, "name", "description", true, new User(), null);
        ItemDto dto = ItemMapper.toItemDto(item);
        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getDescription(), dto.getDescription());
    }

    @Test
    void toItem() {
        User user = new User();
        ItemDto dto = new ItemDto(0L, "name", "description", true, null);
        Item item = ItemMapper.toItem(dto, user, null);
        assertNotNull(item);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getDescription(), dto.getDescription());
    }

}
