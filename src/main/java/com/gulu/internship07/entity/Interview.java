package com.gulu.internship07.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "interviews")
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "application_id")
    private Long applicationId;
    
    @Column(name = "interview_time")
    private LocalDateTime interviewTime;
    
    private String location;
    private String interviewer;
    private Integer status;
    private String feedback;
    
    @Column(name = "cancel_reason")
    private String cancelReason;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
} 