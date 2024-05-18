package ru.practicum.ewm.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.location.dto.LocationDto;
import ru.practicum.ewm.model.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.ewm.model.event.utils.EventUtils.EVENT_DATE_TIME_FORMAT;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = EVENT_DATE_TIME_FORMAT)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = EVENT_DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDto location;

    private boolean paid;

    private int participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = EVENT_DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    private boolean requestModeration;

    private Event.State state;

    private String title;

    private Long views;

}
