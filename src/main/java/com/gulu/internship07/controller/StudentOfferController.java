package com.gulu.internship07.controller;

import com.gulu.internship07.entity.Offer;
import com.gulu.internship07.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/student/offers")
public class StudentOfferController {

    @Autowired
    private OfferRepository offerRepository;

    @PostMapping("/accept")
    public ResponseEntity<?> acceptOffer(@RequestBody Map<String, Object> request) {
        try {
            Long applicationId = Long.valueOf(request.get("applicationId").toString());
            Integer status = Integer.valueOf(request.get("status").toString());
            
            Optional<Offer> offerOpt = offerRepository.findByApplicationId(applicationId);
            if (offerOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Offer记录不存在"
                ));
            }
            
            Offer offer = offerOpt.get();
            offer.setStatus(status);
            offerRepository.save(offer);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Offer已接受"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "操作失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectOffer(@RequestBody Map<String, Object> request) {
        try {
            Long applicationId = Long.valueOf(request.get("applicationId").toString());
            Integer status = Integer.valueOf(request.get("status").toString());
            
            Optional<Offer> offerOpt = offerRepository.findByApplicationId(applicationId);
            if (offerOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Offer记录不存在"
                ));
            }
            
            Offer offer = offerOpt.get();
            offer.setStatus(status);
            offerRepository.save(offer);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Offer已拒绝"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "操作失败: " + e.getMessage()
            ));
        }
    }
} 