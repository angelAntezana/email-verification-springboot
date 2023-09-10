package com.angel.antezana.emailverificationdemo.registration;

import java.io.UnsupportedEncodingException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.angel.antezana.emailverificationdemo.event.RegistrationCompleteEvent;
import com.angel.antezana.emailverificationdemo.event.listener.RegistrationCompleteEventListener;
import com.angel.antezana.emailverificationdemo.registration.token.VerificationToken;
import com.angel.antezana.emailverificationdemo.registration.token.VerificationTokenRepository;
import com.angel.antezana.emailverificationdemo.user.User;
import com.angel.antezana.emailverificationdemo.user.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/register")
public class RegistrationController {
    
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RegistrationCompleteEventListener eventListener;
    private final HttpServletRequest servletRequest;

    @PostMapping
    private String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user = userService.registerUser(registrationRequest);
        publisher.publishEvent(new RegistrationCompleteEvent(user,  applicationUrl(request)));

        return "Usuario registrado con exito. Please,check your email to complete your registration";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){

        String url = applicationUrl(servletRequest)+"/api/v1/register/resend-verification-token?token="+token;
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).get();
        if(verificationToken.getUser().isEnabled()){
            return "This account has already been verified,please,login.";
        }

        String verificationResult = userService.validateToken(token);
       if(verificationResult.equalsIgnoreCase("Valid")){
            return "Email verified successfully. Now you can login to your account.";
       }
       log.info("Click the link to verify your registration: {}",url);
       return "Invalid verification link, <a href=\""+url+"\">Get a new verification link. </a>";
    }

    @GetMapping("/resend-verification-token")
    public String resendVerificationToken(@RequestParam("token")String oldToken, final HttpServletRequest request)throws MessagingException, UnsupportedEncodingException{

        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User theUser = verificationToken.getUser();
        resendVerificationTokenEmail(theUser,applicationUrl(request),verificationToken);
        return "A new verification link has been sent to your email.please,check toyour account.";
    }

    private void resendVerificationTokenEmail(User theUser, String applicationUrl,
            VerificationToken verificationToken)throws MessagingException, UnsupportedEncodingException {

        String url = applicationUrl+"/api/v1/register/verifyEmail?token="+verificationToken.getToken();
        eventListener.sendVerificationEmail(url);
    }

    private String applicationUrl(HttpServletRequest request) {
        
        return "http://"+request.getServerName()+":"+ request.getServerPort()+ request.getContextPath();
    }
}
