package com.backmind.review.repository;

import com.backmind.review.entity.NoteReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoteReviewRepository extends JpaRepository<NoteReview, UUID> {
}
