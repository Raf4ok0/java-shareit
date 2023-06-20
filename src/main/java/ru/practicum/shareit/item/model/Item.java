package ru.practicum.shareit.item.model;

import lombok.*;
import org.apache.coyote.Request;
import ru.practicum.shareit.user.User;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private Request request;

    public Item(long id, String name, String description, Boolean available, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}

