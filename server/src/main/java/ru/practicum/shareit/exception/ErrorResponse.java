package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    @JsonProperty("error")
    private final String errorMessage;
}
