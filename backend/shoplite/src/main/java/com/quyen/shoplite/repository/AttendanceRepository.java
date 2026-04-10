package com.quyen.shoplite.repository;

import com.quyen.shoplite.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByEmployee_IdAndWorkingDay(Integer employeeId, LocalDate workingDay);
    Optional<Attendance> findTopByEmployee_IdOrderByCheckInDesc(Integer employeeId);
}
