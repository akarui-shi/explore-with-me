package ru.practicum.ewm.model.request.dto;

import lombok.Data;
import ru.practicum.ewm.model.request.Request;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    public static final String REQUESTS_IDS_NULL_ERROR_MESSAGE = "Requests ids must be provided.";
    public static final String STATUS_NULL_ERROR_MESSAGE = "New status for requests must be provided.";

    @NotNull(message =  REQUESTS_IDS_NULL_ERROR_MESSAGE)
    private List<Long> requestIds;

    @NotNull(message = STATUS_NULL_ERROR_MESSAGE)
    private Request.State status;
}
