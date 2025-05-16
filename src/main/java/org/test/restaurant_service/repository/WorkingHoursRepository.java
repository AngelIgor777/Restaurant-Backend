package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.WorkingHours;

import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Integer> {
    Optional<WorkingHours> findByDayOfWeek(short dayOfWeek);
}
