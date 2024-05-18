package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.location.dto.LocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location fromDto(LocationDto locationDto);

    LocationDto toDto(Location location);
}
