package com.example.projetdestage.Twilio;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.example.projetdestage.dto.OtpResponseDto;
import com.example.projetdestage.models.User;
import com.example.projetdestage.repository.UserRepository;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Autowired
    private UserRepository userRepository;

    // Logger pour des raisons de journalisation
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

    // Modèle de message OTP envoyé par SMS
    private static final String OTP_MESSAGE_TEMPLATE = "Cher client, votre OTP est %s pour l'envoi de SMS via l'organisation. Merci.";

    // Configuration Twilio injectée via Spring
    @Autowired
    private TwilioConfig twilioConfig;

    // Map pour stocker les OTP associés aux numéros de téléphone
    private Map<String, String> otpMap = new ConcurrentHashMap<>();

    /**
     * Envoie un OTP via SMS en utilisant l'API Twilio.
     *
     * @param otpRequest La demande contenant le numéro de téléphone auquel l'OTP doit être envoyé.
     * @return OtpResponseDto contenant le statut et le message.
     */
    public OtpResponseDto sendSMS(OtpRequest otpRequest) {
        OtpResponseDto otpResponseDto;
        try {
            // Construit des objets de numéro de téléphone Twilio pour l'expéditeur et le destinataire
            PhoneNumber to = new PhoneNumber(otpRequest.getPhoneNumber());
            PhoneNumber from = new PhoneNumber(twilioConfig.getPhoneNumber());

            // Génère un nouvel OTP et crée le message SMS
            String otp = generateOTP();
            String otpMessage = String.format(OTP_MESSAGE_TEMPLATE, otp);
            Message message = Message.creator(to, from, otpMessage).create();

            // Stocke l'OTP généré dans la carte pour une validation ultérieure
            otpMap.put(otpRequest.getPhoneNumber(), otp);

            // Stocke le code SMS dans la base de données
            storeSmsCodeInDatabase(otpRequest.getPhoneNumber(), otp);

            // Définit le statut de la réponse comme DELIVERED
            otpResponseDto = new OtpResponseDto(OtpStatus.DELIVERED, otpMessage);
        } catch (ApiException e) {
            // Journalise les erreurs de l'API Twilio et définit le statut de la réponse comme FAILED
            LOGGER.error("Erreur API Twilio lors de l'envoi du SMS", e);
            otpResponseDto = new OtpResponseDto(OtpStatus.FAILED, e.getMessage());
        } catch (Exception e) {
            // Journalise d'autres erreurs et définit le statut de la réponse comme FAILED
            LOGGER.error("Erreur lors de l'envoi du SMS avec Twilio", e);
            otpResponseDto = new OtpResponseDto(OtpStatus.FAILED, e.getMessage());
        }
        return otpResponseDto;
    }

    /**
     * Valide l'OTP reçu pour un numéro de téléphone donné.
     *
     * @param otpValidationRequest La demande contenant le numéro de téléphone et l'OTP à valider.
     * @return Un message indiquant si l'OTP est valide ou invalide.
     */
    public String validateOtp(OtpValidationRequest otpValidationRequest) {
        String phoneNumber = otpValidationRequest.getPhoneNumber();
        String otp = otpMap.get(phoneNumber);

        // Vérifie si l'OTP stocké correspond à l'OTP reçu
        if (otp != null && otp.equals(otpValidationRequest.getOtp())) {
            // Supprime l'OTP de la carte s'il est valide
            otpMap.remove(phoneNumber);

            // Enregistre le numéro de téléphone dans la base de données
            Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
            if (optionalUser.isPresent()) {
                // Mettez à jour l'utilisateur ou effectuez toute autre opération nécessaire
                User user = optionalUser.get();
                user.setPhoneNumber(phoneNumber);
                userRepository.save(user);
            } else {
                // Créez un nouvel utilisateur si nécessaire
                User user = new User();
                user.setPhoneNumber(phoneNumber);
                userRepository.save(user);
            }

            return "OTP est valide !";
        } else {
            return "OTP est invalide !";
        }
    }

    /**
     * Génère un OTP aléatoire à six chiffres.
     *
     * @return L'OTP généré.
     */
    public String generateOTP() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    /**
     * Stocke le code SMS dans la base de données.
     *
     * @param phoneNumber Le numéro de téléphone associé au code SMS.
     * @param smsCode     Le code SMS à stocker.
     */
    private void storeSmsCodeInDatabase(String phoneNumber, String smsCode) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);

        optionalUser.ifPresent(user -> {
            user.setOtp(smsCode);
            userRepository.save(user);
        });
    }
}
