package com.gulu.internship07.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id")
    private Long studentId;
    
    @Column(name = "job_id")
    private Long jobId;
    
    @Column(name = "resume_id")
    private Long resumeId;
    
    private Integer status;
    
    @Column(name = "cancel_reason")
    private String cancelReason;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column
    private String feedback;

    // Getters and Setters
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    // ... 
} 