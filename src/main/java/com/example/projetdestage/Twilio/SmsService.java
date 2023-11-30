package com.example.projetdestage.Twilio;

import com.example.projetdestage.dto.OtpResponseDto;
import com.example.projetdestage.models.User;
import com.example.projetdestage.payload.request.SignupRequest;
import com.example.projetdestage.repository.UserRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class SmsService {
    @Autowired
    private TwilioConfig twilioConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    private Map<String, String> otpMap = new HashMap<>();

    public OtpResponseDto sendSMS(SignupRequest SignupRequest) {
        try {
            String phoneNumber = SignupRequest.getPhoneNumber();
            PhoneNumber to = new PhoneNumber(phoneNumber);
            PhoneNumber from = new PhoneNumber(twilioConfig.getPhoneNumber());
            String otp = generateOTP();

            log.info("Generated OTP: {} for phone number: {}", otp, phoneNumber);

            String otpMessage = otp;
            Message message = Message.creator(to, from, otpMessage).create();

            otpMap.put(phoneNumber, otp);  // Use the phone number as a key

            log.info("OTP Map after storing: {}", otpMap);

            // Save the phone number in the database
            Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
            User user;
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                user = new User();
            }

            // Update or save the phone number
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user);

            return new OtpResponseDto(OtpStatus.DELIVERED, otpMessage);
        } catch (Exception e) {
            log.error("Error sending OTP", e);
            return new OtpResponseDto(OtpStatus.FAILED, e.getMessage());
        }
    }


    // Ajoutez une méthode pour vérifier l'existence de l'OTP pour un numéro de téléphone donné
    public boolean isOtpStored(String phoneNumber) {
        return otpMap.containsKey(phoneNumber);
    }

    // Ajoutez une méthode pour récupérer l'OTP pour un numéro de téléphone donné
    public String getStoredOtp(String phoneNumber) {
        return otpMap.get(phoneNumber);
    }

    // Ajoutez une méthode pour supprimer l'OTP après validation
    public void removeOtp(String phoneNumber) {
        otpMap.remove(phoneNumber);
    }

   /* public ResponseEntity<String> validateOtp(OtpValidationRequest otpValidationRequest) {
        log.info("Inside validateOtp :: {} {}", otpValidationRequest.getPhoneNumber(), otpValidationRequest.getOtpNumber());

        try {
            String phoneNumber = otpValidationRequest.getPhoneNumber();
            String enteredOtp = otpValidationRequest.getOtpNumber();

            // Recherche de l'utilisateur par numéro de téléphone
           // User user = userRepository.findByPhoneNumber(phoneNumber).orElse(null);
            User user = userRepository.findByUsername(otpValidationRequest.getUsername()).orElse(null);
            if (user != null) {
                // Utilisateur trouvé, procédez à la validation de l'OTP
                if (isOtpStored(phoneNumber)) {
                    String storedOtp = getStoredOtp(phoneNumber);

                    log.info("Stored OTP for {}: {}", phoneNumber, storedOtp);

                    if (enteredOtp.equals(storedOtp)) {
                        handleSuccessfulOtpValidation(user);
                        removeOtp(phoneNumber);  // Supprimez l'OTP après validation
                        return ResponseEntity.ok("{\"message\": \"OTP is valid!\"}");
                    } else {
                        log.info("Entered OTP does not match stored OTP.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Invalid OTP!\"}");
                    }
                } else {
                    log.info("No stored OTP for the given phone number.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Invalid OTP!\"}");
                }
            } else {
                log.error("User not found for phone number: {}", phoneNumber);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
            }
        } catch (Exception e) {
            log.error("Error during OTP validation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Internal server error.\"}");
        }
    }
*/





    public ResponseEntity<String> validateOtp(OtpValidationRequest otpValidationRequest) {
        log.info("Inside validateOtp :: {} {}", otpValidationRequest.getPhoneNumber(), otpValidationRequest.getOtpNumber());

        try {
            String phoneNumber = otpValidationRequest.getPhoneNumber();
            String enteredOtp = otpValidationRequest.getOtpNumber();

            // Recherche de l'utilisateur par numéro de téléphone
            User user = userRepository.findByPhoneNumber(phoneNumber).orElseGet(() -> userRepository.findByUsername(otpValidationRequest.getUsername()).orElse(null));

            if (user != null) {
                // Utilisateur trouvé, procédez à la validation de l'OTP
                if (isOtpStored(phoneNumber)) {
                    String storedOtp = getStoredOtp(phoneNumber);

                    log.info("Stored OTP for {}: {}", phoneNumber, storedOtp);

                    if (enteredOtp.equals(storedOtp)) {
                        handleSuccessfulOtpValidation(user);
                        removeOtp(phoneNumber);  // Supprimez l'OTP après validation
                        return ResponseEntity.ok("{\"message\": \"OTP is valid!\"}");
                    } else {
                        log.info("Entered OTP does not match stored OTP.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Invalid OTP!\"}");
                    }
                } else {
                    log.info("No stored OTP for the given phone number.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Invalid OTP!\"}");
                }
            } else {
                log.error("User not found for phone number: {}", phoneNumber);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"User not found!\"}");
            }
        } catch (Exception e) {
            log.error("Error during OTP validation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Internal server error.\"}");
        }
    }

    // ...



    private void handleSuccessfulOtpValidation(User user) {
        try {
            if (user != null) {
                if (!user.isPhoneNumberVerified()) {
                    // Mettre à jour les champs nécessaires après la validation de l'OTP
                    user.setPhoneNumberVerified(true);
                    user.setValidateOtp(true);

                    // Enregistrer les modifications dans la base de données
                    userRepository.save(user);
                } else {
                    log.error("User already verified for phone number: {}", user.getPhoneNumber());
                }
            } else {
                log.error("User is null. Unable to handle successful OTP validation.");
            }
        } catch (Exception e) {
            log.error("Error during OTP validation", e);
        }
    }

    private String generateOTP() {
        return new DecimalFormat("000000").format(Math.floor(Math.random() * 1000000));
    }

}

