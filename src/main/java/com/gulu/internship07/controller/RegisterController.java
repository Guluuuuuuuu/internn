package com.gulu.internship07.controller;

import com.gulu.internship07.entity.Student;
import com.gulu.internship07.entity.Company;
import com.gulu.internship07.entity.School;
import com.gulu.internship07.repository.StudentRepository;
import com.gulu.internship07.repository.CompanyRepository;
import com.gulu.internship07.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerData) {
        String role = registerData.get("role");
        String username = registerData.get("username");
        String password = registerData.get("password");
        String name = registerData.get("name");

        try {
            switch (role) {
                case "student":
                    if (studentRepository.existsByUsername(username)) {
                        return ResponseEntity.badRequest().body("用户名已存在");
                    }
                    Student student = new Student();
                    student.setUsername(username);
                    student.setPassword(password);
                    student.setName(name);
                    student.setImage("/images/default-avatar.png");
                    studentRepository.save(student);
                    break;

                case "company":
                    if (companyRepository.existsByUsername(username)) {
                        return ResponseEntity.badRequest().body("用户名已存在");
                    }
                    Company company = new Company();
                    company.setUsername(username);
                    company.setPassword(password);
                    company.setName(name);
                    company.setImage("/images/default-avatar.png");
                    companyRepository.save(company);
                    break;

                case "school":
                    if (schoolRepository.existsByUsername(username)) {
                        return ResponseEntity.badRequest().body("用户名已存在");
                    }
                    School school = new School();
                    school.setUsername(username);
                    school.setPassword(password);
                    school.setName(name);
                    school.setImage("/images/default-avatar.png");
                    schoolRepository.save(school);
                    break;

                default:
                    return ResponseEntity.badRequest().body("无效的角色类型");
            }
            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("注册失败：" + e.getMessage());
        }
    }
} 