package com.example.projetdestage.security.services;

import com.example.projetdestage.models.Comment;
import com.example.projetdestage.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment addComment(Comment comment) {
        // Ajouter la logique pour ajouter un commentaire
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPublication(Long publicationId) {
        // Ajouter la logique pour récupérer les commentaires par publication
        return commentRepository.findByPublicationId(publicationId);
    }

    // Ajoutez d'autres méthodes pour gérer les commentaires...
}
