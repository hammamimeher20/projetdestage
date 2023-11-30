package com.example.projetdestage.controllers;

import com.example.projetdestage.Twilio.OtpRequest;
import com.example.projetdestage.Twilio.OtpValidationRequest;
import com.example.projetdestage.Twilio.SmsService;
import com.example.projetdestage.dto.OtpResponseDto;
import com.example.projetdestage.email.EmailService;
import com.example.projetdestage.models.ERole;
import com.example.projetdestage.models.Role;
import com.example.projetdestage.models.User;
import com.example.projetdestage.payload.request.LoginRequest;
import com.example.projetdestage.payload.request.SignupRequest;
import com.example.projetdestage.payload.response.JwtResponse;
import com.example.projetdestage.payload.response.MessageResponse;
import com.example.projetdestage.repository.RoleRepository;
import com.example.projetdestage.repository.UserRepository;
import com.example.projetdestage.security.jwt.JwtUtils;
import com.example.projetdestage.security.services.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Autres autowireds omis

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    SmsService smsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;


    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    EmailService emailService;


    // Endpoint pour l'authentification d'un utilisateur
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (Exception e) {
            log.error("Erreur pendant l'authentification de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Nom d'utilisateur ou mot de passe incorrect"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Erreur : Nom d'utilisateur déjà pris"));
            }

            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Erreur : L'e-mail est déjà utilisé"));
            }

            User user = new User(signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()));
            user.setPhoneNumber(signUpRequest.getPhoneNumber());

            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Erreur : Rôle introuvable"));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    Role userRole = roleRepository.findByName(ERole.valueOf(role.toUpperCase()))
                            .orElseThrow(() -> new RuntimeException("Erreur : Rôle introuvable"));
                    roles.add(userRole);
                });
            }

            user.setRoles(roles);

            userRepository.save(user);


            if (user.isEnabled()) {
                if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {

                    // Envoyer l'OTP par SMS
                    //

                    return ResponseEntity.ok(new MessageResponse("Veuillez fournir le code OTP reçu par SMS."));
                } else {
                    // Le numéro de téléphone est manquant
                    return ResponseEntity.badRequest().body(new MessageResponse("Le numéro de téléphone est manquant."));
                }
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signUpRequest.getUsername(), signUpRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            String confirmationLink = "http://localhost:8081/confirmation/?token=" + jwt;
            boolean emailStatus = emailService.sendSimpleEmail(user.getEmail(), "Welcome Mail",
                    "Bienvenue à Organization,\n" +
                            "\n" + "Merci d’avoir rejoint Organization.\n" +
                            "\n" + "Nous aimerions vous confirmer que votre compte a été créé avec succès.\n " +
                            "\n" + "Pour confirmer votre compte, veuillez cliquer ici :\n "
                            + confirmationLink + "\n" + "Cordialement,\n" + "Organization");

            System.out.println("emailStatus: " + emailStatus);

            return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès! "
                    + "Veuillez confirmer votre compte pour rejoindre votre e-mail"));
        } catch (Exception e) {
            log.error("Erreur pendant l'enregistrement de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Erreur du serveur"));
        }
    }


    // Endpoint pour confirmer le compte d'utilisateur
    @GetMapping("/confirm-account")
    public ResponseEntity<MessageResponse> confirmAccount(@RequestParam("token") String token) {
        try {
            // Extraire le nom d'utilisateur à partir du jeton JWT
            String username = jwtUtils.getUserNameFromJwtToken(token);

            // Rechercher l'utilisateur dans la base de données par nom d'utilisateur
            User user = userRepository.findByUsername(username).orElse(null);

            // Vérifier si l'utilisateur existe
            if (user != null) {
                // Activer le compte de l'utilisateur
                user.setEnabled(true);
                userRepository.save(user);

                // Appeler la méthode pour envoyer l'OTP au numéro de téléphone de l'utilisateur
                SignupRequest SignupRequest = new SignupRequest();
                SignupRequest.setPhoneNumber(user.getPhoneNumber());

               // OtpResponseDto SignupRequest = smsService.sendSMS(SignupRequest);

                // Vous pouvez vérifier ici otpResponse.getStatus() pour voir si l'envoi a réussi ou non.
                // Si l'envoi a réussi, retournez une réponse demandant au client de fournir le code OTP
                // S'il y a une erreur, vous pouvez traiter en conséquence.
                return ResponseEntity.ok(new MessageResponse("Veuillez fournir le code OTP reçu par SMS."));
            } else {
                // Retourner une réponse 404 si l'utilisateur n'est pas trouvé
                return ResponseEntity.status(404).body(new MessageResponse("Utilisateur non trouvé."));
            }
        } catch (Exception e) {
            // Gérer les erreurs de manière appropriée (500 Internal Server Error)
            log.error("Erreur pendant la confirmation du compte", e);
            return ResponseEntity.status(500).body(new MessageResponse("Erreur du serveur"));
        }
    }
    // Endpoint pour demander le numéro de téléphone et envoyer l'OTP
    @PostMapping("/request-phone-number")
    public ResponseEntity<MessageResponse> requestPhoneNumber(@RequestBody SignupRequest signupRequest) {
        try {
            // Recherchez l'utilisateur dans la base de données par nom d'utilisateur ou autre identifiant
            // (vous pouvez ajuster cela en fonction de votre modèle utilisateur)
            User user = userRepository.findByUsername(signupRequest.getUsername()).orElse(null);

            // Vérifier si l'utilisateur existe
            if (user != null) {
                // Générez et envoyez l'OTP par SMS

                signupRequest.setPhoneNumber(signupRequest.getPhoneNumber());

                OtpResponseDto otpResponse = smsService.sendSMS(signupRequest);

                // Vous pouvez vérifier ici otpResponse.getStatus() pour voir si l'envoi a réussi ou non.
                // System.out.println("emailStatus: " + emailStatus);
               //System.out.println("sendstatus:"+ otpStatus());
                // Si l'envoi a réussi, retournez une réponse demandant au client de fournir le code OTP
                // S'il y a une erreur, vous pouvez traiter en conséquence.
                return ResponseEntity.ok(new MessageResponse("Veuillez fournir le code OTP reçu par SMS."));
            } else {
                // Retourner une réponse 404 si l'utilisateur n'est pas trouvé
                return ResponseEntity.status(404).body(new MessageResponse("Utilisateur non trouvé."));
            }
        } catch (Exception e) {
            // Gérer les erreurs de manière appropriée (500 Internal Server Error)
            log.error("Erreur pendant la demande du numéro de téléphone et l'envoi de l'OTP", e);
            return ResponseEntity.status(500).body(new MessageResponse("Erreur du serveur"));
        }
    }

    // Endpoint pour valider l'OTP
    @PostMapping("/validate-otp")
    public ResponseEntity<MessageResponse> validateOtp(@RequestBody OtpValidationRequest otpValidationRequest) {
        try {
            // Recherchez l'utilisateur dans la base de données par nom d'utilisateur ou autre identifiant
            // (vous pouvez ajuster cela en fonction de votre modèle utilisateur)
            User user = userRepository.findByUsername(otpValidationRequest.getUsername()).orElse(null);

            // Vérifier si l'utilisateur existe
            if (user != null) {
                // Validez l'OTP en appelant la méthode appropriée du service SMS
                ResponseEntity<String> otpValidationResponse = smsService.validateOtp(otpValidationRequest);
                        //smsService.validateOtp(otpValidationRequest);

                // Vous pouvez traiter ici la réponse de validation de l'OTP
                if (otpValidationResponse.getStatusCode() == HttpStatus.OK) {
                    // L'OTP est valide, vous pouvez activer d'autres fonctionnalités ou rediriger l'utilisateur
                    return ResponseEntity.ok(new MessageResponse("Validation de l'OTP réussie. Vous pouvez maintenant accéder à votre compte."));
                } else {
                    // L'OTP n'est pas valide, retournez un message d'erreur approprié
                    return ResponseEntity.status(400).body(new MessageResponse("Code OTP incorrect. Veuillez réessayer."));
                }
            } else {
                // Retourner une réponse 404 si l'utilisateur n'est pas trouvé
                return ResponseEntity.status(404).body(new MessageResponse("Utilisateur non trouvé."));
            }
        } catch (Exception e) {
            // Gérer les erreurs de manière appropriée (500 Internal Server Error)
            log.error("Erreur pendant la validation de l'OTP", e);
            return ResponseEntity.status(500).body(new MessageResponse("Erreur du serveur"));
        }
    }

}

