package com.example.projetdestage.controllers;

import com.example.projetdestage.models.Comment;
import com.example.projetdestage.security.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        // Ajouter la logique pour ajouter un commentaire
        Comment addedComment = commentService.addComment(comment);
        return ResponseEntity.ok(addedComment);
    }

    @GetMapping("/{publicationId}")
    public ResponseEntity<List<Comment>> getCommentsByPublication(@PathVariable Long publicationId) {
        List<Comment> comments = commentService.getCommentsByPublication(publicationId);
        return ResponseEntity.ok(comments);
    }

    // Ajoutez d'autres méthodes pour gérer les commentaires...
}
