package com.example.projetdestage.controllers;

import com.example.projetdestage.exception.ResourceNotFound;
import com.example.projetdestage.models.Organizations;
import com.example.projetdestage.security.services.OrganizationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/organizations")
public class organizationsController {


    @Autowired
    OrganizationsService organizationService;

    @GetMapping(value = "/welcome")
    public String welcome(){
        return
                "Welcome, I'm a robot. I will help you to manage Organization ";
    }




    @PostMapping
    @PreAuthorize("hasRole ('MODERATOR') or hasRole('ADMIN') ")
    public Organizations createOrganization (@Valid @RequestBody Organizations organization){
        return
        organizationService.save(organization);
    }

    @GetMapping
    List<Organizations> getOrganization(){
        return
        organizationService.getOrganizations();
    }

    @GetMapping(value = "/get")
    public Organizations getOrganisation(@RequestParam ("organisationsId") long id)  {
        return (Organizations) organizationService.getOrganizations();
    }


    @DeleteMapping
    @PreAuthorize("hasRole ('MODERATOR') or hasRole('ADMIN') ")
    public Map<String,Boolean> deleteOrganizations(@RequestParam("id") long id){
        return  organizationService.deleteOrganization(id);

    }

    @PutMapping
    @PreAuthorize("hasRole ('MODERATOR') or hasRole('ADMIN') ")
    public  Organizations updateOrganization (@RequestParam("id") long id,@Valid @RequestBody Organizations organizations) throws ResourceNotFound, ResourceNotFound {
        return  organizationService.updateOrganizations(id, organizations);
    }
}
