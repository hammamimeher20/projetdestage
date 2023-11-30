package com.example.projetdestage.repository;

import com.example.projetdestage.models.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    // Vous pouvez ajouter des méthodes de requête personnalisées si nécessaire
}
