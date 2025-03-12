package com.gulu.internship07.repository;

import com.gulu.internship07.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // 基础的 CRUD 操作由 JpaRepository 提供
    List<Application> findByStudentId(Long studentId);
    
    // 添加新方法
    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);
} 