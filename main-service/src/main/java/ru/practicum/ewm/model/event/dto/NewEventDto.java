package ru.practicum.ewm.model.event.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.model.location.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.model.event.utils.EventUtils.*;

@Data
public class NewEventDto {

    @NotBlank(message = EVENT_ANNOTATION_EMPTY_ERROR_MESSAGE)
    @Size(min = EVENT_ANNOTATION_MIN_LENGTH, max = EVENT_ANNOTATION_MAX_LENGTH, message = EVENT_ANNOTATION_LENGTH_ERROR_MESSAGE)
    private String annotation;

    @NotNull(message = EVENT_CATEGORY_EMPTY_ERROR_MESSAGE)
    @JsonAlias("category")
    private Long categoryId;

    @NotBlank(message = EVENT_DESCRIPTION_EMPTY_ERROR_MESSAGE)
    @Size(min = EVENT_DESCRIPTION_MIN_LENGTH, max = EVENT_DESCRIPTION_MAX_LENGTH, message = EVENT_DESCRIPTION_LENGTH_ERROR_MESSAGE)
    private String description;

    @NotNull(message = EVENT_DATE_EMPTY_ERROR_MESSAGE)
    @FutureOrPresent(message = EVENT_DATE_PAST_ERROR_MESSAGE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = EVENT_DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull(message = EVENT_LOCATION_EMPTY_ERROR_MESSAGE)
    private Location location;

    private boolean paid;

    @PositiveOrZero(message = EVENT_PARTICIPANT_LIMIT_NEGATIVE_ERROR_MESSAGE)
    private int participantLimit;

    private boolean requestModeration = true;

    @NotBlank(message = EVENT_TITLE_EMPTY_ERROR_MESSAGE)
    @Size(min = EVENT_TITLE_MIN_LENGTH, max = EVENT_TITLE_MAX_LENGTH, message = EVENT_TITLE_LENGTH_ERROR_MESSAGE)
    private String title;

}
