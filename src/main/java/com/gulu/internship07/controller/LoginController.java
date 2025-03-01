package com.gulu.internship07.controller;

import com.gulu.internship07.repository.StudentRepository;
import com.gulu.internship07.repository.CompanyRepository;
import com.gulu.internship07.repository.SchoolRepository;
import com.gulu.internship07.repository.AdminRepository;
import com.gulu.internship07.entity.Student;
import com.gulu.internship07.entity.Company;
import com.gulu.internship07.entity.School;
import com.gulu.internship07.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String role = loginData.get("role");

        try {
            Map<String, Object> response = new HashMap<>();
            boolean isAuthenticated = false;
            Long userId = null;
            String name = null;

            switch (role) {
                case "admin":
                    Admin admin = adminRepository.findByUsername(username);
                    if (admin != null && admin.getPassword().equals(password)) {
                        isAuthenticated = true;
                        userId = admin.getId();
                        name = admin.getName();
                    }   
                    break;

                case "student":
                    Student student = studentRepository.findByUsername(username);
                    if (student != null && student.getPassword().equals(password)) {
                        isAuthenticated = true;
                        userId = student.getId();
                        name = student.getName();
                    }
                    break;

                case "company":
                    Company company = companyRepository.findByUsername(username);
                    if (company != null && company.getPassword().equals(password)) {
                        isAuthenticated = true;
                        userId = company.getId();
                        name = company.getName();
                    }
                    break;

                case "school":
                    School school = schoolRepository.findByUsername(username);
                    if (school != null && school.getPassword().equals(password)) {
                        isAuthenticated = true;
                        userId = school.getId();
                        name = school.getName();
                    }
                    break;

                default:
                    return ResponseEntity.badRequest().body("无效的角色类型");
            }

            if (isAuthenticated) {
                response.put("success", true);
                response.put("message", "登录成功");
                response.put("userId", userId);
                response.put("username", username);
                response.put("name", name);
                response.put("role", role);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("用户名或密码错误");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("登录失败：" + e.getMessage());
        }
    }
} 