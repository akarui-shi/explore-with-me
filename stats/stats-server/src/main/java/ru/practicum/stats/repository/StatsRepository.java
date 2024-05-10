package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long>, JpaSpecificationExecutor<EndpointHit> {

    @Query("select new ru.practicum.stats.model.ViewStats(hit.app, hit.uri, count(hit.ip))" +
            "from EndpointHit as hit " +
            "where hit.timestamp between :start and :end " +
            "group by hit.app, hit.uri " +
            "order by count(hit.id) desc")
    List<ViewStats> getStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.stats.model.ViewStats(hit.app, hit.uri, count(distinct(hit.ip)))" +
            "from EndpointHit as hit " +
            "where hit.timestamp between :start and :end " +
            "group by hit.app, hit.uri " +
            "order by count(distinct(hit.ip)) desc")
    List<ViewStats> getStatsUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.stats.model.ViewStats(hit.app, hit.uri, count(hit.id))" +
            "from EndpointHit as hit " +
            "where (hit.timestamp between :start and :end) and hit.uri in :uris " +
            "group by hit.app, hit.uri " +
            "order by count(hit.id) desc")
    List<ViewStats> getStatsWithUris(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("uris") List<String> uris);

    @Query("select new ru.practicum.stats.model.ViewStats(hit.app, hit.uri, count(distinct(hit.ip)))" +
            "from EndpointHit as hit " +
            "where (hit.timestamp between :start and :end) and hit.uri in :uris " +
            "group by hit.app, hit.uri " +
            "order by count(distinct(hit.ip)) desc")
    List<ViewStats> getStatsUniqueWithUris(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("uris") List<String> uris);

}
