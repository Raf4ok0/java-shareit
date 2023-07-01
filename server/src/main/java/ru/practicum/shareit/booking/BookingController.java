package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.utils.Constants;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String DEFAULT_SEARCH_VALUE = "ALL";
    private static final String HEADER_WITH_USER_ID_NAME = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingCreationDto bookingDto,
                                    @RequestHeader(HEADER_WITH_USER_ID_NAME) long userId) {
        log.info("Попытка забронировать вещь с id = {} пользователем с id = {}", bookingDto.getItemId(), userId);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setBookingStatus(@PathVariable long bookingId, @RequestParam boolean approved,
                                       @RequestHeader(HEADER_WITH_USER_ID_NAME) long userId) {
        log.info("Попытка изменить статус бронирования с id = {} пользователем с id = {}", bookingId, userId);
        return bookingService.setBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader(HEADER_WITH_USER_ID_NAME) long userId) {
        log.info("Попытка получить бронирование по id = {} пользователем с id = {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBookerId(@RequestHeader(HEADER_WITH_USER_ID_NAME) long userId,
                                                  @RequestParam(defaultValue = DEFAULT_SEARCH_VALUE) String state,
                                                  @RequestParam(defaultValue = Constants.DEFAULT_START_PAGE) int from,
                                                  @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        log.info("Попытка получить {} бронирований начиная с {} со статусом {} автора бронирований с id = {}", size,
                from, state, userId);
        return bookingService.getBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerId(@RequestHeader(HEADER_WITH_USER_ID_NAME) long userId,
                                                 @RequestParam(defaultValue = DEFAULT_SEARCH_VALUE) String state,
                                                 @RequestParam(defaultValue = Constants.DEFAULT_START_PAGE) int from,
                                                 @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        log.info("Попытка получить {} бронирований начиная с {} со статусом {} владельца вещей с id = {}", size, from,
                state, userId);
        return bookingService.getBookingsByOwnerId(userId, state, from, size);
    }
}
