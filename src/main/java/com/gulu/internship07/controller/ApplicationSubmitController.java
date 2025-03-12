package com.gulu.internship07.controller;

import com.gulu.internship07.entity.Application;
import com.gulu.internship07.entity.Resume;
import com.gulu.internship07.repository.ApplicationRepository;
import com.gulu.internship07.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
public class ApplicationSubmitController {

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ResumeRepository resumeRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> submitApplication(@RequestBody Map<String, Object> payload) {
        try {
            Long studentId = Long.valueOf(payload.get("studentId").toString());
            Long jobId = Long.valueOf(payload.get("jobId").toString());
            
            // 检查是否已经申请过该岗位
            boolean alreadyApplied = applicationRepository.existsByStudentIdAndJobId(studentId, jobId);
            if (alreadyApplied) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "您已经申请过该岗位"
                ));
            }
            
            // 查找学生的简历
            Optional<Resume> resumeOpt = resumeRepository.findByStudentId(studentId);
            if (resumeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "请先完善您的简历再申请岗位"
                ));
            }
            
            // 创建新的申请记录
            Application application = new Application();
            application.setStudentId(studentId);
            application.setJobId(jobId);
            application.setResumeId(resumeOpt.get().getId());
            application.setStatus(0); // 已投递状态
            application.setCreateTime(LocalDateTime.now());
            application.setUpdateTime(LocalDateTime.now());
            
            applicationRepository.save(application);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "申请成功",
                "applicationId", application.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "申请失败: " + e.getMessage()
            ));
        }
    }
} 