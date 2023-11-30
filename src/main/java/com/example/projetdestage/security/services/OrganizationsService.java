package com.example.projetdestage.security.services;


import com.example.projetdestage.exception.ResourceNotFound;
import com.example.projetdestage.models.Organizations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface OrganizationsService {
    Organizations save(Organizations organization);

    List<Organizations> getOrganizations();

    Map<String, Boolean> deleteOrganization(long id);

    Organizations updateOrganizations(long id, Organizations organizations) throws ResourceNotFound;
}
