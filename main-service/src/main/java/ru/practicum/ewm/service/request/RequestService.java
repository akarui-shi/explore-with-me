package ru.practicum.ewm.service.request;

import ru.practicum.ewm.model.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsByUserIdAndEventId(Long userId, Long eventId, EventRequestStatusUpdateRequest requests);
}
