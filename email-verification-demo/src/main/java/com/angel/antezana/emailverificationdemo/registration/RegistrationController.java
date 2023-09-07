package com.angel.antezana.emailverificationdemo.registration;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angel.antezana.emailverificationdemo.event.RegistrationCompleteEvent;
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

    @PostMapping
    private String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user = userService.registerUser(registrationRequest);
        //publich registration event //VIDEO 35:17
        publisher.publishEvent(new RegistrationCompleteEvent(user,  applicationUrl(request)));

        return "Usuario registrado con exito. Please,check your email for to complete your registration";
    }

    

    private String applicationUrl(HttpServletRequest request) {
        
        return "http://"+request.getServerName()+":"+ request.getServerPort()+ request.getContextPath();
    }
}
