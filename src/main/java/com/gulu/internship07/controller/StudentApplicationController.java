package com.gulu.internship07.controller;

import com.gulu.internship07.entity.Application;
import com.gulu.internship07.repository.ApplicationRepository;
import com.gulu.internship07.repository.OfferRepository;
import com.gulu.internship07.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/student/applications")
public class StudentApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelApplication(@RequestBody Map<String, Object> request) {
        try {
            Long applicationId = Long.valueOf(request.get("applicationId").toString());
            Integer status = Integer.valueOf(request.get("status").toString());
            
            Optional<Application> applicationOpt = applicationRepository.findById(applicationId);
            if (applicationOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "申请记录不存在"
                ));
            }
            
            Application application = applicationOpt.get();
            application.setStatus(status);
            applicationRepository.save(application);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "申请已取消"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "操作失败: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{applicationId}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long applicationId) {
        try {
            // 1. 删除关联的 offer 记录
            offerRepository.deleteByApplicationId(applicationId);
            
            // 2. 删除关联的 interviews 记录
            interviewRepository.deleteByApplicationId(applicationId);
            
            // 3. 删除 application 记录
            applicationRepository.deleteById(applicationId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "申请已删除"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "删除失败: " + e.getMessage()
            ));
        }
    }
} 