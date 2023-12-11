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

// Cette classe implémente l'interface UserDetails de Spring Security,
// qui représente les détails d'un utilisateur pour l'authentification.
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    // Constructeur pour créer une instance UserDetailsImpl avec les détails de l'utilisateur.
    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Méthode statique pour construire un UserDetailsImpl à partir d'un objet User.
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    // Méthode pour obtenir les rôles/autorités de l'utilisateur.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Méthode pour obtenir l'ID de l'utilisateur.
    public Long getId() {
        return id;
    }

    // Méthode pour obtenir l'e-mail de l'utilisateur.
    public String getEmail() {
        return email;
    }

    // Méthode pour obtenir le mot de passe de l'utilisateur.
    @Override
    public String getPassword() {
        return password;
    }

    // Méthode pour obtenir le nom d'utilisateur de l'utilisateur.
    @Override
    public String getUsername() {
        return username;
    }

    // Méthodes suivantes indiquent si le compte de l'utilisateur est expiré, verrouillé,
    // ou si les informations d'identification sont expirées. Actuellement, toutes renvoient true.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Méthode indiquant si le compte de l'utilisateur est activé. Actuellement, renvoie toujours true.
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Méthode pour comparer deux instances UserDetailsImpl. Utilisé pour l'égalité dans Spring Security.
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
