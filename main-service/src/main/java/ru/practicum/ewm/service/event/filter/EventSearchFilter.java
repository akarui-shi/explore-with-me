package ru.practicum.ewm.service.event.filter;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.model.event.Event;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventSearchFilter {
    private String text;

    private List<Long> categories;

    private Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    private boolean onlyAvailable;

    private Event.SortType sort;
}
