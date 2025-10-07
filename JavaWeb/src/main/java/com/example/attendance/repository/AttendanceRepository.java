package com.example.attendance.repository;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByAttendanceDate(LocalDate date);
    List<Attendance> findByStudent_StudentId(Long studentId);

    // ✅ New method to check for duplicates
    boolean existsByStudentAndAttendanceDate(Student student, LocalDate attendanceDate);
}
