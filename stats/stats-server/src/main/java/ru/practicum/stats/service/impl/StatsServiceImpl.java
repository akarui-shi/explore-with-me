package ru.practicum.stats.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.mapping.EndpointHitMapper;
import ru.practicum.stats.mapping.ViewStatsMapper;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.repository.StatsRepository;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.service.exception.EntityHasNotSavedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Override
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        try {
            EndpointHit hit = statsRepository.save(endpointHitMapper.fromDto(endpointHitDto));
            log.info("Saved hit: " + hit);
            return endpointHitMapper.toDto(hit);
        } catch (DataIntegrityViolationException e) {
            throw new EntityHasNotSavedException("Entity has not saved: " + endpointHitDto);
        }
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> stats = unique ? statsRepository.getStatsUniqueWithFilters(start, end, uris) :
                statsRepository.getStatsWithFilters(start, end, uris);
        return stats.stream().map(viewStatsMapper::toDto).collect(Collectors.toList());
    }
}
