package com.example.projetdestage.models;


import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;



@Data
@Entity
    @Table(	name = "organizations")
public class Organizations implements Serializable {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(name = "type")
        private String type;
        @Column(name = "name")
        private String name;
        @Column (name = "email")
        private  String email;




}
