package com.example.projetdestage.security.services;



import com.example.projetdestage.exception.ResourceNotFound;
import com.example.projetdestage.models.Organizations;
import com.example.projetdestage.repository.OrganizationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrganizationsServiceImpl implements OrganizationsService {
@Autowired
OrganizationsRepository organizationsRepository;


    @Override
    public Organizations save(Organizations organization) {
        return save(organization );
    }
    public  Organizations getOrganization (Long id) throws ResourceNotFound {

        return organizationsRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFound("organization not found id :" +id)
        );
    }

    @Override
    public List<Organizations> getOrganizations() {
        return (List<Organizations>) organizationsRepository.findAll();
    }

    @Override
    public Map<String, Boolean> deleteOrganization(long id) {
        organizationsRepository.deleteById(id);
        Map<String,Boolean> res= new HashMap<>();
        res.put("deleted", Boolean.TRUE);
        return res ;
    }

    @Override
    public Organizations updateOrganizations(long id, Organizations organizations) throws ResourceNotFound {

        Organizations old = getOrganization(id);
        old.setName(organizations.getName());
        old.setType(organizations.getType());
        old.setEmail(organizations.getEmail());

        return organizationsRepository.save(old);
    }
}
