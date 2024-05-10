package ru.practicum.stats.mapping;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    EndpointHitDto toDto(EndpointHit endpointHit);

    EndpointHit fromDto(EndpointHitDto endpointHitDto);
}
