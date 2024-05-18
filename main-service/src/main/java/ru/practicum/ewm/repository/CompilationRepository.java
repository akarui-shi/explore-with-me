package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.compilation.Compilation;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long>, JpaSpecificationExecutor<Compilation> {

    @Query("select c from Compilation c " +
            "left join fetch c.events e " +
            "left join fetch e.category " +
            "left join fetch e.initiator where c.id = ?1")
    Optional<Compilation> findCompilationWithEventById(Long compId);

}
