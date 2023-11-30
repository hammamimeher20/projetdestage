package com.example.projetdestage.security.jwt;

import com.example.projetdestage.security.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // Cette méthode est appelée à chaque requête HTTP pour filtrer et traiter le jeton JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extraire le jeton JWT de l'en-tête de la requête
            String jwt = parseJwt(request);

            // Vérifier si le jeton est présent et valide
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Extraire le nom d'utilisateur du jeton JWT
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Charger les détails de l'utilisateur à partir du service UserDetails
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Créer une authentification Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Ajouter les détails de l'authentification à partir de la requête
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Gérer les erreurs liées à l'authentification de l'utilisateur
            logger.error("Cannot set user authentication: {}", e);
        }

        // Passer la requête au filtre suivant dans la chaîne
        filterChain.doFilter(request, response);
    }

    // Méthode pour extraire le jeton JWT de l'en-tête de la requête
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
