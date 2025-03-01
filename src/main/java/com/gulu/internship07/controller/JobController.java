package com.gulu.internship07.controller;

import com.gulu.internship07.entity.Job;
import com.gulu.internship07.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")  // 基础路径是 /api
@CrossOrigin(origins = "*")  // 允许所有来源的跨域请求
public class JobController {
    
    private static final Logger logger = LoggerFactory.getLogger(JobController.class);
    
    @Autowired
    private JobRepository jobRepository;
    
    @GetMapping("/latest-jobs")  // 这个路径会与基础路径组合成 /api/latest-jobs
    public ResponseEntity<?> getLatestJobs() {
        logger.info("Received request for latest jobs");
        try {
            List<Job> jobs = jobRepository.findLatestJobs();
            logger.info("Found {} jobs", jobs.size());
            
            // 添加更详细的日志
            if (jobs.isEmpty()) {
                logger.warn("No jobs found in database");
                return ResponseEntity.ok().body("[]");  // 返回空数组而不是 null
            }
            
            logger.info("Successfully retrieved jobs from database");
            return ResponseEntity.ok(jobs);
            
        } catch (Exception e) {
            logger.error("Error fetching jobs: ", e);
            return ResponseEntity.internalServerError()
                .body("Error fetching jobs: " + e.getMessage());
        }
    }

    // 添加搜索接口
    @GetMapping("/jobs/search")
    public ResponseEntity<?> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String education) {
        try {
            logger.info("Searching jobs with keyword: {}, location: {}, jobType: {}, education: {}", 
                    keyword, location, jobType, education);
            
            List<Job> jobs = jobRepository.searchJobs(keyword, location, jobType, education);
            logger.info("Found {} jobs matching criteria", jobs.size());
            
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            logger.error("Error searching jobs: ", e);
            return ResponseEntity.internalServerError()
                    .body("Error searching jobs: " + e.getMessage());
        }
    }
} 