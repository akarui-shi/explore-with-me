package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.dto.*;
import ru.practicum.ewm.service.event.filter.EventAdminSearchFilter;
import ru.practicum.ewm.service.event.filter.EventSearchFilter;
import ru.practicum.ewm.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> getEvents(EventSearchFilter searchFilter,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {
        log.info("GET /events с параметрами from={} size={}", from, size);
        log.info("searchFilter={}", searchFilter);
        return eventService.getEvents(searchFilter, from, size, request);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEvent(@PathVariable long eventId, HttpServletRequest request) {
        log.info("GET /events/{}", eventId);
        return eventService.getEvent(eventId, request);
    }

    @GetMapping("users/{userId}/events")
    public List<EventFullDto> getEventsByUserId(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /users/{}/events", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping("users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto event) {
        log.info("post /users/{}/events", userId);
        return eventService.addEvent(userId, event);
    }

    @GetMapping("users/{userId}/events/{eventId}")
    public EventFullDto getEventByUserIdAndEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("users/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventDto event) {
        log.info("PATCH /users/{}/events/{}", userId, eventId);
        log.info(event.toString());
        return eventService.updateEvent(userId, eventId, event);
    }

    @GetMapping("admin/events")
    public List<EventFullDto> getEvents(EventAdminSearchFilter searchFilter,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /admin/events from {} size {}", from, size);
        return eventService.getEvents(searchFilter, from, size);
    }

    @PatchMapping("admin/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventDto event) {
        log.info("PATCH /admin/events/{}", eventId);
        log.info(event.toString());
        return eventService.updateEvent(eventId, event);
    }
}
