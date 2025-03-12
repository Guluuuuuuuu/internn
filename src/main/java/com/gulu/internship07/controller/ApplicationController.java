package com.gulu.internship07.controller;

import com.gulu.internship07.entity.Application;
import com.gulu.internship07.entity.Interview;
import com.gulu.internship07.entity.Offer;
import com.gulu.internship07.repository.ApplicationRepository;
import com.gulu.internship07.repository.InterviewRepository;
import com.gulu.internship07.repository.OfferRepository;
import com.gulu.internship07.service.ApplicationStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private InterviewRepository interviewRepository;
    
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private ApplicationStatusService applicationStatusService;

    /**
     * 获取学生的所有申请
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentApplications(@PathVariable Long studentId) {
        try {
            // 使用 ApplicationStatusService 中的方法
            List<Map<String, Object>> applications = applicationStatusService.getStudentApplications(studentId, 100, 0);
            
            // 处理每个申请的状态
            applications.forEach(app -> {
                Long appId = ((Number) app.get("id")).longValue();
                Map<String, Object> status = applicationStatusService.getApplicationStatus(appId);
                app.put("status", status);
            });
            
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            logger.error("获取学生申请记录失败", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取申请状态
     */
    @GetMapping("/{applicationId}/status")
    public ResponseEntity<?> getApplicationStatus(@PathVariable Long applicationId) {
        try {
            Map<String, Object> status = applicationStatusService.getApplicationStatus(applicationId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 以下是辅助方法，实际实现可能不同
    private String getJobPosition(Long jobId) {
        // 实现获取职位名称的逻辑
        return "职位名称";
    }
    
    private String getCompanyName(Long jobId) {
        // 实现获取公司名称的逻辑
        return "公司名称";
    }
    
    private String getCompanyLogo(Long jobId) {
        // 实现获取公司logo的逻辑
        return "/company-logos/default.png";
    }
    
    private String getCompanyAddress(Long jobId) {
        // 实现获取公司地址的逻辑
        return "公司地址";
    }
} 