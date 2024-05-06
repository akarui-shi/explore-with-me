package ru.practicum.stats.mapping;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.model.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {
    ViewStatsDto toDto(ViewStats viewStats);

    ViewStats fromDto(ViewStatsDto viewStatsDto);
}
