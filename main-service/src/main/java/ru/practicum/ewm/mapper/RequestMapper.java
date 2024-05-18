package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.model.request.Request;
import ru.practicum.ewm.model.request.dto.ParticipationRequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "state", target = "status")
    ParticipationRequestDto toDto(Request participationRequest);
}
