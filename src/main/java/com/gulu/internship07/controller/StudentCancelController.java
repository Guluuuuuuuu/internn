package com.gulu.internship07.controller;

import com.gulu.internship07.repository.InterviewRepository;
import com.gulu.internship07.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/cancel")
public class StudentCancelController {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private OfferRepository offerRepository;

    @PostMapping("/interview/{applicationId}")
    public ResponseEntity<?> cancelInterview(@PathVariable Long applicationId) {
        try {
            var interviewOpt = interviewRepository.findByApplicationId(applicationId);
            if (interviewOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "面试记录不存在"
                ));
            }

            var interview = interviewOpt.get();
            interview.setStatus(-2);  // 设置为取消状态
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

    @PostMapping("/offer/{applicationId}")
    public ResponseEntity<?> cancelOffer(@PathVariable Long applicationId) {
        try {
            var offerOpt = offerRepository.findByApplicationId(applicationId);
            if (offerOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Offer记录不存在"
                ));
            }

            var offer = offerOpt.get();
            offer.setStatus(-2);  // 设置为取消状态
            offerRepository.save(offer);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "入职已取消"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "操作失败: " + e.getMessage()
            ));
        }
    }
} 