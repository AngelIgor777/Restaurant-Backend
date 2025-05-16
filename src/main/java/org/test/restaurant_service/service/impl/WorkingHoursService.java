package org.test.restaurant_service.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.WorkingHours;
import org.test.restaurant_service.repository.WorkingHoursRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkingHoursService {

    private final WorkingHoursRepository repository;

    public WorkingHours save(WorkingHours hours) {
        return repository.save(hours);
    }

    public WorkingHours update(WorkingHours hours) {
        return repository.save(hours);
    }

    public List<WorkingHours> getAll() {
        return repository.findAll();
    }

    public WorkingHours getByDayOfWeek(short day) {
        return repository.findByDayOfWeek(day).orElse(null);
    }

    public boolean isNowInWorkingTime() {
        LocalTime now = LocalTime.now();
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        short dayValue = (short) day.getValue(); // To match DB dayOfWeek (1-7)

        return repository.findByDayOfWeek(dayValue)
                .map(hours -> !now.isBefore(hours.getOpenTime()) && now.isBefore(hours.getCloseTime()))
                .orElse(false);
    }
}
