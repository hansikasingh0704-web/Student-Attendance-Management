package com.example.attendance.controller;

import com.example.attendance.entity.Admin;
import com.example.attendance.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    private final AdminService adminService;

    public LoginController(AdminService adminService) { this.adminService = adminService; }

    @GetMapping({"/", "/login"})
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        var opt = adminService.login(username, password);
        if (opt.isPresent()) {
            Admin admin = opt.get();
            session.setAttribute("admin", admin.getUsername());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
