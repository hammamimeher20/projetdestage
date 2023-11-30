package com.example.projetdestage.security.services;

import com.example.projetdestage.models.User;
import com.example.projetdestage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    // Méthode pour charger les détails de l'utilisateur par son nom d'utilisateur
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Rechercher l'utilisateur dans la base de données par nom d'utilisateur
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Construire et retourner UserDetailsImpl à partir de l'utilisateur trouvé
        return UserDetailsImpl.build(user);
    }

}
