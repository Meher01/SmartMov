package com.SmartMov.repository;

import com.SmartMov.entity.Target;
import com.SmartMov.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TargetRepository extends JpaRepository<Target, Long> {

    List<Target> findByUser(User user);

    Optional<Target> findByIdAndUser(Long id, User user);
}