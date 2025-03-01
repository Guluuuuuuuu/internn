package com.gulu.internship07.repository;

import com.gulu.internship07.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByUsername(String username);
    Company findByUsername(String username);
} 