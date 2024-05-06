package ru.practicum.stats.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.mapping.EndpointHitMapper;
import ru.practicum.stats.mapping.ViewStatsMapper;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.repository.StatsRepository;
import ru.practicum.stats.service.exception.EntityHasNotSavedException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatsServiceImplTest {

    @Spy
    EndpointHitMapper hitMapper = Mappers.getMapper(EndpointHitMapper.class);

    @Spy
    ViewStatsMapper viewStatsMapper = Mappers.getMapper(ViewStatsMapper.class);
    @Mock
    StatsRepository statsRepository;

    @InjectMocks
    StatsServiceImpl statsService;

    @Test
    void saveHit_whenDatabaseError_thenThrowException() {
        EndpointHitDto newEndpointHit = EndpointHitDto.builder().app("app").uri("/service").ip("192.0.0.1")
                .timestamp(LocalDateTime.of(2024, 4, 1, 12, 0)).build();

        when(statsRepository.save(any(EndpointHit.class))).thenThrow(new DataIntegrityViolationException("Database error."));

        assertThrows(EntityHasNotSavedException.class, () -> statsService.saveHit(newEndpointHit));
    }

    @Test
    void getStats_whenInputValid_thenReturnListOfDto() {
        LocalDateTime start = LocalDateTime.of(2024, 4, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 4, 1, 23, 59);

        ViewStats stats = ViewStats.builder().app("app").uri("/service").hits(10L).build();

        when(statsRepository.getStats(start, end)).thenReturn(List.of(stats));

        List<ViewStatsDto> actualStatsList = statsService.getStats(start, end, null, false);

        assertEquals(actualStatsList, List.of(viewStatsMapper.toDto(stats)));
        verify(statsRepository).getStats(start, end);
    }


}