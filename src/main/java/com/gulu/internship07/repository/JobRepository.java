package com.gulu.internship07.repository;

import com.gulu.internship07.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    @Query(value = "SELECT * FROM jobs ORDER BY id DESC", nativeQuery = true)
    List<Job> findLatestJobs();

    @Query(value = """
        SELECT * FROM jobs 
        WHERE (:keyword IS NULL OR 
              title LIKE CONCAT('%', :keyword, '%') OR 
              company_name LIKE CONCAT('%', :keyword, '%'))
        AND (:location IS NULL OR location LIKE CONCAT('%', :location, '%'))
        AND (:jobType IS NULL OR job_type = :jobType)
        AND (:education IS NULL OR education = :education)
        ORDER BY id DESC
        """, nativeQuery = true)
    List<Job> searchJobs(
        @Param("keyword") String keyword,
        @Param("location") String location,
        @Param("jobType") String jobType,
        @Param("education") String education
    );
} 