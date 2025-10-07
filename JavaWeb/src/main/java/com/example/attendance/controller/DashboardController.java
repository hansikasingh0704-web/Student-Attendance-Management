package com.example.attendance.controller;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Student;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;

    public DashboardController(StudentService studentService, AttendanceService attendanceService) {
        this.studentService = studentService;
        this.attendanceService = attendanceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) return "redirect:/login";

        var students = studentService.getAll();
        model.addAttribute("students", students);

        LocalDate today = LocalDate.now();
        var todayRecords = attendanceService.findByDate(today);
        model.addAttribute("todayCount", todayRecords.size());

        // calculate attendance percentage for each student
        // calculate attendance percentage for each student
        var summary = new java.util.ArrayList<java.util.Map<String, Object>>();
        for (var s : students) {
            var records = attendanceService.findByStudentId(s.getStudentId());
            long total = records.size();

            long present = records.stream()
                    .filter(r -> {
                        String st = r.getStatus();
                        return st != null && !st.isBlank() && st.trim().toUpperCase().startsWith("P");
                    })
                    .count();

            double percent = total == 0 ? 0.0 : (present * 100.0 / total);

            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("student", s);
            m.put("percent", String.format("%.2f", percent)); // string for display
            m.put("percentValue", percent);                    // numeric for conditional styling
            summary.add(m);
        }
        model.addAttribute("summary", summary);
        double avg = summary.stream()
                .mapToDouble(m -> (double) m.get("percentValue"))
                .average()
                .orElse(0.0);
        model.addAttribute("avgPercent", String.format("%.2f", avg));


        return "dashboard";
    }

    // ----------------- POST METHOD TO SAVE ATTENDANCE -----------------
    @PostMapping("/dashboard")
    public String saveAttendance(@RequestParam java.util.Map<String, String> params) {

        List<Student> students = studentService.getAll(); // fetch all students
        LocalDate today = LocalDate.now();

        for (Student s : students) {
            String status = params.get("status_" + s.getStudentId());

            if (status == null) {
                throw new IllegalArgumentException("Status missing for student " + s.getName());
            }

            // normalize input: always save PRESENT or ABSENT
            String normalized;
            if ("P".equalsIgnoreCase(status) || "PRESENT".equalsIgnoreCase(status)) {
                normalized = "PRESENT";
            } else {
                normalized = "ABSENT";
            }

            Attendance attendance = new Attendance();
            attendance.setStudent(s);
            attendance.setAttendanceDate(today);
            attendance.setStatus(normalized);

            attendanceService.save(attendance); // save via service
        }

        return "redirect:/dashboard";
    }
}
