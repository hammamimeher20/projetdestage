package com.example.projetdestage.Twilio;

import com.example.projetdestage.dto.OtpResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;


@CrossOrigin(origins ="http://localhost:8081", maxAge = 3600, allowCredentials= "true" )
@RestController
@RequestMapping("/otp")
@Slf4j
public class OtpController {

	@Autowired
	private SmsService smsService;

	@GetMapping("/process")
	public String processSMS() {
		return "SMS sent";
	}

	@PostMapping("/send-otp")

	public OtpResponseDto sendOtp(@RequestBody OtpRequest otpRequest) {
		log.info("inside sendOtp :: "+otpRequest );
		return smsService.sendSMS(otpRequest);
	}
	@PostMapping("/validate-otp")

	public  String validationOtp(@RequestBody OtpValidationRequest otpValidationRequest){
		log.info("inside validateOtp ::"+ otpValidationRequest +" "+otpValidationRequest.getOtp());
		return  smsService.validateOtp(otpValidationRequest);
	}
}
