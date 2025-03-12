package com.gulu.internship07.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume")
public class Resume {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id")
    private Long studentId;
    
    private String name;
    
    private Integer age;
    
    @Column(columnDefinition = "TEXT")
    private String education;
    
    @Column(columnDefinition = "TEXT")
    private String experience;
    
    @Column(columnDefinition = "TEXT")
    private String skills;
    
    @Column(columnDefinition = "TEXT")
    private String certificates;
    
    @Column(name = "english_level", columnDefinition = "TEXT")
    @JsonProperty("english_level")
    private String englishLevel;
    
    @Column(columnDefinition = "LONGTEXT")
    private String photo;
    
    @Column(columnDefinition = "TEXT")
    private String introduction;
    
    @Column(name = "education_background", columnDefinition = "TEXT")
    @JsonProperty("education_background")
    private String educationBackground;
    
    @Column(name = "competition_experience", columnDefinition = "TEXT")
    @JsonProperty("competition_experience")
    private String competitionExperience;
    
    @Column(name = "internship_experience", columnDefinition = "TEXT")
    @JsonProperty("internship_experience")
    private String internshipExperience;
    
    @Column(name = "research_experience", columnDefinition = "TEXT")
    @JsonProperty("research_experience")
    private String researchExperience;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getEducation() {
        return education;
    }
    
    public void setEducation(String education) {
        this.education = education;
    }
    
    public String getExperience() {
        return experience;
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
    }
    
    public String getSkills() {
        return skills;
    }
    
    public void setSkills(String skills) {
        this.skills = skills;
    }
    
    public String getCertificates() {
        return certificates;
    }
    
    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }
    
    public String getEnglishLevel() {
        return englishLevel;
    }
    
    public void setEnglishLevel(String englishLevel) {
        this.englishLevel = englishLevel;
    }
    
    public String getPhoto() {
        return photo;
    }
    
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    
    public String getIntroduction() {
        return introduction;
    }
    
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    
    public String getEducationBackground() {
        return educationBackground;
    }
    
    public void setEducationBackground(String educationBackground) {
        this.educationBackground = educationBackground;
    }
    
    public String getCompetitionExperience() {
        return competitionExperience;
    }
    
    public void setCompetitionExperience(String competitionExperience) {
        this.competitionExperience = competitionExperience;
    }
    
    public String getInternshipExperience() {
        return internshipExperience;
    }
    
    public void setInternshipExperience(String internshipExperience) {
        this.internshipExperience = internshipExperience;
    }
    
    public String getResearchExperience() {
        return researchExperience;
    }
    
    public void setResearchExperience(String researchExperience) {
        this.researchExperience = researchExperience;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 