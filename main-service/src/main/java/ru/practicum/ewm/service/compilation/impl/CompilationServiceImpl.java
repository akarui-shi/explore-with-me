package ru.practicum.ewm.service.compilation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundDataException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.compilation.Compilation;
import ru.practicum.ewm.model.compilation.dto.CompilationDto;
import ru.practicum.ewm.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.model.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.compilation.CompilationService;
import ru.practicum.ewm.service.event.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        List<Specification<Compilation>> specifications = searchFilterToSpecificationList(pinned);
        List<Compilation> compilations = compilationRepository.findAll(specifications.stream()
                .reduce(Specification::and).orElse(null), pageRequest).getContent();

        log.info("get compilations from {} size {} with pinned {} : {}", from, size, pinned, compilations);

        return compilations.stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationsById(Long compId) {
        Compilation compilation = getCompilationWithEvents(compId);
        CompilationDto compilationDto = compilationMapper.toDto(compilation);

        log.info("get compilation by id {} : {}", compId, compilationDto);

        return compilationDto;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = newCompilationDto.getEvents() != null ?
                eventRepository.findAllById(newCompilationDto.getEvents()) : List.of();

        Compilation compilation = compilationMapper.fromDto(newCompilationDto, events);
        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("create compilation: {}", compilation);

        return compilationMapper.toDto(savedCompilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundDataException("Compilation with id " + compId + " not found"));

        log.info("delete compilation: {}", compilation);

        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationDto updateCompilation) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundDataException("Compilation with id " + compId + " not found"));

        List<Event> events = updateCompilation.getEvents() != null ?
                eventRepository.findAllById(updateCompilation.getEvents()) : List.of();

        Compilation savedCompilation = compilationRepository.save(compilationMapper.updateCompilation(compilation,
                updateCompilation, events));

        CompilationDto compilationDto = compilationMapper.toDto(savedCompilation);
        eventService.loadShortEventsViewsNumber(compilationDto.getEvents());
        return compilationDto;

    }

    private Compilation getCompilationWithEvents(Long compId) {
        return compilationRepository.findCompilationWithEventById(compId)
                .orElseThrow(() -> new IllegalArgumentException("Compilation not found"));
    }

    private List<Specification<Compilation>> searchFilterToSpecificationList(Boolean pinned) {
        List<Specification<Compilation>> resultSpecification = new ArrayList<>();
        resultSpecification.add(pinned == null ? null : isPinned(pinned));
        return resultSpecification.stream().filter(Objects::nonNull).collect(Collectors.toList());

    }

    private Specification<Compilation> isPinned(Boolean pinned) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pinned"), pinned);
    }
}
