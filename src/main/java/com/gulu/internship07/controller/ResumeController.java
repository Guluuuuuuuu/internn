package com.gulu.internship07.controller;

import com.gulu.internship07.entity.Resume;
import com.gulu.internship07.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "http://localhost:8080")
public class ResumeController {

    @Autowired
    private ResumeRepository resumeRepository;

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getResume(@PathVariable Long studentId) {
        try {
            Optional<Resume> resumeOpt = resumeRepository.findByStudentId(studentId);
            if (resumeOpt.isPresent()) {
                Resume resume = resumeOpt.get();
                // 打印调试信息
                System.out.println("找到简历: " + resume.getId());
                System.out.println("英语水平: " + resume.getEnglishLevel());
                System.out.println("教育背景: " + resume.getEducationBackground());
                System.out.println("竞赛经历: " + resume.getCompetitionExperience());
                System.out.println("实习经历: " + resume.getInternshipExperience());
                System.out.println("科研经历: " + resume.getResearchExperience());
                
                return ResponseEntity.ok(resume);
            } else {
                // 如果没有找到简历，返回一个空的简历模板
                Map<String, Object> emptyResume = new HashMap<>();
                emptyResume.put("name", "");
                emptyResume.put("age", "");
                emptyResume.put("education", "");
                emptyResume.put("certificates", "");
                emptyResume.put("english_level", "");
                emptyResume.put("photo", "");
                emptyResume.put("introduction", "");
                emptyResume.put("education_background", "");
                emptyResume.put("competition_experience", "");
                emptyResume.put("internship_experience", "");
                emptyResume.put("research_experience", "");
                emptyResume.put("skills", "");
                emptyResume.put("experience", "");
                
                return ResponseEntity.ok(emptyResume);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印详细错误信息
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取简历失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{studentId}")
    public ResponseEntity<?> saveResume(@PathVariable Long studentId, @RequestBody Resume resume) {
        try {
            resume.setStudentId(studentId);
            Resume savedResume = resumeRepository.save(resume);
            return ResponseEntity.ok(savedResume);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "保存简历失败: " + e.getMessage()
            ));
        }
    }
} 