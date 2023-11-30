package com.example.projetdestage.controllers;

import com.example.projetdestage.exception.ResourceNotFound;
import com.example.projetdestage.models.Publication;
import com.example.projetdestage.security.services.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Publication>> getAllPublications() {
        List<Publication> publications = publicationService.getAllPublications();
        return ResponseEntity.ok(publications);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Publication> getPublicationById(@PathVariable Long id) throws ResourceNotFound {
        Publication publication = publicationService.getPublicationById(id);
        return ResponseEntity.ok(publication);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Publication> createPublication(@RequestBody Publication publication) {
        Publication createdPublication = publicationService.createPublication(publication);
        return ResponseEntity.ok(createdPublication);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Publication> updatePublication(@PathVariable Long id, @RequestBody Publication publication) throws ResourceNotFound {
        Publication updatedPublication = publicationService.updatePublication(id, publication);
        return ResponseEntity.ok(updatedPublication);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePublication(@PathVariable Long id) throws ResourceNotFound {
        publicationService.deletePublication(id);
        return ResponseEntity.noContent().build();
    }
}
