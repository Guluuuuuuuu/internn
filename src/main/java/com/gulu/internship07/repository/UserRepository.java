package com.gulu.internship07.repository;

import com.gulu.internship07.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
} 