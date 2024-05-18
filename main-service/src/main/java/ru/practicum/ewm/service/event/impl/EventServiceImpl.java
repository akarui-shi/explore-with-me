package ru.practicum.ewm.service.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.EventNotModifiableException;
import ru.practicum.ewm.exception.IncorrectDateRangeException;
import ru.practicum.ewm.exception.NotAuthorizedException;
import ru.practicum.ewm.exception.NotFoundDataException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.dto.EventFullDto;
import ru.practicum.ewm.model.event.dto.EventShortDto;
import ru.practicum.ewm.model.event.dto.NewEventDto;
import ru.practicum.ewm.model.event.dto.UpdateEventDto;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.event.filter.EventAdminSearchFilter;
import ru.practicum.ewm.service.event.filter.EventSearchFilter;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.service.event.specification.EventSpecification.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final Environment environment;
    private final EventMapper eventMapper;
    private final StatsClient statsClient = new StatsClient("http://localhost:9090");

    @Override
    public List<EventShortDto> getEvents(EventSearchFilter searchFilter, int from, int size, HttpServletRequest request) {
        validateDateRange(searchFilter);

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Specification<Event>> specifications = eventSearchFilterToSpecifications(searchFilter);
        List<Event> events = eventRepository.findAll(specifications.stream().reduce(Specification::and).orElse(null),
                pageRequest).getContent();

        List<EventShortDto> eventShortDtos = events.stream().map(eventMapper::toShortDto).collect(Collectors.toList());
        loadShortEventsViewsNumber(eventShortDtos);

        if (searchFilter.getSort() != null && searchFilter.getSort().equals(Event.SortType.VIEWS)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews));
        }

        sendStats(request);

        return events.stream().map(eventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id '" + eventId + "' not found."));

        if (!event.getState().equals(Event.State.PUBLISHED)) {
            throw new NotFoundDataException("Event with id '" + eventId + "' is not published. State: '" + event.getState() + "'");
        }

        long eventViewsNumbers = getEventViewsNumber(eventId);
        sendStats(request);

        event.setViews(eventViewsNumbers);
        eventRepository.save(event);
        log.info("getEvent: {}", event);
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventFullDto> getEventsByUserId(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);

        log.info("getEventsByUserId: {}", events);
        return events.stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id '" + userId + "' not found."));
        Category category = categoryRepository.findById(newEventDto.getCategoryId()).orElseThrow(() ->
                new NotFoundDataException("Category with id '" + newEventDto.getCategoryId() + "' not found."));
        Location location = locationRepository.save(newEventDto.getLocation());

        Event event = eventMapper.fromDto(newEventDto);
        event.setInitiator(user);
        event.setLocation(location);
        event.setCategory(category);
        event.setState(Event.State.PENDING);

        Event savedEvent = eventRepository.save(event);

        log.info("Event created: {}", savedEvent);
        return eventMapper.toDto(event);
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id '" + userId + "' not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id '" + eventId + "' not found."));
        if (event.getInitiator().getId() != userId) {
            throw new NotAuthorizedException("User with id '" + userId + "' is not an initiator of event with id '" +
                    event.getId() + "'.");
        }
        return eventMapper.toDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto event) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id '" + userId + "' not found."));

        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id '" + eventId + "' not found."));

        checkEventIsPublished(eventToUpdate);
        changeStateIfNeeded(event, eventToUpdate);
        eventMapper.updateEvent(event, eventToUpdate);

        Event savedEvent = eventRepository.save(eventToUpdate);
        log.info("Event updated: {}", savedEvent);

        long eventViewsNumbers = getEventViewsNumber(eventId);

        return eventMapper.toDto(savedEvent, eventViewsNumbers);
    }

    @Override
    public List<EventFullDto> getEvents(EventAdminSearchFilter searchFilter, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Specification<Event>> specifications = eventAdminSearchFilterToSpecifications(searchFilter);
        List<Event> events = eventRepository.findAll(specifications.stream().reduce(Specification::and).orElse(null),
                pageRequest).getContent();
        log.info("Requesting full events info by admin  with filter '{}'. List size '{}'.", searchFilter, events.size());
        return events.stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id '" + eventId + "' not found."));
        eventMapper.updateEvent(updateEventDto, event);
        updateEventState(updateEventDto.getStateAction(), event);
        Event savedEvent = eventRepository.save(event);
        log.info("Event updated: {}", savedEvent);

        return eventMapper.toDto(savedEvent);
    }

    private void updateEventState(UpdateEventDto.State stateAction, Event event) {
        if (stateAction == null) {
            return;
        }
        switch (stateAction) {
            case PUBLISH_EVENT:
                checkIfEventIsCanceled(event);
                checkIfEventIsAlreadyPublished(event);
                event.setState(Event.State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case REJECT_EVENT:
                checkIfEventIsAlreadyPublished(event);
                event.setState(Event.State.CANCELED);
                break;
        }
    }

    private void checkIfEventIsCanceled(Event event) {
        if (event.getState().equals(Event.State.CANCELED)) {
            throw new NotAuthorizedException("Can not publish canceled event.");
        }
    }

    private void checkIfEventIsAlreadyPublished(Event event) {
        if (event.getState().equals(Event.State.PUBLISHED)) {
            throw new NotAuthorizedException("Event is already published.");
        }
    }

    private void changeStateIfNeeded(UpdateEventDto updateEvent, Event eventToUpdate) {
        if (updateEvent.getStateAction() == null) {
            return;
        }
        switch (updateEvent.getStateAction()) {
            case CANCEL_REVIEW:
                eventToUpdate.setState(Event.State.CANCELED);
                break;
            case SEND_TO_REVIEW:
                eventToUpdate.setState(Event.State.PENDING);
                break;
        }
    }

    private void checkEventIsPublished(Event event) {
        if (event.getState().equals(Event.State.PUBLISHED)) {
            throw new EventNotModifiableException("Published event with id '" + event.getId() + "' can not be modified.");
        }
    }

    public Long getEventViewsNumber(Long eventId) {
        Map<Long, Long> eventViews = getViewForEvents(List.of(eventId));
        return eventViews.get(eventId);
    }

    public void loadShortEventsViewsNumber(List<EventShortDto> dtos) {
        Map<Long, Long> eventsViews = getViewForEvents(dtos.stream().map(EventShortDto::getId).collect(Collectors.toList()));
        for (EventShortDto dto : dtos) {
            dto.setViews(eventsViews.get(dto.getId()));
        }
    }

    private void sendStats(HttpServletRequest request) {
        log.info("Send hit for {}", request.getRequestURI());
        statsClient.saveHitSync(environment.getProperty("application.name"),
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
    }

    private Map<Long, Long> getViewForEvents(List<Long> eventsIds) {
        List<String> uris = eventsIds.stream().map(id -> "/events/" + id).collect(Collectors.toList());
        List<ViewStatsDto> viewsStats = statsClient.getStats(null, null, uris, true);

        Map<Long, Long> eventsViews = viewsStats.stream()
                .collect(Collectors.toMap(
                        viewStats -> Long.parseLong(viewStats.getUri().substring(viewStats.getUri().lastIndexOf("/") + 1)),
                        ViewStatsDto::getHits));

        for (Long eventId : eventsIds) {
            if (!eventsViews.containsKey(eventId)) {
                eventsViews.put(eventId, 0L);
            }
        }
        return eventsViews;
    }

    private void validateDateRange(EventSearchFilter searchFilter) {
        if (searchFilter.getRangeStart() != null && searchFilter.getRangeEnd() != null) {
            if (searchFilter.getRangeStart().isAfter(searchFilter.getRangeEnd())) {
                throw new IncorrectDateRangeException("Wrong date range.");
            }
        }
    }

    private List<Specification<Event>> eventSearchFilterToSpecifications(EventSearchFilter searchFilter) {
        List<Specification<Event>> resultSpecification = new ArrayList<>();
        resultSpecification.add(eventStatusEquals(Event.State.PUBLISHED));
        resultSpecification.add(textInAnnotationOrDescriptionIgnoreCase(searchFilter.getText()));
        resultSpecification.add(categoriesIdIn(searchFilter.getCategories()));
        resultSpecification.add(isPaid(searchFilter.getPaid()));
        resultSpecification.add(eventDateInRange(searchFilter.getRangeStart(), searchFilter.getRangeEnd()));
        resultSpecification.add(isAvailable(searchFilter.isOnlyAvailable()));
        return resultSpecification.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<Specification<Event>> eventAdminSearchFilterToSpecifications(EventAdminSearchFilter searchFilter) {
        List<Specification<Event>> resultSpecification = new ArrayList<>();
        resultSpecification.add(eventStatusIn(searchFilter.getStates()));
        resultSpecification.add(initiatorIdIn(searchFilter.getUsers()));
        resultSpecification.add(categoriesIdIn(searchFilter.getCategories()));
        resultSpecification.add(eventDateInRange(searchFilter.getRangeStart(), searchFilter.getRangeEnd()));
        resultSpecification.add(isAvailable(searchFilter.isOnlyAvailable()));
        return resultSpecification.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
