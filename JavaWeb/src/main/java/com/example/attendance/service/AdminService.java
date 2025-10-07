package com.example.attendance.service;

import com.example.attendance.entity.Admin;
import com.example.attendance.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepo;

    public AdminService(AdminRepository adminRepo) { this.adminRepo = adminRepo; }

    public Optional<Admin> login(String username, String password) {
        return adminRepo.findByUsernameAndPassword(username, password);
    }
}
