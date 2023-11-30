 package com.example.projetdestage.dto;

import com.example.projetdestage.Twilio.OtpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpResponseDto {
    private OtpStatus status;
    private String message;


}
