package com.example.projetdestage.security.services;

import com.example.projetdestage.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String phoneNumber;
    private boolean enabled;
    private boolean validateOtp;
    private boolean phoneNumberVerified;

    private Collection<? extends GrantedAuthority> authorities;
    public UserDetailsImpl(Long id, String username, String email, String password,
                           String phoneNumber,boolean validateOtp ,boolean enabled,boolean phoneNumberVerified,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber=phoneNumber;
        this.enabled = enabled;
        this.phoneNumberVerified= phoneNumberVerified;
        this.validateOtp = validateOtp;
        this.authorities = authorities;
    }
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.isEnabled(),
                user.isPhoneNumberVerified(),
                user.isValidateOtp(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public boolean getEnabled() {
        return enabled;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

     public  String phoneNumber(){return phoneNumber;}



    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
    //  méthode  pour récupérer le numéro de téléphone
    public String getPhoneNumber() {
        return phoneNumber;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
