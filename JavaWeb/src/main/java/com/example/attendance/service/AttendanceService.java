package com.example.attendance.service;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepo;

    public AttendanceService(AttendanceRepository attendanceRepo) {
        this.attendanceRepo = attendanceRepo;
    }

    public List<Attendance> getAll() {
        return attendanceRepo.findAll();
    }

    public Attendance save(Attendance a) {
        return attendanceRepo.save(a);
    }

    public Optional<Attendance> findById(Long id) {
        return attendanceRepo.findById(id);
    }

    public List<Attendance> findByDate(LocalDate date) {
        return attendanceRepo.findByAttendanceDate(date);
    }

    public List<Attendance> findByStudentId(Long studentId) {
        return attendanceRepo.findByStudent_StudentId(studentId);
    }

    // ✅ New: check if student already has attendance for this date
    public boolean existsByStudentAndDate(Student student, LocalDate date) {
        return attendanceRepo.existsByStudentAndAttendanceDate(student, date);
    }

    // ✅ New: delete attendance by id
    public void deleteById(Long id) {
        attendanceRepo.deleteById(id);
    }
}
