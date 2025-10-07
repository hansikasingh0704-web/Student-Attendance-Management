package com.example.attendance.controller;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final StudentService studentService;

    public AttendanceController(AttendanceService attendanceService, StudentService studentService) {
        this.attendanceService = attendanceService;
        this.studentService = studentService;
    }

    private boolean auth(HttpSession session) { return session.getAttribute("admin") != null; }

    @GetMapping
    public String list(HttpSession session, Model model,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (!auth(session)) return "redirect:/login";
        if (date == null) date = LocalDate.now();
        List<Attendance> records = attendanceService.findByDate(date);
        model.addAttribute("records", records);
        model.addAttribute("date", date);
        return "attendance-list";
    }

    @GetMapping("/mark")
    public String markForm(HttpSession session, Model model,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (!auth(session)) return "redirect:/login";
        if (date == null) date = LocalDate.now();
        model.addAttribute("date", date);
        model.addAttribute("students", studentService.getAll());
        return "mark-attendance";
    }

    @PostMapping("/save")
    public String save(@RequestParam Long studentId,
                       @RequestParam String status,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {

        if (!auth(session)) return "redirect:/login";

        var opt = studentService.findById(studentId);
        if (opt.isPresent()) {
            Student student = opt.get();

            // ✅ Prevent duplicate attendance
            if (attendanceService.existsByStudentAndDate(student, date)) {
                redirectAttributes.addFlashAttribute("error",
                        "Attendance already marked for " + student.getName() + " on " + date);
                return "redirect:/attendance?date=" + date;
            }

            Attendance a = new Attendance();
            a.setAttendanceDate(date);
            a.setStatus(status);
            a.setStudent(student);
            attendanceService.save(a);

            redirectAttributes.addFlashAttribute("success",
                    "Attendance saved for " + student.getName());
        }

        return "redirect:/attendance?date=" + date;
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!auth(session)) return "redirect:/login";
        var opt = attendanceService.findById(id);
        if (opt.isPresent()) {
            model.addAttribute("attendance", opt.get());
            return "edit-attendance";
        }
        return "redirect:/attendance";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Attendance attendance, HttpSession session) {
        if (!auth(session)) return "redirect:/login";
        attendanceService.save(attendance);
        return "redirect:/attendance?date=" + attendance.getAttendanceDate().toString();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!auth(session)) return "redirect:/login";
        attendanceService.deleteById(id);
        return "redirect:/attendance";
    }

    @GetMapping("/summary/{studentId}")
    public String summary(@PathVariable Long studentId, HttpSession session, Model model) {
        if (!auth(session)) return "redirect:/login";
        var opt = studentService.findById(studentId);
        if (opt.isEmpty()) return "redirect:/dashboard";
        var student = opt.get();
        var records = attendanceService.findByStudentId(studentId);

        long total = records.size();

        long present = records.stream()
                .filter(r -> {
                    String s = r.getStatus();
                    if (s == null) return false;
                    s = s.trim();
                    if (s.isEmpty()) return false;
                    return s.toUpperCase().startsWith("P"); // counts "P", "p", "PRESENT", "Present", etc.
                })
                .count();

        long absent = total - present;
        double percent = total == 0 ? 0.0 : (present * 100.0 / total);

        model.addAttribute("student", student);
        model.addAttribute("records", records);
        model.addAttribute("percent", String.format("%.2f", percent));
        model.addAttribute("total", total);
        model.addAttribute("present", present);
        model.addAttribute("absent", absent);

        return "attendance-summary";
    }}
