package ru.practicum.ewm.service.request.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.EventNotModifiableException;
import ru.practicum.ewm.exception.NotAuthorizedException;
import ru.practicum.ewm.exception.NotFoundDataException;
import ru.practicum.ewm.exception.RequestAlreadyExistsException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.Request;
import ru.practicum.ewm.model.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.request.RequestService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.model.request.Request.State.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        List<Request> requests = requestRepository.findAllByRequesterId(user.getId());

        return requests.stream()
                .map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
         User user = userRepository.findById(userId).orElseThrow(() ->
                 new NotFoundDataException("User with id " + userId + " not found"));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id " + eventId + " not found"));

        checkIfUserCanMakeRequest(userId, eventId, event);
        checkIfParticipationRequestExists(userId, eventId);
        checkIfEventIsNotPublished(event, userId);

        Request request = createRequest(user, event);
        Request savedRequest = requestRepository.save(request);
        log.info("Request created: " + savedRequest);
        return requestMapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id " + userId + " not found"));
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundDataException("Request with id " + requestId + " not found"));

        checkIfUserCanCancelParticipationRequest(userId, request);
        request.setState(CANCELED);
        requestRepository.save(request);

        return requestMapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id " + userId + " not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id " + eventId + " not found"));
        checkIfUserIsEventInitiator(userId, event);
        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsByUserIdAndEventId(Long userId, Long eventId, EventRequestStatusUpdateRequest requests) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id " + userId + " not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id " + eventId + " not found"));

        long participantLimit = checkParticipantLimit(event);
        final List<Long> requestIds = requests.getRequestIds();
        final List<Request> participationRequests = requestRepository.findAllByIdIn(requestIds);
        int lastConfirmedRequest = 0;
        final EventRequestStatusUpdateResult eventRequestStatusUpdate = new EventRequestStatusUpdateResult();
        lastConfirmedRequest = populateStatusUpdateDto(requests, participationRequests, eventRequestStatusUpdate, lastConfirmedRequest, event, participantLimit);
        rejectRemainingRequestsAfterExceedingParticipantLimit(lastConfirmedRequest, participationRequests, eventRequestStatusUpdate);
        log.info("Participation status for event with id '{}' was updated by user with id '{}'. Update request: '{}'.",
                eventId, userId, requests);
        return eventRequestStatusUpdate;

    }

    private int populateStatusUpdateDto(EventRequestStatusUpdateRequest statusUpdate, List<Request> participationRequests, EventRequestStatusUpdateResult eventRequestStatusUpdate, int lastConfirmedRequest, Event event, long participantLimit) {
        for (Request participationRequest : participationRequests) {
            if (!participationRequest.getState().equals(PENDING)) {
                throw new NotAuthorizedException("For status change request must have status PENDING. Current status: '"
                        + participationRequest.getState() + "'.");
            }
            participationRequest.setState(statusUpdate.getStatus());
            requestRepository.save(participationRequest);
            if (statusUpdate.getStatus().equals(CONFIRMED)) {
                eventRequestStatusUpdate.addConfirmedRequest(requestMapper.toDto(participationRequest));
                lastConfirmedRequest++;
                long incrementedParticipants = event.addParticipant();
                eventRepository.save(event);
                if (incrementedParticipants == participantLimit) {
                    break;
                }
            }
        }
        return lastConfirmedRequest;
    }

    private void rejectRemainingRequestsAfterExceedingParticipantLimit(int lastConfirmedRequest, List<Request> participationRequests, EventRequestStatusUpdateResult eventRequestStatusUpdate) {
        for (int i = lastConfirmedRequest; i < participationRequests.size(); i++) {
            Request participationRequest = participationRequests.get(i);
            participationRequest.setState(REJECTED);
            requestRepository.save(participationRequest);
            eventRequestStatusUpdate.addRejectedRequest(requestMapper.toDto(participationRequest));
        }
    }

    private Request createRequest(User user, Event event) {
        Request request = Request.builder()
                .requester(user)
                .event(event)
                .build();
        if (event.getConfirmedRequests() == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new NotAuthorizedException("Participant limit is exceeded for event with id '" + event.getId() + "'.");
        } else if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            request.setState(CONFIRMED);
            addConfirmedRequestToEvent(event);
        } else {
            request.setState(PENDING);
        }
        return request;
    }

    private void addConfirmedRequestToEvent(Event event) {
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
    }

    private void checkIfParticipationRequestExists(Long userId, Long eventId) {
        Optional<Request> participationRequest = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (participationRequest.isPresent()) {
            throw new RequestAlreadyExistsException("Participation request by user with id '" + userId + "' to event " +
                    "with id '" + eventId + "' already exists.");
        }
    }

    private void checkIfUserCanMakeRequest(Long userId, Long eventId, Event event) {
        if (event.getInitiator().getId() == userId) {
            throw new NotAuthorizedException("Initiator with id '" + userId + "' can not make participation request " +
                    "to his own event with id '" + eventId + "'.");
        }
    }

    private void checkIfEventIsNotPublished(Event event, Long userId) {
        if (!event.getState().equals(Event.State.PUBLISHED)) {
            throw new NotAuthorizedException("User with id '" + userId + "'can not make request to not published event " +
                    "with id '" + event.getId() + "'.");
        }
    }

    private static long checkParticipantLimit(Event event) {
        long participantLimit = event.getParticipantLimit();

        if (participantLimit == 0 || !event.isRequestModeration()) {
            throw new EventNotModifiableException("Event with id '" + event.getId() + "' has no participant limit or " +
                    "pre moderation if off. No need to confirm requests. Participant limit: '" + event.getParticipantLimit()
                    + "', Moderation: '" + event.isRequestModeration() + "'");
        }

        long currentParticipants = event.getConfirmedRequests();

        if (currentParticipants == participantLimit) {
            throw new NotAuthorizedException("The participant limit has been reached");
        }
        return participantLimit;
    }

    private void checkIfUserIsEventInitiator(Long userId, Event event) {
        if (event.getInitiator().getId() != userId) {
            throw new NotAuthorizedException("User with id '" + userId + "' is not an initiator of event with id '" +
                    event.getId() + "'.");
        }
    }

    private void checkIfUserCanCancelParticipationRequest(Long userId, Request request) {
        if (request.getRequester().getId() != userId) {
            throw new NotAuthorizedException("User with id '" + userId + "' is not authorized to cancel participation request with" +
                    "id '" + request.getId() + "'.");
        }
    }
}
