package com.angel.antezana.emailverificationdemo.user;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.angel.antezana.emailverificationdemo.exception.UserAlreadyExistsException;
import com.angel.antezana.emailverificationdemo.registration.RegistrationRequest;
import com.angel.antezana.emailverificationdemo.registration.token.VerificationToken;
import com.angel.antezana.emailverificationdemo.registration.token.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenRepository tokenRepository;
    @Override
    public List<User> getUsers() {
      return userRepository.findAll();
    }

    @Override
    public String validateToken(String verificationToken) {
        VerificationToken token = tokenRepository.findByToken(verificationToken).get();
        
        if(token == null){
            return "Invalid verfication token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if((token.getExpirationTime().getTime() - calendar.getTime().getTime())<=0){
            return "Verification link already expired," +
            " Please, click the link below to receive a new verification link";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid";
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        
        Optional<User> usuario = userRepository.findByEmail(request.email());
        if(usuario.isPresent()){
            throw new UserAlreadyExistsException("User with email" + request.email()+" already exists");
        }

        var newUser = new User();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        return userRepository.save(newUser);
        //VIDEO 20:26
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User user, String verificationToken) {
       
        var verificationToken2 = new VerificationToken(verificationToken,user);
        tokenRepository.save(verificationToken2);
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = tokenRepository.findByToken(oldToken).get();
        VerificationToken verificationTokenTime = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpirationTime(verificationTokenTime.getTokenExpirationTime());
        return tokenRepository.save(verificationToken);
    }
    

}
