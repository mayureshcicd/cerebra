package com.cerebra.fileprocessor.service;

public interface EmailService {

     void sendEmailLoginCredentials(String name, String email, String password, String sendTo, String message);

}
