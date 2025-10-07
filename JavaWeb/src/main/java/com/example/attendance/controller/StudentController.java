package com.example.attendance.controller;

import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) { this.studentService = studentService; }

    private boolean auth(HttpSession session) { return session.getAttribute("admin") != null; }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!auth(session)) return "redirect:/login";
        model.addAttribute("students", studentService.getAll());
        return "students";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session, Model model) {
        if (!auth(session)) return "redirect:/login";
        model.addAttribute("student", new Student());
        return "add-student";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Student student, HttpSession session) {
        if (!auth(session)) return "redirect:/login";
        // when inserting, leave studentId null so DB trigger/sequence sets it
        student.setStudentId(null);
        studentService.save(student);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!auth(session)) return "redirect:/login";
        var opt = studentService.findById(id);
        if (opt.isPresent()) {
            model.addAttribute("student", opt.get());
            return "edit-student";
        }
        return "redirect:/students";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Student student, HttpSession session) {
        if (!auth(session)) return "redirect:/login";
        studentService.save(student);
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!auth(session)) return "redirect:/login";
        studentService.deleteById(id);
        return "redirect:/students";
    }
}
