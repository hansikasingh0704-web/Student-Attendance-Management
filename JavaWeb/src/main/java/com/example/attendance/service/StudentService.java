package com.example.attendance.service;

import com.example.attendance.entity.Student;
import com.example.attendance.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepo;

    public StudentService(StudentRepository studentRepo) { this.studentRepo = studentRepo; }

    public List<Student> getAll() { return studentRepo.findAll(); }
    public Student save(Student s) { return studentRepo.save(s); }
    public Optional<Student> findById(Long id) { return studentRepo.findById(id); }
    public void deleteById(Long id) { studentRepo.deleteById(id); }
}
