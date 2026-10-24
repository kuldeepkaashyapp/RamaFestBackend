package com.rama.fest.ramafest.reposatory;

import com.rama.fest.ramafest.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface EventRepostory extends JpaRepository<Event,Long> {

    @Query("select e from Event e where e.isDeleted = false")
    Page<Event> findAllActive(Pageable pageable);

    @Query("select e from Event e where e.id = :id and e.isDeleted = false")
    java.util.Optional<Event> findActiveById(@Param("id") Long id);

}
