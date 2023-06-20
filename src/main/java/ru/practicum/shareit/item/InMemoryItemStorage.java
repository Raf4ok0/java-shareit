package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long currentId;

    @Override
    public Item create(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);

        log.info("Создана вещь с id = {}", item.getId());
        return item;
    }

    @Override
    public boolean isItemExist(long userId, long itemId) {
        log.info("Получена информация о существовании вещи с id = {} у пользователя с id = {}", itemId, userId);

        if (items.containsKey(itemId)) {
            return items.get(itemId).getOwner().getId() == userId;
        } else {
            return false;
        }
    }

    @Override
    public boolean isItemExist(long itemId) {
        log.info("Получена информация о существовании вещи с id = {}", itemId);
        return items.containsKey(itemId);
    }

    @Override
    public Item get(long userId, long itemId) {
        Item item = items.get(itemId);
        log.info("Получена вещь с id = {} у пользователя с id = {}", itemId, item.getOwner().getId());

        return item;
    }

    @Override
    public Item get(long itemId) {
        if (items.containsKey(itemId)) {
            log.info("Получена вещь с с id = {}", itemId);
            return items.get(itemId);
        }

        throw new NotFoundException("Вещь с id " + itemId + " не найдена");
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.info("Обновлена информация о вещи с id = {} у пользователя с id = {}", item.getId(), item.getOwner());

        return item;
    }

    @Override
    public List<Item> getUsersItems(long userId) {
        List<Item> usersItems = items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());

        log.info("Получен список вещей пользователя с id = {} длиной {}", userId, usersItems.size());

        return usersItems;
    }

    @Override
    public List<Item> searchItems(String text) {
        String searchText = text.toLowerCase();
        List<Item> searchedItems = items.values().stream()
                .filter(item -> item.isAvailable())
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        log.info("Получен список вещей длиной {}, найденный по поисковой строке: {}", searchedItems.size(), text);
        return searchedItems;
    }
}
