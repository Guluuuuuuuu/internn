package com.gulu.internship07.repository;

import com.gulu.internship07.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    Optional<Offer> findByApplicationId(Long applicationId);

    void deleteByApplicationId(Long applicationId);
} 