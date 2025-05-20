package com.example.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entites.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
