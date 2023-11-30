package com.example.projetdestage.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;
@Data
public class Response {
    private Date date;
    private String message;
    private HttpStatus status;
}
