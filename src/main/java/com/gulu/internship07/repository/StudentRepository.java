package com.gulu.internship07.repository;

import com.gulu.internship07.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByUsername(String username);
    Student findByUsername(String username);
} 