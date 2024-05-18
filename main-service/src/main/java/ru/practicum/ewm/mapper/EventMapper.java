package ru.practicum.ewm.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.dto.EventFullDto;
import ru.practicum.ewm.model.event.dto.EventShortDto;
import ru.practicum.ewm.model.event.dto.NewEventDto;
import ru.practicum.ewm.model.event.dto.UpdateEventDto;


@Mapper(componentModel = "spring", uses = {LocationMapper.class, UserMapper.class, CategoryMapper.class})
public interface EventMapper {
    EventShortDto toShortDto(Event event);

    EventFullDto toDto(Event event);

    EventFullDto toDto(Event event, Long views);

    Event fromDto(EventShortDto eventShortDto);

    Event fromDto(NewEventDto newEventDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEvent(UpdateEventDto updateEventDto, @MappingTarget Event event);

}
