package com.gulu.internship07.repository;

import com.gulu.internship07.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByUsername(String username);
    boolean existsByUsername(String username);
} 