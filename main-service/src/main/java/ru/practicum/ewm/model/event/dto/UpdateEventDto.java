package ru.practicum.ewm.model.event.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.model.location.dto.LocationDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.model.event.utils.EventUtils.*;

@Data
public class UpdateEventDto {
    public enum State {
        PUBLISH_EVENT,
        REJECT_EVENT,
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }

    @Size(min = EVENT_ANNOTATION_MIN_LENGTH, max = EVENT_ANNOTATION_MAX_LENGTH, message = EVENT_ANNOTATION_LENGTH_ERROR_MESSAGE)
    private String annotation;

    @JsonAlias("category")
    private Long categoryId;

    @Size(min = EVENT_DESCRIPTION_MIN_LENGTH, max = EVENT_DESCRIPTION_MAX_LENGTH, message = EVENT_DESCRIPTION_LENGTH_ERROR_MESSAGE)
    private String description;

    @FutureOrPresent(message = EVENT_DATE_PAST_ERROR_MESSAGE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = EVENT_DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = EVENT_PARTICIPANT_LIMIT_NEGATIVE_ERROR_MESSAGE)
    private Integer participantLimit;

    private Boolean requestModeration;

    private State stateAction;

    @Size(min = EVENT_TITLE_MIN_LENGTH, max = EVENT_TITLE_MAX_LENGTH, message = EVENT_TITLE_LENGTH_ERROR_MESSAGE)
    private String title;
}
