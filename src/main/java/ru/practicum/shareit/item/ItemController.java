package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.utils.Constants;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_WITH_USER_ID_NAME = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId,
                              @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Попытка создать вещь пользователем с id = {}", userId);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId,
                              @Validated({Update.class}) @RequestBody ItemDto itemDto, @PathVariable long itemId) {
        log.info("Попытка обновить вещь с id = {} пользователем с id = {}", itemId, userId);
        itemDto.setId(itemId);

        return itemService.updateItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItem(@RequestHeader(HEADER_WITH_USER_ID_NAME) long userId,
                                      @PathVariable @Positive long itemId) {
        log.info("Попытка получить вешь с id = {}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingDto> getUsersItems(@RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId,
                                                  @RequestParam(defaultValue = Constants.DEFAULT_START_PAGE) @Min(0) int from,
                                                  @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) @Positive int size) {

        log.info("Попытка получить все вещи пользователя с id = {}", userId);
        return itemService.getUsersItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = Constants.DEFAULT_START_PAGE) @Min(0) int from,
                                     @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) @Positive int size) {

        log.info("Попытка найти вещи по поисковой строке: {}", text);

        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId,
                                    @PathVariable @Positive long itemId, @Valid @RequestBody CommentDto commentDto) {
        log.info("Попытка оставить отзыв на вещь с id = {} пользователем с id = {}", itemId, userId);
        return itemService.createComment(commentDto, userId, itemId);
    }

}
