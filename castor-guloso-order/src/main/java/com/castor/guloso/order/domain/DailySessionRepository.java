package com.castor.guloso.order.domain;

import com.castor.guloso.core.domain.DailySession;
import com.castor.guloso.core.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailySessionRepository extends JpaRepository<DailySession, Long> {

    Optional<DailySession> findBySessionDateAndStatus(LocalDate date, SessionStatus status);

    Optional<DailySession> findBySessionDate(LocalDate date);
}
