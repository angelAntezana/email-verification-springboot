package com.angel.antezana.emailverificationdemo.registration;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.angel.antezana.emailverificationdemo.event.RegistrationCompleteEvent;
import com.angel.antezana.emailverificationdemo.registration.token.VerificationToken;
import com.angel.antezana.emailverificationdemo.registration.token.VerificationTokenRepository;
import com.angel.antezana.emailverificationdemo.user.User;
import com.angel.antezana.emailverificationdemo.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/register")
public class RegistrationController {
    
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository verificationTokenRepository;

    @PostMapping
    private String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user = userService.registerUser(registrationRequest);
        publisher.publishEvent(new RegistrationCompleteEvent(user,  applicationUrl(request)));

        return "Usuario registrado con exito. Please,check your email to complete your registration";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).get();
        if(verificationToken.getUser().isEnabled()){
            return "This account has already been verified,please,login.";
        }

        String verificationResult = userService.validateToken(token);
       if(verificationResult.equalsIgnoreCase("Valid")){
            return "Email verified successfully. Now you can login to your account.";
       }
       return "Invalid verification";
    }


    private String applicationUrl(HttpServletRequest request) {
        
        return "http://"+request.getServerName()+":"+ request.getServerPort()+ request.getContextPath();
    }
}
