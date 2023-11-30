package com.example.projetdestage.models;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data

public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    private int likes; // Ajoutez cette propriété


    // Constructeurs, getters, setters...

    // Getter et Setter pour 'title'
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter et Setter pour 'content'
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Autres getters et setters pour d'autres champs...
}

