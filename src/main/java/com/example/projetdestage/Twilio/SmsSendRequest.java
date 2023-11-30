package com.example.projetdestage.Twilio;

import lombok.Data;

@Data
public class SmsSendRequest {
    private String distinationSMSNumber;
    private  String smsMessage;
}
