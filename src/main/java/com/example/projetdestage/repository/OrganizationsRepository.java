package com.example.projetdestage.repository;


import com.example.projetdestage.models.Organizations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrganizationsRepository extends CrudRepository<Organizations, Long> {
    public List<Organizations> findByName(String   Name );
    public  List<Organizations> findByType( String Type);


}
