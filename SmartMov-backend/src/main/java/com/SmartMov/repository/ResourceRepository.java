package com.SmartMov.repository;

import com.SmartMov.entity.Resource;
import com.SmartMov.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByTargetIdAndTargetUser(
            Long targetId,
            User user
    );

    List<Resource> findByTargetUser(User user);

    @Query("""
        SELECT r
        FROM Resource r
        JOIN r.target t
        WHERE r.id = :resourceId
        AND t.user = :user
    """)
    Optional<Resource> findByIdAndTargetUser(
            @Param("resourceId") Long resourceId,
            @Param("user") User user
    );
}