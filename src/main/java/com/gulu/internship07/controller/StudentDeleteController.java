package com.gulu.internship07.controller;

import com.gulu.internship07.repository.ApplicationRepository;
import com.gulu.internship07.repository.InterviewRepository;
import com.gulu.internship07.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentDeleteController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Transactional
    @DeleteMapping("/delete/application/{applicationId}")
    public ResponseEntity<?> deleteApplicationRecord(@PathVariable Long applicationId) {
        try {
            // 1. 检查记录是否存在
            if (!applicationRepository.existsById(applicationId)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "申请记录不存在"
                ));
            }

            // 2. 删除关联的 offer 记录
            offerRepository.deleteByApplicationId(applicationId);
            
            // 3. 删除关联的 interviews 记录
            interviewRepository.deleteByApplicationId(applicationId);
            
            // 4. 删除 application 记录
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