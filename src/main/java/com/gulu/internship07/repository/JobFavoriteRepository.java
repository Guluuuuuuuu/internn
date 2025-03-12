package com.gulu.internship07.repository;

import com.gulu.internship07.entity.JobFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JobFavoriteRepository extends JpaRepository<JobFavorite, Long> {
    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);
    
    @Modifying
    @Transactional
    void deleteByStudentIdAndJobId(Long studentId, Long jobId);
} 