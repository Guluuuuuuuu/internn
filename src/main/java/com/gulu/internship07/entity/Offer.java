package com.gulu.internship07.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "offer")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "application_id")
    private Long applicationId;
    
    @Column(name = "offer_time")
    private LocalDateTime offerTime;
    
    @Column(name = "accept_deadline")
    private LocalDateTime acceptDeadline;
    
    private Integer status;
    
    @Column(name = "cancel_reason")
    private String cancelReason;
} 