package com.example.projetdestage.Twilio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpValidationRequest {

	private String phoneNumber;
	private String otpNumber;
	private  String Username;

}
