package com.example.projetdestage.repository;

import com.example.projetdestage.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPublicationId(Long publicationId);
}
