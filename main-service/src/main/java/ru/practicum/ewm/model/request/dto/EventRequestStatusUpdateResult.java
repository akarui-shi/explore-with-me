package ru.practicum.ewm.model.request.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

    public void addConfirmedRequest(ParticipationRequestDto requestDto) {
        confirmedRequests.add(requestDto);
    }

    public void addRejectedRequest(ParticipationRequestDto requestDto) {
        rejectedRequests.add(requestDto);
    }


}
