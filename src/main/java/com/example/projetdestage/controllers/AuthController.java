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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials= "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

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
    @Autowired
    SmsService smsService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("authentication:   " + userDetails.isEnabled());

        // if(userDetails.isEnabled()){
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
        // }else{
        //   return ResponseEntity.status(403).body(" your account is not activated yet");
        //  }

    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // Set phone number during registration
        user.setPhoneNumber(signUpRequest.getPhoneNumber());

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpRequest.getUsername(), signUpRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Build confirmation link including JWT token
        String confirmationLink = "http://localhost:8081/confirmation/?token=" + jwt;

        // Send confirmation email
        boolean emailStatus = emailService.sendSimpleEmail(user.getEmail(), "Welcome Mail",
                "Welcome To Organization" + "\n" + "Bienvenue à Organization,\n" +
                        "\n" + "Merci d’avoir rejoint Organization.\n " +
                        "\n" + "Nous aimerions vous confirmer que votre compte a été créé avec succès.\n " +
                        "\n" + "To confirm your account, please click here :\n "
                        + confirmationLink + "\n" + "Cordialement,\n" + "Organization");
        System.out.println("emailStatus: " + emailStatus);


        return ResponseEntity.ok(new MessageResponse("User registered successfully! " +
                "Please confirm your account through email and valid  SMS."));
    }


    @GetMapping("/confirm-account")
    public ResponseEntity<MessageResponse> confirmAccount(@RequestParam("token") String token) {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                user.setEnabled(true);
                userRepository.save(user);

                return ResponseEntity.ok(new MessageResponse("Account confirmed successfully."));

            } else {
                return ResponseEntity.status(404).body(new MessageResponse("User not found."));
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return ResponseEntity.status(500).body(new MessageResponse("Server error"));
        }

    }
}

   /*@GetMapping("/confirm-account")
   public ResponseEntity<String> confirmAccount(@RequestParam("token") String token,
                                                @RequestParam("phoneNumber") String phoneNumber) {
       try {
           String username = jwtUtils.getUserNameFromJwtToken(token);
           User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

           // Vérifiez si l'utilisateur a déjà confirmé son numéro de téléphone
           if (!user.isPhoneConfirmed()) {
               // Générez un code d'activation (vous pouvez utiliser une bibliothèque comme java.util.Random)
               String activationCode = smsService.generateOTP();

               // Envoyez le code d'activation par SMS
               OtpResponseDto otpResponse = smsService.sendSMS(new OtpRequest(phoneNumber));

               // Stockez le code d'activation dans la base de données ou dans un endroit sécurisé
               user.setActivationCode(activationCode);
               userRepository.save(user);

               // Vous pouvez également ajouter d'autres informations dans la réponse si nécessaire
               return ResponseEntity.ok("Veuillez fournir votre numéro de téléphone pour l'activation. Un code a été envoyé à votre numéro.");
           }


       } catch (Exception e) {
           // Gérez les exceptions
           System.out.println("error:   " + e.getMessage());
           return ResponseEntity.status(500).body("server error");
       }
       return ResponseEntity.badRequest().body("Invalid request");
   }

    */







