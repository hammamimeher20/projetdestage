package com.example.projetdestage.Twilio;


import com.example.projetdestage.dto.OtpResponseDto;
import com.example.projetdestage.payload.request.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/otp")
@Slf4j
public class OtpController {

	private final Map<String, String> otpMap = new HashMap<>();

	@Autowired
	private SmsService smsService;

	@PostMapping("/send-otp")
	public ResponseEntity<OtpResponseDto> sendOtp(@RequestBody SignupRequest SignupRequest) {
		log.info("Inside sendOtp :: {}", SignupRequest.getPhoneNumber());
		OtpResponseDto otpResponseDto = smsService.sendSMS(SignupRequest);
		otpMap.put(SignupRequest.getPhoneNumber(), otpResponseDto.getStatus().equals(OtpStatus.DELIVERED) ? otpResponseDto.getMessage() : "");
		return ResponseEntity.ok(otpResponseDto);
	}

	@PostMapping("/validate-otp")
	public ResponseEntity<String> validateOtp(@RequestBody OtpValidationRequest otpValidationRequest) {
		log.info("Inside validateOtp :: {} {}", otpValidationRequest.getPhoneNumber(), otpValidationRequest.getOtpNumber());

		// Appel de la méthode du service pour la validation de l'OTP
		return smsService.validateOtp(otpValidationRequest);
	}
}























