package com.SmartMov.repository;

import com.SmartMov.entity.LearningSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import com.SmartMov.entity.User;
import java.util.List;


public interface LearningSessionRepository
        extends JpaRepository<LearningSession, Long> {

    List<LearningSession> findByTargetUser(User user);

        List<LearningSession> findByTargetIdAndTargetUser(
            Long targetId,
            User user
        );

    boolean existsByTargetIdAndEndedAtIsNull(Long targetId);

    @Query("""
        SELECT s
        FROM LearningSession s
        JOIN s.target t
        WHERE s.id = :sessionId
        AND t.user = :user
    """)
    Optional<LearningSession> findByIdAndTargetUser(
            @Param("sessionId") Long sessionId,
            @Param("user") User user
    );
}
