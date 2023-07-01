package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEndValid {
    String message() default "Start must be before end";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}