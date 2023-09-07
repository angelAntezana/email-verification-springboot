package com.angel.antezana.emailverificationdemo.event.listener;

import java.util.UUID;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.angel.antezana.emailverificationdemo.event.RegistrationCompleteEvent;
import com.angel.antezana.emailverificationdemo.user.User;
import com.angel.antezana.emailverificationdemo.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent>{

    private final UserService userService; 

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
      //1. Get the newly registered user
        User user = event.getUser();
      //2. Create a verification token for the user
      String verificationToken = UUID.randomUUID().toString();
      //3. Save the verification token for the user
      userService.saveUserVerificationToken(user,verificationToken);
      //4. Build the verification URL to be sent to the user
      String url = event.getApplicationUrl()+"/api/v1/register/verifyEmail?token="+verificationToken;
      //5. Send the email
        log.info("Click the link to verify your registration: {}",url);


    }
    
}
