package ru.practicum.ewm.model.compilation.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class NewCompilationDto {

    public static final String COMPLICATION_TITLE_EMPTY_ERROR_MESSAGE = "Compilation title cannot be empty";

    public static final int COMPLICATION_TITLE_MIN_LENGTH = 1;
    public static final int COMPLICATION_TITLE_MAX_LENGTH = 50;
    public static final String COMPLICATION_TITLE_LENGTH_ERROR_MESSAGE = "Compilation title length must be between " +
            COMPLICATION_TITLE_MIN_LENGTH + " and " + COMPLICATION_TITLE_MAX_LENGTH + ".";

    private List<Long> events;

    private Boolean pinned;

    @NotBlank(message = COMPLICATION_TITLE_EMPTY_ERROR_MESSAGE)
    @Size(min = COMPLICATION_TITLE_MIN_LENGTH, max = COMPLICATION_TITLE_MAX_LENGTH, message = COMPLICATION_TITLE_LENGTH_ERROR_MESSAGE)
    private String title;
}
