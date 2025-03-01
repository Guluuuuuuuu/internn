package com.gulu.internship07.repository;

import com.gulu.internship07.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
    School findByUsername(String username);
    boolean existsByUsername(String username);
} 