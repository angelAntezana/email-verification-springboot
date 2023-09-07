package com.angel.antezana.emailverificationdemo.registration;

public record RegistrationRequest(
    String firstName,
     String lastName,
     String email,
     String password,
     String role) {
  
}
