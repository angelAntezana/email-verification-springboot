package com.angel.antezana.emailverificationdemo.event.listener;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.angel.antezana.emailverificationdemo.event.RegistrationCompleteEvent;
import com.angel.antezana.emailverificationdemo.user.User;
import com.angel.antezana.emailverificationdemo.user.UserService;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent>{

    private final UserService userService; 
    private static final String TEXT_HTML_ENCODING = "text/html";
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender javaMailSender;
    public static final String MESSAGE_VERIFICATION = "New User Account Verification";

    public static final String EMAIL_TEMPLATE = "emailtemplate";

    private final TemplateEngine templateEngine;

    public static final String UTF_8_ENCODING = "UTF-8";
    private  User theUser;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
      //1. Get the newly registered user
        theUser = event.getUser();
      //2. Create a verification token for the user
      String verificationToken = UUID.randomUUID().toString();
      //3. Save the verification token for the user
      userService.saveUserVerificationToken(theUser,verificationToken);
      //4. Build the verification URL to be sent to the user
      String url = event.getApplicationUrl()+"/api/v1/register/verifyEmail?token="+verificationToken;
      //5. Send the email
        log.info("Click the link to verify your registration: {}",url);
        sendVerificationEmail(url);
    }

    public void sendVerificationEmail(String url){
      try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(MESSAGE_VERIFICATION);
            helper.setFrom(fromEmail);
            helper.setTo(theUser.getEmail());
            //helper.setText(EmailUtils.getEmailMessage(name, host, confirmacion));
            Context context = new Context();
            context.setVariables(Map.of("name",theUser.getFirstName(),"url",url));
            String text = templateEngine.process(EMAIL_TEMPLATE,context);
            //ADD attachments

            //Add HTML email body
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text,TEXT_HTML_ENCODING);
            mimeMultipart.addBodyPart(messageBodyPart);

            //Add images to the email body
            messageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource("C:\\Users\\angel\\OneDrive\\Im\u00E1genes\\Saved Pictures\\mar.jpg");
            messageBodyPart.setDataHandler(new DataHandler(dataSource));
            messageBodyPart.setHeader("Content-ID", "image");
            mimeMultipart.addBodyPart(messageBodyPart);

            message.setContent(mimeMultipart); 
            javaMailSender.send(message);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    private MimeMessage getMimeMessage() {
      return javaMailSender.createMimeMessage();
  }
    
}
