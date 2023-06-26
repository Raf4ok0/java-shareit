package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String DEFAULT_SEARCH_VALUE = "ALL";
    private static final String HEADER_WITH_USER_ID_NAME = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingCreationDto bookingDto,
                                    @RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId) {
        log.info("Попытка забронировать вещь с id = {} пользователем с id = {}", bookingDto.getItemId(), userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setBookingStatus(@PathVariable @Positive long bookingId, @RequestParam boolean approved,
                                       @RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId) {
        log.info("Попытка изменить статус бронирования с id = {} пользователем с id = {}", bookingId, userId);
        return bookingService.setBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable @Positive long bookingId,
                                 @RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId) {
        log.info("Попытка получить бронирование по id = {} пользователем с id = {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId,
                                                  @RequestParam(defaultValue = DEFAULT_SEARCH_VALUE) String state) {
        log.info("Попытка получить все бронирования со статусом {} автора бронирований с id = {}", state, userId);
        return bookingService.getBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerId(@RequestHeader(HEADER_WITH_USER_ID_NAME) @Positive long userId,
                                                 @RequestParam(defaultValue = DEFAULT_SEARCH_VALUE) String state) {
        log.info("Попытка получить все бронирования со статусом {} владельца вещей с id = {}", state, userId);
        return bookingService.getBookingsByOwnerId(userId, state);
    }
}
