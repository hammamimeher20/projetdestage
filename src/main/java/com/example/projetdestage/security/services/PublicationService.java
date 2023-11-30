package com.example.projetdestage.security.services;

import com.example.projetdestage.exception.ResourceNotFound;
import com.example.projetdestage.models.Publication;
import com.example.projetdestage.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.List;

@Service
public class PublicationService {

    @Autowired
    private PublicationRepository publicationRepository;

    public List<Publication> getAllPublications() {
        return publicationRepository.findAll();
    }

    public Publication getPublicationById(Long id) throws ResourceNotFound {
        return publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Publication not found with id: " + id));
    }

    public Publication createPublication(Publication publication) {
        return publicationRepository.save(publication);
    }

    public Publication updatePublication(Long id, Publication newPublication) throws ResourceNotFound {
        Publication existingPublication = getPublicationById(id);

        // Mettez à jour les champs de la publication existante avec les valeurs de la nouvelle publication
        existingPublication.setTitle(newPublication.getTitle());
        existingPublication.setContent(newPublication.getContent());

        return publicationRepository.save(existingPublication);
    }

    public void deletePublication(Long id) throws ResourceNotFound {
        Publication existingPublication = getPublicationById(id);
        publicationRepository.delete(existingPublication);
    }

    public Publication likePublication(Long id) throws ResourceNotFound {
        Publication publication = getPublicationById(id);
        publication.setLikes(publication.getLikes() + 1);
        return publicationRepository.save(publication);
    }

    public Publication unlikePublication(Long id) throws ResourceNotFound {
        Publication publication = getPublicationById(id);
        int currentLikes = publication.getLikes();
        if (currentLikes > 0) {
            publication.setLikes(currentLikes - 1);
        }
        return publicationRepository.save(publication);
    }
}
