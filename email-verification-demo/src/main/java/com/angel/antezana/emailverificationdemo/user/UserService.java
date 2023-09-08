package com.angel.antezana.emailverificationdemo.user;

import java.util.List;
import java.util.Optional;

import com.angel.antezana.emailverificationdemo.registration.RegistrationRequest;

public interface UserService {
    
    List<User> getUsers();
    User registerUser(RegistrationRequest request);
    Optional<User> findByEmail(String email);
    void saveUserVerificationToken(User user, String verificationToken);
    String validateToken(String verificationToken);
}
