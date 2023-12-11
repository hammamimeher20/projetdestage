package com.example.projetdestage.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;


    @Column(name = "phone_number")
    private String phoneNumber;


    @Column(name = "validate_otp")
    private boolean validateOtp;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "phone_number_verified")
    private boolean phoneNumberVerified;

    private  String otp;
    
    private  String PhoneConfirmed;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(  name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }


    public void setValidateOtp(boolean validateOtp) {
        this.validateOtp= validateOtp;
    }

    public void setOtpValidated(boolean b) {

    }


    public boolean isPhoneConfirmed() {
        return false;
    }

    public void setActivationCode(String activationCode) {
    }
}
