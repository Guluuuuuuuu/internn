package com.gulu.internship07.repository;

import com.gulu.internship07.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
    boolean existsByUsername(String username);
    School findByUsername(String username);
} 