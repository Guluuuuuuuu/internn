package com.gulu.internship07.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(name = "company_name")
    private String companyName;
    
    private String salary;
    private String location;
    
    @Column(name = "job_type")
    private String jobType;
    
    private String education;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String description;
    private String requirements;
} 