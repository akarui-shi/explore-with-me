package ru.practicum.ewm.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.model.compilation.Compilation;
import ru.practicum.ewm.model.compilation.dto.CompilationDto;
import ru.practicum.ewm.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.model.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {
    CompilationDto toDto(Compilation compilation);

    Compilation fromDto(CompilationDto compilationDto);

    @Mapping(target = "events", source = "events")
    Compilation fromDto(NewCompilationDto newCompilationDto, List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation updateCompilation(@MappingTarget Compilation compilation,
                                  UpdateCompilationDto updateCompilation,
                                  List<Event> events);
}
