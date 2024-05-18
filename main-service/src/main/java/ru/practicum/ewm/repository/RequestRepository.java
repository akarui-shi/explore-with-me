package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.request.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long>, JpaSpecificationExecutor<Request> {
    List<Request> findAllByRequesterId(Long id);

    Optional<Request> findByRequesterIdAndEventId(Long id, Long eventId);

    @Query("SELECT p FROM Request p JOIN FETCH p.requester r JOIN FETCH p.event e WHERE p.id IN ?1")
    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByEventId(Long id);
}
