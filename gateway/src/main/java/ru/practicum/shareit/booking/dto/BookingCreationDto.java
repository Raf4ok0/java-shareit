package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreationDto {
    @NotNull
    private Long itemId;
    @NotNull
    @FutureOrPresent
    @StartBeforeEndValid
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
}
