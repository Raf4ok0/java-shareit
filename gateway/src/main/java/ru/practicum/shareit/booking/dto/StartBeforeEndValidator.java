package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEndValid, StartEndDto> {
    @Override
    public void initialize(StartBeforeEndValid annotation) {
    }

    @Override
    public boolean isValid(StartEndDto bean, ConstraintValidatorContext context) {
        final LocalDateTime start = bean.getStart();
        final LocalDateTime end = bean.getEnd();

        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
