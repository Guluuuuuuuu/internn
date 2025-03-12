package com.gulu.internship07.service;

import com.gulu.internship07.entity.Job;
import com.gulu.internship07.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    // 根据ID获取岗位
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }
    
    // 其他现有的方法...
} 