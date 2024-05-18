package ru.practicum.ewm.service.event;

import ru.practicum.ewm.model.event.dto.*;
import ru.practicum.ewm.service.event.filter.EventAdminSearchFilter;
import ru.practicum.ewm.service.event.filter.EventSearchFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEvents(EventSearchFilter searchFilter, int from, int size, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);

    List<EventFullDto> getEventsByUserId(Long userId, int from, int size);

    EventFullDto addEvent(Long userId, NewEventDto event);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto event);

    List<EventFullDto> getEvents(EventAdminSearchFilter eventAdminSearchFilter, int from, int size);

    EventFullDto updateEvent(Long eventId, UpdateEventDto event);

    void loadShortEventsViewsNumber(List<EventShortDto> dtos);

}
