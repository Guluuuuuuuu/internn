package com.gulu.internship07.controller;

import com.gulu.internship07.entity.Interview;
import com.gulu.internship07.repository.InterviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    @Autowired
    private InterviewRepository interviewRepository;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmInterview(@RequestBody Map<String, Object> request) {
        try {
            Long applicationId = Long.valueOf(request.get("applicationId").toString());
            Integer status = Integer.valueOf(request.get("status").toString());
            
            Optional<Interview> interviewOpt = interviewRepository.findByApplicationId(applicationId);
            if (interviewOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "面试记录不存在"
                ));
            }
            
            Interview interview = interviewOpt.get();
            interview.setStatus(status);
            interviewRepository.save(interview);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "面试已确认"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "操作失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelInterview(@RequestBody Map<String, Object> request) {
        try {
            Long applicationId = Long.valueOf(request.get("applicationId").toString());
            Integer status = Integer.valueOf(request.get("status").toString());
            
            Optional<Interview> interviewOpt = interviewRepository.findByApplicationId(applicationId);
            if (interviewOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "面试记录不存在"
                ));
            }
            
            Interview interview = interviewOpt.get();
            interview.setStatus(status);
            interviewRepository.save(interview);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "面试已取消"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "操作失败: " + e.getMessage()
            ));
        }
    }
} 