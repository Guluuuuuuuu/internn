package com.gulu.internship07.controller;

import com.gulu.internship07.entity.JobFavorite;
import com.gulu.internship07.repository.JobFavoriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/job-favorites")
@CrossOrigin(origins = "http://localhost:8080")
public class JobFavoriteController {

    private static final Logger logger = LoggerFactory.getLogger(JobFavoriteController.class);

    @Autowired
    private JobFavoriteRepository jobFavoriteRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/toggle")
    @Transactional
    public ResponseEntity<?> toggleFavorite(@RequestBody Map<String, Long> request) {
        logger.info("Received favorite toggle request: {}", request);
        
        Long studentId = request.get("studentId");
        Long jobId = request.get("jobId");

        if (studentId == null || jobId == null) {
            logger.error("Invalid request parameters: studentId={}, jobId={}", studentId, jobId);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid parameters"));
        }

        try {
            boolean exists = jobFavoriteRepository.existsByStudentIdAndJobId(studentId, jobId);
            logger.info("Favorite exists: {}", exists);
            
            if (exists) {
                jobFavoriteRepository.deleteByStudentIdAndJobId(studentId, jobId);
                return ResponseEntity.ok().body(Map.of("favorite", false));
            } else {
                JobFavorite favorite = new JobFavorite();
                favorite.setStudentId(studentId);
                favorite.setJobId(jobId);
                favorite.setCreateTime(LocalDateTime.now());
                jobFavoriteRepository.save(favorite);
                return ResponseEntity.ok().body(Map.of("favorite", true));
            }
        } catch (Exception e) {
            logger.error("Error processing favorite toggle", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkFavorite(@RequestParam Long studentId, @RequestParam Long jobId) {
        boolean isFavorite = jobFavoriteRepository.existsByStudentIdAndJobId(studentId, jobId);
        return ResponseEntity.ok().body(Map.of("favorite", isFavorite));
    }

    @DeleteMapping("/student/{studentId}/job/{jobId}")
    @Transactional
    public ResponseEntity<?> deleteFavorite(@PathVariable Long studentId, @PathVariable Long jobId) {
        jobFavoriteRepository.deleteByStudentIdAndJobId(studentId, jobId);
        return ResponseEntity.ok().body(Map.of("favorite", false));
    }

    @GetMapping("/student/{studentId}/jobs")
    public ResponseEntity<?> getFavoriteJobs(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String keyword) {
        try {
            // 获取总记录数
            String countSql = """
                SELECT COUNT(*) 
                FROM jobs j 
                INNER JOIN job_favorites f ON j.id = f.job_id 
                LEFT JOIN company c ON j.company_id = c.id
                WHERE f.student_id = ?
                """ + (keyword != null ? " AND (j.title LIKE ? OR c.name LIKE ?)" : "");
            
            Integer total;
            if (keyword != null) {
                String likePattern = "%" + keyword + "%";
                total = jdbcTemplate.queryForObject(countSql, Integer.class, studentId, likePattern, likePattern);
            } else {
                total = jdbcTemplate.queryForObject(countSql, Integer.class, studentId);
            }
            
            // 获取分页数据
            String sql = """
                SELECT j.*, f.create_time as collect_time, c.image as company_logo
                FROM jobs j 
                INNER JOIN job_favorites f ON j.id = f.job_id 
                LEFT JOIN company c ON j.company_id = c.id
                WHERE f.student_id = ?
                """ + (keyword != null ? " AND (j.title LIKE ? OR c.name LIKE ?)" : "") + """
                ORDER BY f.create_time DESC
                LIMIT ? OFFSET ?
                """;
            
            int offset = (page - 1) * size;
            List<Map<String, Object>> favoriteJobs;
            if (keyword != null) {
                String likePattern = "%" + keyword + "%";
                favoriteJobs = jdbcTemplate.queryForList(sql, studentId, likePattern, likePattern, size, offset);
            } else {
                favoriteJobs = jdbcTemplate.queryForList(sql, studentId, size, offset);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("jobs", favoriteJobs);
            response.put("total", total);
            response.put("currentPage", page);
            response.put("totalPages", (total + size - 1) / size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching favorite jobs for student: " + studentId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
} 